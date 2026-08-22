package com.l2journey.gameserver.model.actor.fakeplayer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.logging.Logger;

import com.l2journey.commons.threads.ThreadPool;
import com.l2journey.commons.util.Rnd;
import com.l2journey.gameserver.data.xml.FakeShopData;
import com.l2journey.gameserver.data.xml.PlayerTemplateData;
import com.l2journey.gameserver.data.xml.RecipeData;
import com.l2journey.gameserver.model.ManufactureItem;
import com.l2journey.gameserver.model.RecipeList;
import com.l2journey.gameserver.model.World;
import com.l2journey.gameserver.model.actor.Player;
import com.l2journey.gameserver.model.actor.appearance.PlayerAppearance;
import com.l2journey.gameserver.model.actor.enums.player.PrivateStoreType;
import com.l2journey.gameserver.model.actor.holders.fakeplayer.CityCatalogHolder;
import com.l2journey.gameserver.model.actor.holders.fakeplayer.CityCatalogHolder.CatalogItem;
import com.l2journey.gameserver.model.actor.holders.fakeplayer.FakeShopHolder;
import com.l2journey.gameserver.model.actor.holders.fakeplayer.FakeShopHolder.ShopType;
import com.l2journey.gameserver.model.actor.templates.PlayerTemplate;
import com.l2journey.gameserver.model.item.enums.ItemProcessType;
import com.l2journey.gameserver.network.serverpackets.PrivateStoreMsgBuy;
import com.l2journey.gameserver.network.serverpackets.PrivateStoreMsgSell;
import com.l2journey.gameserver.network.serverpackets.RecipeShopMsg;
import com.l2journey.gameserver.util.Broadcast;

/**
 * Controller class for a FakeShop based on the native Player class.
 */
public class FakeShop
{
	private static final Logger LOGGER = Logger.getLogger(FakeShop.class.getName());

	private final FakeShopHolder _holder;
	private Player _player;
	private ScheduledFuture<?> _updateTask;

	public FakeShop(FakeShopHolder holder)
	{
		_holder = holder;
	}

	public FakeShopHolder getHolder()
	{
		return _holder;
	}

	public Player getPlayer()
	{
		return _player;
	}

	public synchronized void spawn()
	{
		if ((_player != null) && _player.isOnline())
		{
			return;
		}

		PlayerTemplate template = PlayerTemplateData.getInstance().getTemplate(_holder.getClassId());
		if (template == null)
		{
			// Default to Dwarf Bounty Hunter (53) if template not found
			template = PlayerTemplateData.getInstance().getTemplate(53);
		}

		final PlayerAppearance appearance = new PlayerAppearance((byte) Rnd.get(3), (byte) Rnd.get(3), (byte) Rnd.get(3), false);
		_player = Player.create(template, "fakeshop_" + _holder.getName().toLowerCase(), _holder.getName(), appearance);

		if (_player == null)
		{
			LOGGER.warning("FakeShop: Failed to create Player instance for " + _holder.getName());
			return;
		}

		_player.setFakePlayer(true);
		_player.getStat().setLevel((byte) _holder.getLevel());
		_player.setXYZ(_holder.getX(), _holder.getY(), _holder.getZ());
		_player.setHeading(_holder.getHeading());
		_player.spawnMe(_holder.getX(), _holder.getY(), _holder.getZ());

		setupShop();

		final long delayMs = _holder.getUpdateIntervalMinutes() * 60L * 1000L;
		_updateTask = ThreadPool.scheduleAtFixedRate(this::recalculateShop, delayMs, delayMs);

		LOGGER.info("FakeShop: " + _holder.getName() + " spawned as " + _holder.getShopType() + " in " + _holder.getCityName());
	}

	public synchronized void setupShop()
	{
		if ((_player == null) || !_player.isOnline())
		{
			return;
		}

		_player.setPrivateStoreType(PrivateStoreType.NONE);

		List<CatalogItem> selectedItems = selectItemsForShop();
		if (selectedItems.isEmpty())
		{
			LOGGER.warning("FakeShop [" + _holder.getName() + "]: No items available to set up shop.");
			return;
		}

		if (_holder.getShopType() == ShopType.SELL)
		{
			setupSellStore(selectedItems);
		}
		else if (_holder.getShopType() == ShopType.BUY)
		{
			setupBuyStore(selectedItems);
		}
		else if (_holder.getShopType() == ShopType.CRAFT)
		{
			setupCraftStore(selectedItems);
		}
	}

	private List<CatalogItem> selectItemsForShop()
	{
		if (!_holder.getCustomItems().isEmpty())
		{
			return _holder.getCustomItems();
		}

		final CityCatalogHolder catalog = FakeShopData.getInstance().getCityCatalog(_holder.getCityName());
		if (catalog == null)
		{
			return Collections.emptyList();
		}

		final List<CatalogItem> pool = new ArrayList<>(catalog.getByCategory(_holder.getCategory()));
		if (pool.isEmpty())
		{
			return Collections.emptyList();
		}

		Collections.shuffle(pool);
		final int count = Math.min(_holder.getMaxItems(), pool.size());
		return pool.subList(0, count);
	}

	private void setupSellStore(List<CatalogItem> items)
	{
		_player.getSellList().clear();
		for (CatalogItem item : items)
		{
			final long count = item.getMinCount() == item.getMaxCount() ? item.getMinCount() : Rnd.get(item.getMinCount(), item.getMaxCount());
			final long price = item.getMinPrice() == item.getMaxPrice() ? item.getMinPrice() : Rnd.get(item.getMinPrice(), item.getMaxPrice());

			_player.getInventory().addItem(ItemProcessType.FEE, item.getItemId(), count, _player, null);
			_player.getSellList().addItem(item.getItemId(), count, price);
		}

		_player.sitDown();
		_player.setPrivateStoreType(PrivateStoreType.SELL);
		_player.broadcastUserInfo();
		_player.broadcastPacket(new PrivateStoreMsgSell(_player));
	}

	private void setupBuyStore(List<CatalogItem> items)
	{
		_player.getBuyList().clear();
		_player.addAdena(ItemProcessType.FEE, 2000000000L, _player, false);

		for (CatalogItem item : items)
		{
			final long count = item.getMinCount() == item.getMaxCount() ? item.getMinCount() : Rnd.get(item.getMinCount(), item.getMaxCount());
			final long price = item.getMinPrice() == item.getMaxPrice() ? item.getMinPrice() : Rnd.get(item.getMinPrice(), item.getMaxPrice());

			_player.getBuyList().addItem(item.getItemId(), count, price);
		}

		_player.sitDown();
		_player.setPrivateStoreType(PrivateStoreType.BUY);
		_player.broadcastUserInfo();
		_player.broadcastPacket(new PrivateStoreMsgBuy(_player));
	}

	private void setupCraftStore(List<CatalogItem> items)
	{
		_player.getManufactureItems().clear();
		for (CatalogItem item : items)
		{
			final RecipeList recipe = RecipeData.getInstance().getRecipeList(item.getItemId());
			if (recipe != null)
			{
				if (recipe.isDwarvenRecipe())
				{
					_player.registerDwarvenRecipeList(recipe, false);
				}
				else
				{
					_player.registerCommonRecipeList(recipe, false);
				}
				_player.getManufactureItems().put(recipe.getId(), new ManufactureItem(recipe.getId(), item.getMinPrice()));
			}
		}

		_player.setStoreName(_holder.getTitle());
		_player.sitDown();
		_player.setPrivateStoreType(PrivateStoreType.MANUFACTURE);
		_player.broadcastUserInfo();
		Broadcast.toSelfAndKnownPlayers(_player, new RecipeShopMsg(_player));
	}

	public synchronized void recalculateShop()
	{
		if ((_player == null) || !_player.isOnline())
		{
			return;
		}

		LOGGER.info("FakeShop [" + _holder.getName() + "]: Recalculating store items and prices...");
		setupShop();
	}

	public synchronized void despawn()
	{
		if (_updateTask != null)
		{
			_updateTask.cancel(false);
			_updateTask = null;
		}

		if (_player != null)
		{
			_player.setPrivateStoreType(PrivateStoreType.NONE);
			_player.deleteMe();
			_player = null;
		}
	}
}
