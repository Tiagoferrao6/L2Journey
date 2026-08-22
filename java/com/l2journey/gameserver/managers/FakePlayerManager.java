package com.l2journey.gameserver.managers;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import com.l2journey.Config;
import com.l2journey.gameserver.data.xml.FakeShopData;
import com.l2journey.gameserver.model.actor.fakeplayer.FakeShop;
import com.l2journey.gameserver.model.actor.holders.fakeplayer.FakeShopHolder;

/**
 * Modularized Manager for Fake Players (FakeShops and FakeHunters).
 */
public class FakePlayerManager
{
	private static final Logger LOGGER = Logger.getLogger(FakePlayerManager.class.getName());

	private final Map<String, FakeShop> _activeShops = new ConcurrentHashMap<>();

	protected FakePlayerManager()
	{
		if (!Config.FAKE_PLAYERS_ENABLED)
		{
			LOGGER.info(getClass().getSimpleName() + ": Fake Players system is disabled.");
			return;
		}

		LOGGER.info(getClass().getSimpleName() + ": Initializing Modular Fake Player Manager.");

		if (Config.FAKE_SHOPS_ENABLED)
		{
			initFakeShops();
		}

		if (Config.FAKE_HUNTERS_ENABLED)
		{
			initFakeHunters();
		}
	}

	public void initFakeShops()
	{
		LOGGER.info(getClass().getSimpleName() + ": Initializing FakeShops module...");
		FakeShopData.getInstance(); // Loads XML configs

		for (FakeShopHolder holder : FakeShopData.getInstance().getFakeShops())
		{
			final FakeShop shop = new FakeShop(holder);
			_activeShops.put(holder.getName().toLowerCase(), shop);
			shop.spawn();
		}

		LOGGER.info(getClass().getSimpleName() + ": Activated " + _activeShops.size() + " FakeShops.");
	}

	public void initFakeHunters()
	{
		LOGGER.info(getClass().getSimpleName() + ": Initializing FakeHunters module...");
		// FakeHunters module initialization
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
