package com.l2journey.gameserver.managers;

import java.util.logging.Logger;

import com.l2journey.gameserver.model.actor.Npc;
import com.l2journey.gameserver.model.actor.instance.FakePlayer;
import com.l2journey.gameserver.model.item.enums.ItemProcessType;
import com.l2journey.gameserver.model.item.instance.Item;
import com.l2journey.gameserver.network.serverpackets.ExAutoSoulShot;

/**
 * Automates merchant consumable inspection, Adena validation, purchase execution,
 * and auto-soulshot activation for AI Companions.
 */
public class BuyListExecutingEngine
{
	private static final Logger LOGGER = Logger.getLogger(BuyListExecutingEngine.class.getName());

	public static final int SOULSHOT_D_ID = 1463;
	public static final int HEALING_POTION_ID = 1061;

	protected BuyListExecutingEngine()
	{
		LOGGER.info("BuyListExecutingEngine: Initialized Companion Purchase Engine.");
	}

	public static BuyListExecutingEngine getInstance()
	{
		return SingletonHolder.INSTANCE;
	}

	private static class SingletonHolder
	{
		protected static final BuyListExecutingEngine INSTANCE = new BuyListExecutingEngine();
	}

	/**
	 * Checks whether the companion bot needs to replenish consumables (Soulshots < 100 or Healing Potions < 10).
	 */
	public boolean needsConsumableReplenishment(FakePlayer bot)
	{
		if (bot == null || !bot.isOnline()) return false;

		long soulshotCount = getItemCount(bot, SOULSHOT_D_ID);
		long potionCount = getItemCount(bot, HEALING_POTION_ID);

		return (soulshotCount < 100 || potionCount < 10);
	}

	/**
	 * Executes a merchant purchase of Soulshots and Healing Potions if the bot has sufficient Adena.
	 */
	public boolean executePurchase(FakePlayer bot, Npc merchantNpc)
	{
		if (bot == null || !bot.isOnline()) return false;

		long adena = bot.getInventory().getAdena();
		int shotsToBuy = 500;
		int potionsToBuy = 20;

		long shotCost = shotsToBuy * 10L;
		long potionCost = potionsToBuy * 100L;
		long totalCost = shotCost + potionCost;

		if (adena < totalCost)
		{
			LOGGER.info("BuyListExecutingEngine: " + bot.getName() + " has insufficient Adena for purchase. Current: " + adena + ", Needed: " + totalCost);
			return false;
		}

		if (merchantNpc != null)
		{
			bot.setTarget(merchantNpc);
		}

		// Deduct Adena
		bot.getInventory().destroyItemByItemId(ItemProcessType.BUY, 57, totalCost, bot, null);

		// Add Items
		bot.getInventory().addItem(ItemProcessType.BUY, SOULSHOT_D_ID, shotsToBuy, bot, null);
		bot.getInventory().addItem(ItemProcessType.BUY, HEALING_POTION_ID, potionsToBuy, bot, null);

		// Auto-activate Soulshots
		bot.addAutoSoulShot(SOULSHOT_D_ID);
		bot.sendPacket(new ExAutoSoulShot(SOULSHOT_D_ID, 1));

		LOGGER.info("BuyListExecutingEngine: " + bot.getName() + " purchased " + shotsToBuy + " D-Shots & " + potionsToBuy + " Potions from merchant.");
		return true;
	}

	private long getItemCount(FakePlayer bot, int itemId)
	{
		Item item = bot.getInventory().getItemByItemId(itemId);
		return item != null ? item.getCount() : 0;
	}
}
