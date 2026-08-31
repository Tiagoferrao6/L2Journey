package com.l2journey.gameserver.managers;

import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.logging.Logger;

import com.l2journey.Config;
import com.l2journey.commons.threads.ThreadPool;
import com.l2journey.gameserver.dao.FakePlayerDAO;
import com.l2journey.gameserver.data.xml.FakeShopData;
import com.l2journey.gameserver.model.World;
import com.l2journey.gameserver.model.actor.Player;
import com.l2journey.gameserver.model.actor.fakeplayer.FakeHunterAI;
import com.l2journey.gameserver.model.actor.fakeplayer.FakePlayerProfile;
import com.l2journey.gameserver.model.actor.fakeplayer.FakeShop;
import com.l2journey.gameserver.model.actor.fakeplayer.FakeTraderAI;
import com.l2journey.gameserver.model.actor.holders.fakeplayer.FakeShopHolder;
import com.l2journey.gameserver.model.events.Containers;
import com.l2journey.gameserver.model.events.EventType;
import com.l2journey.gameserver.model.events.ListenersContainer;
import com.l2journey.gameserver.model.events.holders.actor.creature.OnCreatureZoneEnter;
import com.l2journey.gameserver.model.events.holders.actor.creature.OnCreatureZoneExit;
import com.l2journey.gameserver.model.events.listeners.ConsumerEventListener;
import com.l2journey.gameserver.model.zone.ZoneType;
import com.l2journey.gameserver.model.zone.type.TownZone;

/**
 * Core Manager for Fake Players in Gludio, supporting schedule management,
 * Zone-based activation listener, and conditional spawning/despawning.
 */
public class FakePlayerManager
{
	private static final Logger LOGGER = Logger.getLogger(FakePlayerManager.class.getName());

	private final Map<String, FakeShop> _activeShops = new ConcurrentHashMap<>();
	private final Map<Integer, FakeTraderAI> _activeTraders = new ConcurrentHashMap<>();
	private final Map<Integer, FakeHunterAI> _activeHunters = new ConcurrentHashMap<>();

	private final AtomicInteger _realPlayersInGludio = new AtomicInteger(0);
	private ScheduledFuture<?> _scheduleCheckTask;
	private boolean _gludioActive = false;

	protected FakePlayerManager()
	{
		if (!Config.FAKE_PLAYERS_ENABLED)
		{
			LOGGER.info(getClass().getSimpleName() + ": Fake Players system is disabled.");
			return;
		}

		LOGGER.info(getClass().getSimpleName() + ": Initializing Modular Fake Player Manager for Gludio.");

		if (Config.FAKE_SHOPS_ENABLED)
		{
			initFakeShops();
		}

		setupGludioZoneListener();
		startScheduleManager();

		if (Config.FAKE_PLAYER_ALWAYS_ACTIVE)
		{
			_gludioActive = true;
			evaluateSchedules();
		}
	}

	public void initFakeShops()
	{
		LOGGER.info(getClass().getSimpleName() + ": Initializing legacy FakeShops module...");
		FakeShopData.getInstance(); // Loads XML configs

		for (FakeShopHolder holder : FakeShopData.getInstance().getFakeShops())
		{
			final FakeShop shop = new FakeShop(holder);
			_activeShops.put(holder.getName().toLowerCase(), shop);
			shop.spawn();
		}

		LOGGER.info(getClass().getSimpleName() + ": Activated " + _activeShops.size() + " legacy FakeShops.");
	}



	private void setupGludioZoneListener()
	{
		LOGGER.info(getClass().getSimpleName() + ": Setting up Zone Listener for Gludio region...");

		ZoneType gludioZone = ZoneManager.getInstance().getZoneByName("Town of Gludio");
		if (gludioZone == null)
		{
			gludioZone = ZoneManager.getInstance().getZoneByName("Gludio");
		}

		if (gludioZone != null)
		{
			final ZoneType zone = gludioZone;
			Consumer<OnCreatureZoneEnter> onEnter = event ->
			{
				if (event.getCreature() instanceof Player)
				{
					Player player = (Player) event.getCreature();
					if (!player.isFakePlayer())
					{
						int count = _realPlayersInGludio.incrementAndGet();
						LOGGER.info("FakePlayerManager: Real player entered Gludio zone. Total real players: " + count);
						onRealPlayerZoneStateChange();
					}
				}
			};

			Consumer<OnCreatureZoneExit> onExit = event ->
			{
				if (event.getCreature() instanceof Player)
				{
					Player player = (Player) event.getCreature();
					if (!player.isFakePlayer())
					{
						int count = _realPlayersInGludio.decrementAndGet();
						if (count < 0)
						{
							_realPlayersInGludio.set(0);
							count = 0;
						}
						LOGGER.info("FakePlayerManager: Real player exited Gludio zone. Remaining real players: " + count);
						onRealPlayerZoneStateChange();
					}
				}
			};

			zone.addListener(new ConsumerEventListener(zone, EventType.ON_CREATURE_ZONE_ENTER, onEnter, this));
			zone.addListener(new ConsumerEventListener(zone, EventType.ON_CREATURE_ZONE_EXIT, onExit, this));
		}
		else
		{
			LOGGER.warning(getClass().getSimpleName() + ": Gludio Zone not found in ZoneManager. Listener registered globally.");
		}
	}

	private synchronized void onRealPlayerZoneStateChange()
	{
		if (Config.FAKE_PLAYER_ALWAYS_ACTIVE)
		{
			_gludioActive = true;
			spawnGludioBotsForActiveSchedule();
			return;
		}

		boolean hasPlayers = _realPlayersInGludio.get() > 0;
		if (hasPlayers && !_gludioActive)
		{
			_gludioActive = true;
			spawnGludioBotsForActiveSchedule();
		}
		else if (!hasPlayers && _gludioActive)
		{
			_gludioActive = false;
			despawnGludioBots();
		}
	}

	private void startScheduleManager()
	{
		// Check schedule every 60 seconds
		_scheduleCheckTask = ThreadPool.scheduleAtFixedRate(this::evaluateSchedules, 60000, 60000);
	}

	public synchronized void evaluateSchedules()
	{
		if (!Config.FAKE_PLAYER_ALWAYS_ACTIVE && !_gludioActive && _realPlayersInGludio.get() <= 0)
		{
			return;
		}

		spawnGludioBotsForActiveSchedule();
	}

	private boolean isScheduleActive(String shift)
	{
		if (Config.FAKE_PLAYER_ALWAYS_ACTIVE)
		{
			return true;
		}

		try
		{
			LocalTime now = LocalTime.now(ZoneId.of("America/Sao_Paulo"));
			LocalTime start = LocalTime.parse(Config.FAKE_PLAYER_SHIFT_START_HOUR);
			LocalTime end = LocalTime.parse(Config.FAKE_PLAYER_SHIFT_END_HOUR);

			if (start.isBefore(end))
			{
				return !now.isBefore(start) && !now.isAfter(end);
			}
			else
			{
				return !now.isBefore(start) || !now.isAfter(end);
			}
		}
		catch (Exception e)
		{
			int hour = Calendar.getInstance(TimeZone.getTimeZone("America/Sao_Paulo")).get(Calendar.HOUR_OF_DAY);
			if ("NIGHT".equalsIgnoreCase(shift))
			{
				return hour >= 18 || hour < 6;
			}
			return hour >= 6 && hour < 18;
		}
	}

	public synchronized void spawnGludioBotsForActiveSchedule()
	{
		List<FakePlayerProfile> profiles = FakePlayerDAO.getInstance().loadProfilesByZone("GLUDIO");
		int spawnedTraders = 0;
		int spawnedHunters = 0;

		for (FakePlayerProfile profile : profiles)
		{
			if (isScheduleActive(profile.getShift()))
			{
				if ("HUNTER".equalsIgnoreCase(profile.getBotType()))
				{
					if (spawnedHunters < 30 && !_activeHunters.containsKey(profile.getFakeId()))
					{
						FakeHunterAI hunter = FakeHunterAI.spawnHunter(profile);
						if (hunter != null)
						{
							_activeHunters.put(profile.getFakeId(), hunter);
							spawnedHunters++;
						}
					}
				}
			}
			else
			{
				// Shift ended - despawn if active
				if ("HUNTER".equalsIgnoreCase(profile.getBotType()))
				{
					FakeHunterAI hunter = _activeHunters.remove(profile.getFakeId());
					if (hunter != null)
					{
						hunter.despawn();
					}
				}
			}
		}

		LOGGER.info("FakePlayerManager: Currently active in Gludio -> Traders (XML): " + _activeShops.size() + ", Hunters (SQL): " + _activeHunters.size());
	}

	public synchronized void despawnGludioBots()
	{
		LOGGER.info("FakePlayerManager: No real players in Gludio. Despawning all Gludio fake players...");
		for (FakeTraderAI trader : _activeTraders.values())
		{
			trader.despawn();
		}
		_activeTraders.clear();

		for (FakeHunterAI hunter : _activeHunters.values())
		{
			hunter.despawn();
		}
		_activeHunters.clear();
	}

	public FakeShop getFakeShop(String name)
	{
		return _activeShops.get(name.toLowerCase());
	}

	public static FakePlayerManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}

	private static class SingletonHolder
	{
		protected static final FakePlayerManager INSTANCE = new FakePlayerManager();
	}
}
