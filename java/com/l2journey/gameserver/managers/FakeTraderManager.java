package com.l2journey.gameserver.managers;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

import com.l2journey.commons.threads.ThreadPool;
import com.l2journey.gameserver.model.actor.instance.FakePlayer;

/**
 * Orchestrates the lifecycle of Fake Traders (SELL, BUY, CRAFT).
 * Controls the renewal cycle (window of activity) and prevents collision during trades.
 */
public class FakeTraderManager
{
	private static final Logger LOGGER = Logger.getLogger(FakeTraderManager.class.getName());
	private final List<FakePlayer> _activeTraders = new CopyOnWriteArrayList<>();
	private final List<String> _reservedNames = new CopyOnWriteArrayList<>();


	protected FakeTraderManager()
	{
		// Runs every 1 minute to check for expired bots
		ThreadPool.scheduleAtFixedRate(new TraderTick(), 60000, 60000);
		LOGGER.info(getClass().getSimpleName() + ": Initialized with TraderTick running every minute.");
	}

	public void addTrader(FakePlayer trader)
	{
		_activeTraders.add(trader);
	}

	public void removeTrader(FakePlayer trader)
	{
		_activeTraders.remove(trader);
	}

	public boolean isNameTaken(String name)
	{
		return _reservedNames.contains(name.toLowerCase());
	}

	public void addReservedName(String name)
	{
		_reservedNames.add(name.toLowerCase());
	}


	private class TraderTick implements Runnable
	{
		@Override
		public void run()
		{
			long currentTime = System.currentTimeMillis();
			for (FakePlayer bot : _activeTraders)
			{
				if (bot.getRenewTime() > 0 && currentTime > (bot.getSpawnTime() + bot.getRenewTime()))
				{
					// Task 4.4: Renewal Collision - Check if a real player is interacting
					if (bot.getActiveRequester() != null || bot.getActiveTradeList() != null)
					{
						// Delay the renew by 2 minutes
						bot.setRenewTime(bot.getRenewTime() + 120000);
						continue;
					}

					// Proceed with reset
					resetTrader(bot);
				}
			}
		}
	}

	private void resetTrader(FakePlayer bot)
	{
		// TODO (Tasks 3.x): Close store, stand up, wipe inventory/adena, randomise new items, sit down.
	}

	public static FakeTraderManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}

	private static class SingletonHolder
	{
		protected static final FakeTraderManager INSTANCE = new FakeTraderManager();
	}
}
