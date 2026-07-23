package com.l2journey.gameserver.model.actor.instance;

import com.l2journey.gameserver.model.actor.Player;
import com.l2journey.gameserver.model.actor.appearance.PlayerAppearance;
import com.l2journey.gameserver.model.actor.templates.PlayerTemplate;
import com.l2journey.gameserver.model.item.instance.Item;
import com.l2journey.gameserver.model.ManufactureItem;
import com.l2journey.gameserver.model.TradeList;
import com.l2journey.gameserver.data.xml.impl.FakeTradersEconomyParser;
import com.l2journey.gameserver.model.fake.EconomyProfile;
import com.l2journey.gameserver.model.fake.EconomyItem;
import com.l2journey.gameserver.model.fake.EconomyRecipe;
import com.l2journey.commons.util.Rnd;
import com.l2journey.gameserver.network.serverpackets.L2GameServerPacket;
import com.l2journey.gameserver.network.serverpackets.SystemMessage;

/**
 * Fake Player implementation for Traders (SELL, BUY, CRAFT).
 * Bypasses database saving and network packet sending.
 */
public class FakePlayer extends Player
{
	private long _spawnTime;
	private long _renewTime;

	/**
	 * Instantiates a new fake player.
	 * @param objectId the object id
	 * @param template the template
	 * @param accountName the account name (can be a dummy string like "FakeTraders")
	 * @param app the appearance
	 */
	public FakePlayer(int objectId, PlayerTemplate template, String accountName, PlayerAppearance app)
	{
		super(objectId, template, accountName, app);
	}

	@Override
	public boolean isFakePlayer()
	{
		return true;
	}

	@Override
	public void store()
	{
		// Ghost Object: Do not save to DB
	}

	@Override
	public void storeCharBase()
	{
		// Ghost Object: Do not save to DB
	}

	@Override
	public int getMaxLoad()
	{
		return Integer.MAX_VALUE; // Infinite inventory weight
	}

	@Override
	public void sendPacket(L2GameServerPacket packet)
	{
		// No network client attached, ignore all packets
	}

	@Override
	public void sendPacket(SystemMessage packet)
	{
		// Overload specifically for SystemMessage
	}

	public void wipeState()
	{
		// Stand up
		setStandUp();
		
		// Clear inventory
		getInventory().destroyAllItems("FakePlayerWipe", this, null);
		
		// Clear adena
		getInventory().reduceAdena("FakePlayerWipe", getInventory().getAdena(), this, null);
		
		// Clear TradeList and RecipeBook
		if (getSellList() != null)
			getSellList().clear();
		if (getBuyList() != null)
			getBuyList().clear();
			
		setPrivateStoreType(0); // STORE_PRIVATE_NONE
		broadcastUserInfo();
	}

	public void setupSellStore(String profileId)
	{
		EconomyProfile profile = FakeTradersEconomyParser.getInstance().getProfile(profileId);
		if (profile == null || profile.getItems().isEmpty())
			return;

		getSellList().clear();

		// Randomly pick 1 to 3 items
		int itemsToSell = Rnd.get(1, 3);
		for (int i = 0; i < itemsToSell; i++)
		{
			EconomyItem eItem = profile.getItems().get(Rnd.get(profile.getItems().size()));
			long count = Rnd.get(eItem.getMinQty(), eItem.getMaxQty());
			long price = Rnd.get(eItem.getMinPrice(), eItem.getMaxPrice());

			if (count > 0 && price > 0)
			{
				Item item = getInventory().addItem("FakeTraderSell", eItem.getItemId(), count, this, null);
				if (item != null)
				{
					getSellList().addItem(item.getObjectId(), count, price);
				}
			}
		}

		setPrivateStoreType(1); // STORE_PRIVATE_SELL
		sitDown(false);
		broadcastUserInfo();
	}

	public void setupBuyStore(String profileId)
	{
		// Inject 1 Billion Adena
		getInventory().addAdena("FakeTraderBuy", 1000000000L, this, null);
		
		EconomyProfile profile = FakeTradersEconomyParser.getInstance().getProfile(profileId);
		if (profile == null || profile.getItems().isEmpty())
			return;

		getBuyList().clear();

		int itemsToBuy = Rnd.get(1, 3);
		for (int i = 0; i < itemsToBuy; i++)
		{
			EconomyItem eItem = profile.getItems().get(Rnd.get(profile.getItems().size()));
			long count = Rnd.get(eItem.getMinQty(), eItem.getMaxQty());
			long price = Rnd.get(eItem.getMinPrice(), eItem.getMaxPrice());

			if (count > 0 && price > 0)
			{
				getBuyList().addItemByItemId(eItem.getItemId(), count, price);
			}
		}

		setPrivateStoreType(3); // STORE_PRIVATE_BUY
		sitDown(false);
		broadcastUserInfo();
	}

	public void setupCraftStore(String profileId)
	{
		EconomyProfile profile = FakeTradersEconomyParser.getInstance().getProfile(profileId);
		if (profile == null || profile.getRecipes().isEmpty())
			return;

		getManufactureItems().clear();

		for (EconomyRecipe eRecipe : profile.getRecipes())
		{
			long fee = Rnd.get(eRecipe.getMinFee(), eRecipe.getMaxFee());
			getManufactureItems().put(eRecipe.getRecipeId(), new ManufactureItem(eRecipe.getRecipeId(), fee));
		}

		setPrivateStoreType(5); // STORE_PRIVATE_MANUFACTURE
		sitDown(false);
		broadcastUserInfo();
	}

	public long getSpawnTime()
	{
		return _spawnTime;
	}

	public void setSpawnTime(long spawnTime)
	{
		_spawnTime = spawnTime;
	}

	public long getRenewTime()
	{
		return _renewTime;
	}

	public void setRenewTime(long renewTime)
	{
		_renewTime = renewTime;
	}
}
