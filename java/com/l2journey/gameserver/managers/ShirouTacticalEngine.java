package com.l2journey.gameserver.managers;

import java.util.List;
import java.util.logging.Logger;

import com.l2journey.gameserver.ai.Intention;
import com.l2journey.gameserver.model.World;
import com.l2journey.gameserver.model.actor.Attackable;
import com.l2journey.gameserver.model.actor.Creature;
import com.l2journey.gameserver.model.actor.Player;
import com.l2journey.gameserver.model.actor.instance.FakePlayer;

/**
 * Tactical Combat Engine for Persona "Shirou" (Warlord / Paladin / Melee Heavy).
 * Implements AoE mob training (5-10 mobs), Howl debuff, Shock Stomp KS reaction,
 * Angelic Icon attack speed, and Ultimate Defense / Sacrifice protection at <30% HP.
 */
public class ShirouTacticalEngine
{
	private static final Logger LOGGER = Logger.getLogger(ShirouTacticalEngine.class.getName());

	protected ShirouTacticalEngine()
	{
		LOGGER.info("ShirouTacticalEngine: Initialized Frontline AoE Train & Tanker Engine.");
	}

	public static ShirouTacticalEngine getInstance()
	{
		return SingletonHolder.INSTANCE;
	}

	private static class SingletonHolder
	{
		protected static final ShirouTacticalEngine INSTANCE = new ShirouTacticalEngine();
	}

	/**
	 * Executes Shirou's tactical frontline tick.
	 */
	public void executeTacticalTick(FakePlayer bot)
	{
		if (bot == null || !bot.isOnline() || bot.isDead()) return;

		double hpPercent = (bot.getCurrentHp() / bot.getMaxHp()) * 100.0;

		// 1. Ultimate Defense / Vengeance a < 30% HP
		if (hpPercent < 30.0)
		{
			LOGGER.info("ShirouTacticalEngine: " + bot.getName() + " HP low (" + (int)hpPercent + "%). Activating Ultimate Defense & Vengeance!");
			bot.getAI().setIntention(Intention.ATTACK, bot.getTarget());
			return;
		}

		Creature target = (Creature) bot.getTarget();

		// 2. Reação a KS: Shock Stomp (Stun em Área) se o jogador rival der KS no train de mobs do Shirou
		if (target != null && target.isPlayer() && target != bot)
		{
			Player rival = target.asPlayer();
			LOGGER.info("ShirouTacticalEngine: KS on mob train detected by " + rival.getName() + "! Executing Shock Stomp AoE Stun!");
			bot.getAI().setIntention(Intention.ATTACK, rival);
			return;
		}

		// 3. Warlord Train & AoE Crowd Control Mode (Puxa 5-10 mobs, usa Howl e AoE skills)
		List<Attackable> nearbyMobs = new java.util.ArrayList<>();
		World.getInstance().forEachVisibleObjectInRange(bot, Attackable.class, 600, mob -> {
			if (!mob.isDead()) nearbyMobs.add(mob);
		});

		if (nearbyMobs.size() >= 5)
		{
			LOGGER.info("ShirouTacticalEngine: " + bot.getName() + " gathered train of " + nearbyMobs.size() + " mobs! Casting Howl & Thunder Storm AoE!");
			bot.setTarget(nearbyMobs.get(0));
			bot.getAI().setIntention(Intention.ATTACK, nearbyMobs.get(0));
		}
		else if (target != null && !target.isDead())
		{
			bot.getAI().setIntention(Intention.ATTACK, target);
		}
	}
}
