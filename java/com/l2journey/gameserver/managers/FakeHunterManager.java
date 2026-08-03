package com.l2journey.gameserver.managers;

import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

import com.l2journey.commons.threads.ThreadPool;
import com.l2journey.commons.util.Rnd;
import com.l2journey.gameserver.ai.Intention;
import com.l2journey.gameserver.data.xml.SkillData;
import com.l2journey.gameserver.data.xml.impl.FakeHunterProfilesParser;
import com.l2journey.gameserver.data.xml.impl.FakeHunterWaypointsParser;
import com.l2journey.gameserver.data.xml.impl.FakePlayerEquipmentData;
import com.l2journey.gameserver.model.Location;
import com.l2journey.gameserver.model.World;
import com.l2journey.gameserver.model.actor.Attackable;
import com.l2journey.gameserver.model.actor.Creature;
import com.l2journey.gameserver.model.actor.Player;
import com.l2journey.gameserver.model.actor.dna.HunterDNA;
import com.l2journey.gameserver.model.actor.dna.HunterProfile;
import com.l2journey.gameserver.model.actor.dna.HunterRoute;
import com.l2journey.gameserver.model.actor.instance.FakePlayer;
import com.l2journey.gameserver.model.groups.Party;
import com.l2journey.gameserver.model.groups.PartyDistributionType;
import com.l2journey.gameserver.model.item.instance.Item;
import com.l2journey.gameserver.model.skill.Skill;
import com.l2journey.gameserver.model.fake.ai.bt.BTActionInteractBypass;
import com.l2journey.gameserver.model.fake.ai.bt.BTActionWalkToNpc;
import com.l2journey.gameserver.model.fake.ai.bt.BTStatus;

/**
 * Orchestrates the lifecycle, route navigation, and combat AI ticks of Fake Hunters.
 * Supports hierarchical waypoint routes, auto-looting, anti-KS targeting, and death respawn loops.
 */
public class FakeHunterManager
{
	private static final Logger LOGGER = Logger.getLogger(FakeHunterManager.class.getName());

	public static final int GLUDIO_TOWN_X = -14347;
	public static final int GLUDIO_TOWN_Y = 123622;
	public static final int GLUDIO_GK_X = -14780;
	public static final int GLUDIO_GK_Y = 123800;
	public static final int GLUDIO_GK_Z = -3120;
	public static final int SAFE_ZONE_LEASH_RADIUS = 1200;

	private final List<FakePlayer> _activeHunters = new CopyOnWriteArrayList<>();
	private final List<String> _reservedNames = new CopyOnWriteArrayList<>();
	private final Map<String, Boolean> _zoneSleepStates = new ConcurrentHashMap<>();

	protected FakeHunterManager()
	{
		// Default zones awake
		_zoneSleepStates.put("GLUDIO", false);
		_zoneSleepStates.put("DION", false);
		_zoneSleepStates.put("GIRAN", false);

		// Schedule AI Tick every 2s
		ThreadPool.scheduleAtFixedRate(new HunterAITick(), 2000, 2000);
		// Schedule Shift Jitter Tick every 60s
		ThreadPool.scheduleAtFixedRate(new ShiftTick(), 60000, 60000);
	}

	public void addHunter(FakePlayer hunter)
	{
		if (hunter != null && !_activeHunters.contains(hunter))
		{
			_activeHunters.add(hunter);
		}
	}

	public void removeHunter(FakePlayer hunter)
	{
		if (hunter != null)
		{
			_activeHunters.remove(hunter);
		}
	}

	public List<FakePlayer> getActiveHunters()
	{
		return _activeHunters;
	}

	public List<FakePlayer> getHunters()
	{
		return _activeHunters;
	}

	public void addReservedName(String name)
	{
		if (name != null && !_reservedNames.contains(name.toLowerCase()))
		{
			_reservedNames.add(name.toLowerCase());
		}
	}

	public boolean isNameReserved(String name)
	{
		return name != null && _reservedNames.contains(name.toLowerCase());
	}

	public void setZoneSleeping(String zoneName, boolean sleeping)
	{
		_zoneSleepStates.put(zoneName.toUpperCase(), sleeping);
		LOGGER.info("FakeHunterManager: Zone " + zoneName + " sleep state set to: " + sleeping);
	}

	public boolean isZoneSleeping(String zoneName)
	{
		return _zoneSleepStates.getOrDefault(zoneName.toUpperCase(), false);
	}

	/**
	 * Main AI decision tick for combat bots.
	 */
	private class HunterAITick implements Runnable
	{
		@Override
		public void run()
		{
			ensureDespairParty();

			for (FakePlayer hunter : _activeHunters)
			{
				if (hunter == null || !hunter.isOnline())
				{
					continue;
				}

				// Death Respawn & Return Loop Check
				if (hunter.isDead() || hunter.isAlikeDead())
				{
					processDeathRespawn(hunter);
					continue;
				}

				boolean isDespairBot = hunter.getName().startsWith("Despair");
				if (isZoneSleeping("GLUDIO") && !isDespairBot)
				{
					continue;
				}

				final HunterDNA dna = hunter.getHunterDNA();

				// Town Gatekeeper Dispatch via Behavior Tree (BTActionWalkToNpc & BTActionInteractBypass)
				double distToTown = hunter.calculateDistance2D(GLUDIO_TOWN_X, GLUDIO_TOWN_Y, 0);
				if (distToTown < 2500)
				{
					BTActionWalkToNpc walkToGk = new BTActionWalkToNpc(GLUDIO_GK_X, GLUDIO_GK_Y, GLUDIO_GK_Z, 180.0);
					BTStatus walkStatus = walkToGk.execute(hunter);
					if (walkStatus == BTStatus.SUCCESS)
					{
						BTActionInteractBypass interactBypass = new BTActionInteractBypass("goto 50012", 400);
						interactBypass.execute(hunter);
					}
					continue;
				}

				// Leash Check - Avoid pulling monsters into Gludio safe town
				if (distToTown < SAFE_ZONE_LEASH_RADIUS)
				{
					hunter.abortAttack();
					hunter.abortCast();
					hunter.setTarget(null);
					LOGGER.info("FakeHunter [" + hunter.getName() + "] triggered Safe Zone Leash (Retreating from Town).");
					continue;
				}

				// Preservation & Safety Check (HP < 20%)
				double currentHpPercent = (hunter.getCurrentHp() / hunter.getMaxHp()) * 100.0;
				if (currentHpPercent < 20.0 && dna != null && dna.getPreservation() > 50)
				{
					if (Rnd.get(100) < dna.getPreservation())
					{
						hunter.abortAttack();
						hunter.abortCast();
						LOGGER.info("FakeHunter [" + hunter.getName() + "] triggered safety flee.");
						continue;
					}
				}

				// Auto Looting Check
				if (processLootPickup(hunter))
				{
					continue;
				}

				// Waypoint Route Navigation
				processWaypointNavigation(hunter);

				// Process specific Despair Bot AI
				if (isDespairBot)
				{
					processDespairBotAI(hunter);
				}
			}
		}

		private void processDeathRespawn(FakePlayer hunter)
		{
			HunterProfile profile = hunter.getHunterProfile();
			int delaySec = (profile != null) ? profile.getTownReturnDelay() : 20;

			// Check death duration
			if ((System.currentTimeMillis() - hunter.getSpawnTime()) > (delaySec * 1000L))
			{
				hunter.doRevive();
				hunter.teleToLocation(GLUDIO_TOWN_X + Rnd.get(-100, 100), GLUDIO_TOWN_Y + Rnd.get(-100, 100), GLUDIO_GK_Z);
				hunter.setCurrentWaypointIndex(0);
				LOGGER.info("FakeHunter [" + hunter.getName() + "] revived after " + delaySec + "s and returned to town.");
			}
		}

		private boolean processLootPickup(FakePlayer hunter)
		{
			HunterProfile profile = hunter.getHunterProfile();
			if (profile == null || !profile.isPickupItems() || hunter.isMoving() || hunter.isAttackingNow() || hunter.isCastingNow())
			{
				return false;
			}

			List<Item> items = World.getInstance().getVisibleObjectsInRange(hunter, Item.class, 500);
			for (Item item : items)
			{
				if (item != null && item.isSpawned())
				{
					hunter.getAI().setIntention(Intention.PICK_UP, item);
					return true;
				}
			}
			return false;
		}

		private void processWaypointNavigation(FakePlayer hunter)
		{
			HunterProfile profile = hunter.getHunterProfile();
			if (profile == null || profile.getAssignedRoutes().isEmpty())
			{
				return;
			}

			String activeRouteId = hunter.getActiveRouteId();
			if (activeRouteId == null)
			{
				activeRouteId = profile.getAssignedRoutes().get(0);
				hunter.setActiveRouteId(activeRouteId);
				hunter.setCurrentWaypointIndex(0);
			}

			HunterRoute route = FakeHunterWaypointsParser.getInstance().getRoute(activeRouteId);
			if (route == null || route.getNodes().isEmpty())
			{
				return;
			}

			int index = hunter.getCurrentWaypointIndex();
			if (index >= route.getNodes().size())
			{
				index = 0;
				hunter.setCurrentWaypointIndex(0);
			}

			HunterRoute.WaypointNode currentNode = route.getNodes().get(index);
			double distToNode = hunter.calculateDistance2D(currentNode.getX(), currentNode.getY(), 0);

			if (distToNode < 120)
			{
				index = (index + 1) % route.getNodes().size();
				hunter.setCurrentWaypointIndex(index);
				currentNode = route.getNodes().get(index);
			}

			if (!hunter.isMoving() && !hunter.isAttackingNow() && !hunter.isCastingNow())
			{
				Location targetLoc = currentNode.getRandomizedLocation();
				hunter.getAI().setIntention(Intention.MOVE_TO, targetLoc);
			}
		}

		private void ensureDespairParty()
		{
			FakePlayer tank = null;
			FakePlayer healer = null;
			FakePlayer dagger = null;

			for (FakePlayer h : _activeHunters)
			{
				if (h != null && !h.isDead())
				{
					if ("DespairTank".equalsIgnoreCase(h.getName()))
					{
						tank = h;
					}
					else if ("DespairHealer".equalsIgnoreCase(h.getName()))
					{
						healer = h;
					}
					else if ("DespairDagger".equalsIgnoreCase(h.getName()))
					{
						dagger = h;
					}
				}
			}

			if (tank != null && (tank.getParty() == null))
			{
				Party party = new Party(tank, PartyDistributionType.FINDERS_KEEPERS);
				tank.setParty(party);
				if (healer != null && healer.getParty() == null)
				{
					party.addPartyMember(healer);
				}
				if (dagger != null && dagger.getParty() == null)
				{
					party.addPartyMember(dagger);
				}
			}
		}

		private void processDespairBotAI(FakePlayer hunter)
		{
			String name = hunter.getName();
			if ("DespairArcher".equalsIgnoreCase(name))
			{
				Attackable target = findTargetMob(hunter, 1500, false);
				if (target != null)
				{
					double dist = hunter.calculateDistance2D(target);
					if (dist < 150)
					{
						int backX = hunter.getX() + (hunter.getX() - target.getX());
						int backY = hunter.getY() + (hunter.getY() - target.getY());
						hunter.getAI().setIntention(Intention.MOVE_TO, new Location(backX, backY, hunter.getZ()));
					}
					else if (hunter.getTarget() != target || !hunter.isAttackingNow())
					{
						hunter.setTarget(target);
						hunter.doAttack(target);
					}
				}
			}
			else if ("DespairTank".equalsIgnoreCase(name))
			{
				Attackable target = findTargetMob(hunter, 1000, false);
				if (target != null)
				{
					hunter.setTarget(target);
					if (Rnd.get(100) < 40)
					{
						castSkill(hunter, target, Rnd.nextBoolean() ? 28 : 18);
					}
					if (!hunter.isAttackingNow())
					{
						hunter.doAttack(target);
					}
				}
			}
			else if ("DespairHealer".equalsIgnoreCase(name))
			{
				Party party = hunter.getParty();
				Player healTarget = null;
				double lowestHp = 70.0;

				if (party != null)
				{
					for (Player member : party.getMembers())
					{
						if (member != null && !member.isDead())
						{
							double hp = (member.getCurrentHp() / member.getMaxHp()) * 100.0;
							if (hp < lowestHp)
							{
								lowestHp = hp;
								healTarget = member;
							}
						}
					}
				}

				if (healTarget != null)
				{
					int healSkillId = (lowestHp < 35.0) ? 1015 : 1217;
					castSkill(hunter, healTarget, healSkillId);
				}
			}
			else if ("DespairDagger".equalsIgnoreCase(name))
			{
				Attackable target = findTargetMob(hunter, 1000, false);
				if (target != null)
				{
					hunter.setTarget(target);
					if (Rnd.get(100) < 50)
					{
						castSkill(hunter, target, Rnd.nextBoolean() ? 30 : 263);
					}
					if (!hunter.isAttackingNow())
					{
						hunter.doAttack(target);
					}
				}
			}
			else if ("DespairSpoil".equalsIgnoreCase(name))
			{
				Attackable deadSpoiled = findTargetMob(hunter, 600, true);
				if (deadSpoiled != null)
				{
					castSkill(hunter, deadSpoiled, 42);
					return;
				}

				Attackable target = findTargetMob(hunter, 1000, false);
				if (target != null)
				{
					hunter.setTarget(target);
					if (!target.isSpoiled() && Rnd.get(100) < 60)
					{
						castSkill(hunter, target, 254);
					}
					if (!hunter.isAttackingNow())
					{
						hunter.doAttack(target);
					}
				}
			}
		}

		private Attackable findTargetMob(FakePlayer hunter, int range, boolean deadOnly)
		{
			HunterProfile profile = hunter.getHunterProfile();
			boolean allowKS = (profile != null) && profile.isAllowKS();
			boolean groupAssist = (profile != null) && profile.isGroupAssist();

			List<Attackable> mobs = World.getInstance().getVisibleObjectsInRange(hunter, Attackable.class, range);

			// Group Assist Priority
			if (groupAssist && !deadOnly && hunter.getParty() != null)
			{
				for (Attackable mob : mobs)
				{
					if (mob != null && !mob.isAlikeDead() && mob.getTarget() != null && mob.getTarget().isPlayer())
					{
						Player targetPlayer = mob.getTarget().asPlayer();
						if (hunter.getParty().getMembers().contains(targetPlayer))
						{
							return mob;
						}
					}
				}
			}

			// Target Scanning with Anti-KS
			for (Attackable mob : mobs)
			{
				if (mob == null || mob.isAlikeDead() != deadOnly)
				{
					continue;
				}
				if (deadOnly)
				{
					if (mob.isSpoiled())
					{
						return mob;
					}
				}
				else
				{
					if (!allowKS && mob.getTarget() != null && mob.getTarget().isPlayer() && !mob.getTarget().asPlayer().isFakePlayer())
					{
						continue;
					}
					return mob;
				}
			}
			return null;
		}

		private void castSkill(FakePlayer caster, Creature target, int skillId)
		{
			Skill skill = SkillData.getInstance().getSkill(skillId, Math.max(1, caster.getSkillLevel(skillId)));
			if (skill != null && caster.getCurrentMp() >= skill.getMpConsume() && !caster.isCastingNow())
			{
				caster.setTarget(target);
				caster.doCast(skill);
			}
		}
	}

	/**
	 * Shift schedule and Jitter management tick.
	 */
	private class ShiftTick implements Runnable
	{
		@Override
		public void run()
		{
			// All FakeHunters remain active 24/7 for continuous behavioral analysis and monitoring.
			// Shift despawn logic is bypassed.
		}

		private String getShiftNameForHour(int hour)
		{
			if (hour >= 6 && hour < 12)
			{
				return "MORNING";
			}
			else if (hour >= 12 && hour < 18)
			{
				return "AFTERNOON";
			}
			else if (hour >= 18 && hour < 23)
			{
				return "PRIME_TIME";
			}
			else
			{
				return "NIGHT";
			}
		}
	}

	public static FakeHunterManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}

	private static class SingletonHolder
	{
		protected static final FakeHunterManager INSTANCE = new FakeHunterManager();
	}
}
