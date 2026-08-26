package com.l2journey.gameserver.model.actor.fakeplayer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.logging.Logger;

import com.l2journey.commons.threads.ThreadPool;
import com.l2journey.commons.util.Rnd;
import com.l2journey.gameserver.ai.PlayerAI;
import com.l2journey.gameserver.data.xml.FakeShopData;
import com.l2journey.gameserver.data.xml.PlayerTemplateData;
import com.l2journey.gameserver.model.actor.Player;
import com.l2journey.gameserver.model.actor.appearance.PlayerAppearance;
import com.l2journey.gameserver.model.actor.enums.player.PrivateStoreType;
import com.l2journey.gameserver.model.actor.holders.fakeplayer.CityCatalogHolder;
import com.l2journey.gameserver.model.actor.holders.fakeplayer.CityCatalogHolder.CatalogItem;
import com.l2journey.gameserver.model.actor.templates.PlayerTemplate;
import com.l2journey.gameserver.model.item.enums.ItemProcessType;
import com.l2journey.gameserver.model.item.instance.Item;
import com.l2journey.gameserver.network.serverpackets.PrivateStoreMsgSell;
import com.l2journey.gameserver.dao.FakePlayerDAO;

/**
 * AI & Controller for Fake Trader players in Gludio.
 */
public class FakeTraderAI extends PlayerAI
{
	private static final Logger LOGGER = Logger.getLogger(FakeTraderAI.class.getName());

	private final FakePlayerProfile _profile;
	private Player _player;
	private ScheduledFuture<?> _economicCycleTask;

	public FakeTraderAI(Player player, FakePlayerProfile profile)
	{
		super(player);
		_player = player;
		_profile = profile;
	}

	public FakePlayerProfile getProfile()
	{
		return _profile;
	}

	public Player getPlayer()
	{
		return _player;
	}

	public static FakeTraderAI spawnTrader(FakePlayerProfile profile)
	{
		int classId = profile.getClassId() > 0 ? profile.getClassId() : 53; // Default Dwarf Bounty Hunter
		PlayerTemplate template = PlayerTemplateData.getInstance().getTemplate(classId);
		if (template == null)
		{
			template = PlayerTemplateData.getInstance().getTemplate(53);
		}

		final String name = "Trader_" + profile.getFakeId();
		final PlayerAppearance appearance = new PlayerAppearance((byte) Rnd.get(3), (byte) Rnd.get(3), (byte) Rnd.get(3), false);
		Player player = Player.create(template, name.toLowerCase(), name, appearance);

		if (player == null)
		{
			LOGGER.warning("FakeTraderAI: Failed to create Player for profile ID " + profile.getFakeId());
			return null;
		}

		FakeTraderAI trader = new FakeTraderAI(player, profile);
		player.setFakePlayer(true);
		player.setAI(trader);
		player.getStat().setLevel((byte) 40);

		int x = profile.getX() != 0 ? profile.getX() : -14228;
		int y = profile.getY() != 0 ? profile.getY() : 123445;
		int z = profile.getZ() != 0 ? profile.getZ() : -3115;
		int heading = profile.getHeading() != 0 ? profile.getHeading() : 16384;

		player.setXYZ(x, y, z);
		player.setHeading(heading);
		player.spawnMe(x, y, z);

		trader.initPrivateStore();
		trader.startEconomicCycle();

		profile.setActive(true);
		profile.setLastActiveTime(System.currentTimeMillis());
		FakePlayerDAO.getInstance().saveProfile(profile);

		LOGGER.info("FakeTraderAI: Spawned Trader " + name + " in zone " + profile.getZoneId());
		return trader;
	}

	public synchronized void despawn()
	{
		if (_economicCycleTask != null)
		{
			_economicCycleTask.cancel(false);
			_economicCycleTask = null;
		}

		if (_player != null)
		{
			_profile.setX(_player.getX());
			_profile.setY(_player.getY());
			_profile.setZ(_player.getZ());
			_profile.setHeading(_player.getHeading());
			_profile.setActive(false);
			FakePlayerDAO.getInstance().saveProfile(_profile);

			_player.deleteMe();
			_player = null;
		}
	}

	public synchronized void initPrivateStore()
	{
		if ((_player == null) || !_player.isOnline())
		{
			return;
		}

		_player.getInventory().destroyItemByItemId(ItemProcessType.DESTROY, 57, _player.getInventory().getAdena(), _player, null);
		_player.getInventory().addAdena(ItemProcessType.REWARD, 1000000, _player, null);

		CityCatalogHolder catalog = FakeShopData.getInstance().getCityCatalog(_profile.getZoneId());
		List<CatalogItem> pool = new ArrayList<>();
		if (catalog != null)
		{
			pool.addAll(catalog.getMaterials());
			pool.addAll(catalog.getSupplies());
			pool.addAll(catalog.getItems());
		}

		int numItems = Rnd.get(1, 5); // 1-5 sales slots
		_player.getSellList().clear();

		if (!pool.isEmpty())
		{
			Collections.shuffle(pool);
			int added = 0;
			for (CatalogItem catItem : pool)
			{
				if (added >= numItems)
				{
					break;
				}
				long count = Rnd.get(catItem.getMinCount(), catItem.getMaxCount());
				long price = Rnd.get(catItem.getMinPrice(), catItem.getMaxPrice());
				Item item = _player.getInventory().addItem(ItemProcessType.FEE, catItem.getItemId(), count, _player, null);
				if (item != null)
				{
					_player.getSellList().addItem(item.getObjectId(), count, price);
					added++;
				}
			}
		}

		if (!_player.getSellList().getItems().isEmpty())
		{
			_player.getSellList().setTitle("Gludio Goods #" + _profile.getFakeId());
			_player.setPrivateStoreType(PrivateStoreType.SELL);
			_player.sitDown();
			_player.broadcastPacket(new PrivateStoreMsgSell(_player));
			_player.broadcastUserInfo();
		}
	}

	public synchronized void refreshEconomicCycle()
	{
		if ((_player == null) || !_player.isOnline())
		{
			return;
		}
		LOGGER.info("FakeTraderAI: Refreshing economic cycle for Trader #" + _profile.getFakeId());
		initPrivateStore();
	}

	private void startEconomicCycle()
	{
		// Economic cycle refresh interval (24 hours = 86,400,000 ms)
		long intervalMs = 24L * 60L * 60L * 1000L;
		_economicCycleTask = ThreadPool.scheduleAtFixedRate(this::refreshEconomicCycle, intervalMs, intervalMs);
	}
}
