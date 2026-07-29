package com.l2journey.gameserver.managers;

import java.util.logging.Logger;

import com.l2journey.commons.threads.ThreadPool;
import com.l2journey.gameserver.model.World;
import com.l2journey.gameserver.model.actor.Player;

/**
 * Monitors real player presence in the Gludio territory.
 * Puts regional Fake Hunters to sleep when no real players are present, conserving CPU resources.
 */
public class GludioZoneListener
{
	private static final Logger LOGGER = Logger.getLogger(GludioZoneListener.class.getName());

	// Center of Gludio Town
	private static final int GLUDIO_CENTER_X = -14347;
	private static final int GLUDIO_CENTER_Y = 123622;
	private static final int GLUDIO_RADIUS = 6000; // Covers Town + Ruins of Despair & Agony

	private boolean _sleeping = false;

	protected GludioZoneListener()
	{
		// Check for real player presence every 10 seconds
		ThreadPool.scheduleAtFixedRate(new ZoneCheckTask(), 10000, 10000);
		LOGGER.info(getClass().getSimpleName() + ": Initialized Gludio regional zone monitor (10s interval).");
	}

	private class ZoneCheckTask implements Runnable
	{
		@Override
		public void run()
		{
			boolean hasRealPlayers = false;

			for (Player player : World.getInstance().getPlayers())
			{
				if (player != null && !player.isFakePlayer() && player.isOnline())
				{
					double distance = player.calculateDistance2D(GLUDIO_CENTER_X, GLUDIO_CENTER_Y, 0);
					if (distance <= GLUDIO_RADIUS)
					{
						hasRealPlayers = true;
						break;
					}
				}
			}

			if (!hasRealPlayers && !_sleeping)
			{
				_sleeping = true;
				FakeHunterManager.getInstance().setZoneSleeping("GLUDIO", true);
				LOGGER.info("GludioZoneListener: No real players in Gludio region. Fake Hunters set to SLEEP mode.");
			}
			else if (hasRealPlayers && _sleeping)
			{
				_sleeping = false;
				FakeHunterManager.getInstance().setZoneSleeping("GLUDIO", false);
				LOGGER.info("GludioZoneListener: Real player entered Gludio region. Fake Hunters AWAKENED.");
			}
		}
	}

	public static GludioZoneListener getInstance()
	{
		return SingletonHolder.INSTANCE;
	}

	private static class SingletonHolder
	{
		protected static final GludioZoneListener INSTANCE = new GludioZoneListener();
	}
}
