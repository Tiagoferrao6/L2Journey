package com.l2journey.gameserver.managers;

import java.util.logging.Logger;

import com.l2journey.commons.util.Rnd;
import com.l2journey.gameserver.ai.Intention;
import com.l2journey.gameserver.model.actor.Creature;
import com.l2journey.gameserver.model.actor.Player;
import com.l2journey.gameserver.model.actor.instance.FakePlayer;
import com.l2journey.gameserver.model.item.enums.ItemProcessType;

/**
 * Tactical Combat Engine for Persona "Crystal" (Silver Ranger / Archer).
 * Implements max-range positioning, 2s kiting, Stunning Shot KS reaction, and Hit&Run PvP with Escape <30% HP.
 */
public class CrystalTacticalEngine
{
	private static final Logger LOGGER = Logger.getLogger(CrystalTacticalEngine.class.getName());

	protected CrystalTacticalEngine()
	{
		LOGGER.info("CrystalTacticalEngine: Initialized Silver Ranger Kiter & Opportunist Engine.");
	}

	public static CrystalTacticalEngine getInstance()
	{
		return SingletonHolder.INSTANCE;
	}

	private static class SingletonHolder
	{
		protected static final CrystalTacticalEngine INSTANCE = new CrystalTacticalEngine();
	}

	/**
	 * Executes Crystal's tactical combat tick.
	 */
	public void executeTacticalTick(FakePlayer bot)
	{
		if (bot == null || !bot.isOnline() || bot.isDead()) return;

		double hpPercent = (bot.getCurrentHp() / bot.getMaxHp()) * 100.0;

		// 1. Amor à Vida: Se HP < 30% no combate/PvP, recuar com Dash ou usar Scroll of Escape
		if (hpPercent < 30.0)
		{
			LOGGER.info("CrystalTacticalEngine: " + bot.getName() + " HP low (" + (int)hpPercent + "%). Executing emergency escape!");
			bot.getInventory().destroyItemByItemId(ItemProcessType.FEE, 736, 1, bot, null); // Scroll of Escape
			bot.teleToLocation(bot.getX() + Rnd.get(-300, 300), bot.getY() + Rnd.get(-300, 300), bot.getZ());
			return;
		}

		Creature target = (Creature) bot.getTarget();
		if (target == null || target.isDead())
		{
			java.util.List<com.l2journey.gameserver.model.actor.Attackable> mobs = new java.util.ArrayList<>();
			com.l2journey.gameserver.model.World.getInstance().forEachVisibleObjectInRange(bot, com.l2journey.gameserver.model.actor.Attackable.class, 2000, mob -> {
				if (!mob.isDead()) mobs.add(mob);
			});

			if (!mobs.isEmpty())
			{
				target = mobs.get(0);
				bot.setTarget(target);
			}
			else
			{
				int farmX = bot.getLevel() < 20 ? -82500 : -18450;
				int farmY = bot.getLevel() < 20 ? 240000 : 145000;
				int farmZ = bot.getLevel() < 20 ? -3700 : -3000;

				if (!bot.isInsideRadius2D(farmX, farmY, bot.getZ(), 500))
				{
					bot.teleToLocation(farmX + Rnd.get(-100, 100), farmY + Rnd.get(-100, 100), farmZ);
				}
				return;
			}
		}

		if (target != null && !target.isDead())
		{
			double dist = bot.calculateDistance3D(target);

			// 2. Mecânica de Kiting: Se mob/player aproxima < 300 unidades, recuar por 2s e atirar
			if (dist < 300)
			{
				int kiteX = bot.getX() + (bot.getX() > target.getX() ? 250 : -250);
				int kiteY = bot.getY() + (bot.getY() > target.getY() ? 250 : -250);
				bot.getAI().setIntention(Intention.MOVE_TO, new com.l2journey.gameserver.model.Location(kiteX, kiteY, bot.getZ()));
				LOGGER.fine("CrystalTacticalEngine: " + bot.getName() + " executing 2s Kite step away from target.");
				return;
			}

			// 3. Reação a KS: Se um jogador tentar roubar o mob do bot, manda Stunning Shot no jogador rival
			if (target.isPlayer() && target != bot)
			{
				Player rival = target.asPlayer();
				LOGGER.info("CrystalTacticalEngine: KS detected by " + rival.getName() + "! Casting Stunning Shot & Entangle!");
				bot.getAI().setIntention(Intention.ATTACK, rival);
			}
			else
			{
				bot.getAI().setIntention(Intention.ATTACK, target);
			}
		}
	}
}
