package com.l2journey.gameserver.managers;

import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

import com.l2journey.commons.threads.ThreadPool;
import com.l2journey.commons.util.Rnd;
import com.l2journey.gameserver.model.actor.dna.HunterDNA;
import com.l2journey.gameserver.model.actor.instance.FakePlayer;

/**
 * Orchestrates the lifecycle and combat AI ticks of Fake Hunters (PvE combat bots).
 * Manages shifts, random jitter delays, preservation/flee decision making, leashing, and regional sleep modes.
 */
public class FakeHunterManager
{
	private static final Logger LOGGER = Logger.getLogger(FakeHunterManager.class.getName());

	private static final int GLUDIO_TOWN_X = -14347;
	private static final int GLUDIO_TOWN_Y = 123622;
	private static final int SAFE_ZONE_LEASH_RADIUS = 1200;

	private final List<FakePlayer> _activeHunters = new CopyOnWriteArrayList<>();
	private final List<String> _reservedNames = new CopyOnWriteArrayList<>();
	private final Map<String, Boolean> _zoneSleepState = new ConcurrentHashMap<>();

	protected FakeHunterManager()
	{
		// AI Tick: Runs every 2 seconds for active combat AI decisions (Task 3.1 & 3.2)
		ThreadPool.scheduleAtFixedRate(new HunterAITick(), 2000, 2000);

		// Shift Tick: Runs every 1 minute for shift transitions and jitter (Task 2.2 & 2.3)
		ThreadPool.scheduleAtFixedRate(new ShiftTick(), 60000, 60000);

		LOGGER.info(getClass().getSimpleName() + ": Initialized with AI tick (2s) and Shift tick (1m).");
	}

	public void addHunter(FakePlayer hunter)
	{
		_activeHunters.add(hunter);
	}

	public void removeHunter(FakePlayer hunter)
	{
		_activeHunters.remove(hunter);
	}

	public List<FakePlayer> getHunters()
	{
		return _activeHunters;
	}

	public boolean isNameTaken(String name)
	{
		return _reservedNames.contains(name.toLowerCase());
	}

	public void addReservedName(String name)
	{
		_reservedNames.add(name.toLowerCase());
	}

	public void setZoneSleeping(String zoneId, boolean sleeping)
	{
		_zoneSleepState.put(zoneId.toUpperCase(), sleeping);
	}

	public boolean isZoneSleeping(String zoneId)
	{
		return _zoneSleepState.getOrDefault(zoneId.toUpperCase(), false);
	}

	/**
	 * Main AI decision tick for combat bots.
	 */
	private class HunterAITick implements Runnable
	{
		@Override
		public void run()
		{
			// Task 3.2: Skip AI calculation if Gludio is in Sleep Mode (no real players)
			if (isZoneSleeping("GLUDIO"))
			{
				return;
			}

			for (FakePlayer hunter : _activeHunters)
			{
				if (hunter == null || hunter.isDead() || hunter.isAlikeDead() || !hunter.isOnline())
				{
					continue;
				}

				final HunterDNA dna = hunter.getHunterDNA();
				if (dna == null)
				{
					continue;
				}

				// Task 2.3: Leash Check - Avoid pulling monsters into Gludio safe town
				double distToTown = hunter.calculateDistance2D(GLUDIO_TOWN_X, GLUDIO_TOWN_Y, 0);
				if (distToTown < SAFE_ZONE_LEASH_RADIUS)
				{
					hunter.abortAttack();
					hunter.abortCast();
					hunter.setTarget(null);
					LOGGER.info("FakeHunter [" + hunter.getName() + "] triggered Safe Zone Leash (Retreating from Town).");
					continue;
				}

				// Task 3.1: Preservation & Safety Check (HP < 20%)
				double currentHpPercent = (hunter.getCurrentHp() / hunter.getMaxHp()) * 100.0;
				if (currentHpPercent < 20.0)
				{
					if (dna.getPreservation() > 50)
					{
						if (Rnd.get(100) < dna.getPreservation())
						{
							hunter.abortAttack();
							hunter.abortCast();
							LOGGER.info("FakeHunter [" + hunter.getName() + "] triggered safety flee (Preservation: " + dna.getPreservation() + ").");
						}
					}
				}
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
			int currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
			String currentShift = getShiftNameForHour(currentHour);

			for (FakePlayer hunter : _activeHunters)
			{
				final HunterDNA dna = hunter.getHunterDNA();
				if (dna == null)
				{
					continue;
				}

				String shift = dna.getShift();
				if (!"ALL_DAY".equalsIgnoreCase(shift) && !shift.equalsIgnoreCase(currentShift))
				{
					long jitterDelayMs = Rnd.get(0, 10) * 60000L;
					ThreadPool.schedule(() ->
					{
						if (hunter.isOnline())
						{
							hunter.deleteMe();
							removeHunter(hunter);
							LOGGER.info("FakeHunter [" + hunter.getName() + "] despawned due to shift end (" + shift + ").");
						}
					}, jitterDelayMs);
				}
			}
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
