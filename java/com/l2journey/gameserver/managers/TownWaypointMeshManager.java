package com.l2journey.gameserver.managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import com.l2journey.commons.threads.ThreadPool;
import com.l2journey.commons.util.Rnd;
import com.l2journey.gameserver.ai.Intention;
import com.l2journey.gameserver.model.Location;
import com.l2journey.gameserver.model.actor.instance.FakePlayer;

/**
 * Manages urban node-based waypoint graphs and pathfinding navigation for towns (Gludio, Giran).
 * Combines node-to-node graph routing with stuck-detection fallback logic.
 */
public class TownWaypointMeshManager
{
	private static final Logger LOGGER = Logger.getLogger(TownWaypointMeshManager.class.getName());

	public enum TownLocation
	{
		GLUDIO_GK(-14780, 123800, -3120),
		GLUDIO_SQUARE(-14347, 123622, -3120),
		GLUDIO_GROCERY(-13950, 123200, -3120),
		GLUDIO_WEAPON_SHOP(-14100, 124100, -3120),
		GLUDIO_BLACKSMITH(-13600, 123700, -3120),
		GLUDIO_CHURCH(-12600, 122500, -3120);

		private final Location _location;

		TownLocation(int x, int y, int z)
		{
			_location = new Location(x, y, z);
		}

		public Location getLocation()
		{
			return _location;
		}
	}

	private final Map<String, List<Location>> _townRoutes = new HashMap<>();
	private final Set<Integer> _activeNavigatingBots = ConcurrentHashMap.newKeySet();

	protected TownWaypointMeshManager()
	{
		initGludioRoutes();
		LOGGER.info("TownWaypointMeshManager: Initialized Gludio urban waypoint mesh routes.");
	}

	public static TownWaypointMeshManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}

	private static class SingletonHolder
	{
		protected static final TownWaypointMeshManager INSTANCE = new TownWaypointMeshManager();
	}

	private void initGludioRoutes()
	{
		// GK to Grocery Route
		List<Location> gkToGrocery = new ArrayList<>();
		gkToGrocery.add(TownLocation.GLUDIO_GK.getLocation());
		gkToGrocery.add(TownLocation.GLUDIO_SQUARE.getLocation());
		gkToGrocery.add(TownLocation.GLUDIO_GROCERY.getLocation());
		_townRoutes.put("GLUDIO_GK_TO_GROCERY", gkToGrocery);

		// GK to Weaponsmith Route
		List<Location> gkToWeapon = new ArrayList<>();
		gkToWeapon.add(TownLocation.GLUDIO_GK.getLocation());
		gkToWeapon.add(TownLocation.GLUDIO_SQUARE.getLocation());
		gkToWeapon.add(TownLocation.GLUDIO_WEAPON_SHOP.getLocation());
		_townRoutes.put("GLUDIO_GK_TO_WEAPON", gkToWeapon);

		// Grocery to GK Route
		List<Location> groceryToGk = new ArrayList<>();
		groceryToGk.add(TownLocation.GLUDIO_GROCERY.getLocation());
		groceryToGk.add(TownLocation.GLUDIO_SQUARE.getLocation());
		groceryToGk.add(TownLocation.GLUDIO_GK.getLocation());
		_townRoutes.put("GLUDIO_GROCERY_TO_GK", groceryToGk);
	}

	public List<Location> getRoute(String routeKey)
	{
		return _townRoutes.getOrDefault(routeKey, new ArrayList<>());
	}

	/**
	 * Checks whether a bot is currently navigating a town route.
	 */
	public boolean isBotNavigating(FakePlayer bot)
	{
		return bot != null && _activeNavigatingBots.contains(bot.getObjectId());
	}

	/**
	 * Navigates a FakePlayer step-by-step along a town route with stuck detection fallback.
	 */
	public void navigateBotAlongRoute(FakePlayer bot, String routeKey, Runnable onComplete)
	{
		if (bot == null || !bot.isOnline()) return;

		if (!_activeNavigatingBots.add(bot.getObjectId()))
		{
			LOGGER.info("TownWaypointMeshManager: Bot " + bot.getName() + " já está navegando uma rota urbana. Ignorando chamada reentrante.");
			LLMCompanionManager.getInstance().getBotTrace(bot).recordFailure("TOWN_NAVIGATION", "Tentativa de rota reentrante ignorada.");
			return;
		}

		LLMCompanionManager.getInstance().getBotTrace(bot).addLog("Iniciou rota urbana: " + routeKey);

		List<Location> waypoints = getRoute(routeKey);
		if (waypoints.isEmpty())
		{
			_activeNavigatingBots.remove(bot.getObjectId());
			if (onComplete != null) onComplete.run();
			return;
		}

		executeWaypointStep(bot, waypoints, 0, onComplete);
	}

	private void executeWaypointStep(FakePlayer bot, List<Location> waypoints, int index, Runnable onComplete)
	{
		if (index >= waypoints.size())
		{
			_activeNavigatingBots.remove(bot.getObjectId());
			if (onComplete != null) onComplete.run();
			return;
		}

		Location nextLoc = waypoints.get(index);
		if (bot.isInsideRadius2D(nextLoc, 100))
		{
			executeWaypointStep(bot, waypoints, index + 1, onComplete);
			return;
		}

		int startX = bot.getX();
		int startY = bot.getY();

		bot.getAI().setIntention(Intention.MOVE_TO, nextLoc);

		// Schedule check in 2.5 seconds
		ThreadPool.schedule(() -> {
			if (!bot.isOnline())
			{
				_activeNavigatingBots.remove(bot.getObjectId());
				return;
			}

			if (bot.isInsideRadius2D(nextLoc, 150))
			{
				executeWaypointStep(bot, waypoints, index + 1, onComplete);
			}
			else
			{
				int deltaX = Math.abs(bot.getX() - startX);
				int deltaY = Math.abs(bot.getY() - startY);

				if (deltaX < 30 && deltaY < 30)
				{
					// Micro-evasion lateral before node fallback
					int evadeX = bot.getX() + Rnd.get(-80, 80);
					int evadeY = bot.getY() + Rnd.get(-80, 80);
					bot.getAI().setIntention(Intention.MOVE_TO, new Location(evadeX, evadeY, bot.getZ()));
					LOGGER.warning("TownWaypointMeshManager: " + bot.getName() + " estagnado (deltaX=" + deltaX + ", deltaY=" + deltaY + "). Executando micro-evasão lateral.");

					ThreadPool.schedule(() -> {
						if (!bot.isOnline())
						{
							_activeNavigatingBots.remove(bot.getObjectId());
							return;
						}
						bot.teleToLocation(nextLoc);
						LOGGER.warning("TownWaypointMeshManager: " + bot.getName() + " preso navegando para " + nextLoc + ". Executando node step fallback.");
						executeWaypointStep(bot, waypoints, index + 1, onComplete);
					}, 1000);
				}
				else
				{
					// Stuck fallback: teleport directly to waypoint node
					bot.teleToLocation(nextLoc);
					LOGGER.warning("TownWaypointMeshManager: " + bot.getName() + " preso navegando para " + nextLoc + ". Executando node step fallback.");
					executeWaypointStep(bot, waypoints, index + 1, onComplete);
				}
			}
		}, 2500);
	}
}

