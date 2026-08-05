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

	public static final int SOULSHOT_NG_ID = 1835;
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

		if (LLMCompanionManager.getInstance().isBotInShopCooldown(bot))
		{
			return false;
		}

		int soulshotId = bot.getLevel() >= 20 ? SOULSHOT_D_ID : SOULSHOT_NG_ID;
		long soulshotCount = getItemCount(bot, soulshotId);
		long potionCount = getItemCount(bot, HEALING_POTION_ID);

		return (soulshotCount < 100 || potionCount < 10);
	}

	/**
	 * Executes a merchant purchase of Soulshots and Healing Potions with proportional scaling if Adena < 7000.
	 */
	public boolean executePurchase(FakePlayer bot, Npc merchantNpc)
	{
		if (bot == null || !bot.isOnline()) return false;

		int soulshotId = bot.getLevel() >= 20 ? SOULSHOT_D_ID : SOULSHOT_NG_ID;
		long adena = bot.getInventory().getAdena();

		int shotsToBuy = 500;
		int potionsToBuy = 20;

		long shotCost = shotsToBuy * 10L;
		long potionCost = potionsToBuy * 100L;
		long totalCost = shotCost + potionCost;

		if (adena < totalCost)
		{
			if (adena < 500)
			{
				LOGGER.info("BuyListExecutingEngine: " + bot.getName() + " possui Adena insuficiente para o pacote mínimo (500 Adena). Atual: " + adena);
				return false;
			}

			// Proportional purchasing based on available Adena
			long availableForShots = (long) (adena * 0.7);
			shotsToBuy = (int) Math.max(50, Math.min(500, availableForShots / 10));
			shotCost = shotsToBuy * 10L;

			long availableForPotions = adena - shotCost;
			potionsToBuy = (int) Math.min(20, Math.max(0, availableForPotions / 100));
			potionCost = potionsToBuy * 100L;

			totalCost = shotCost + potionCost;

			if (totalCost > adena)
			{
				shotsToBuy = (int) (adena / 10);
				potionsToBuy = 0;
				shotCost = shotsToBuy * 10L;
				potionCost = 0;
				totalCost = shotCost;
			}
		}

		if (totalCost <= 0)
		{
			return false;
		}

		if (merchantNpc != null)
		{
			bot.setTarget(merchantNpc);
		}

		// Deduct Adena
		bot.getInventory().destroyItemByItemId(ItemProcessType.BUY, 57, totalCost, bot, null);

		// Add Items
		if (shotsToBuy > 0)
		{
			bot.getInventory().addItem(ItemProcessType.BUY, soulshotId, shotsToBuy, bot, null);
			// Auto-activate Soulshots
			bot.addAutoSoulShot(soulshotId);
			bot.sendPacket(new ExAutoSoulShot(soulshotId, 1));
		}

		if (potionsToBuy > 0)
		{
			bot.getInventory().addItem(ItemProcessType.BUY, HEALING_POTION_ID, potionsToBuy, bot, null);
		}

		LOGGER.info("BuyListExecutingEngine: " + bot.getName() + " realizou compra proporcional de " + shotsToBuy + " Shots e " + potionsToBuy + " Poções por " + totalCost + " Adena.");
		return true;
	}

	private long getItemCount(FakePlayer bot, int itemId)
	{
		Item item = bot.getInventory().getItemByItemId(itemId);
		return item != null ? item.getCount() : 0;
	}
}
