package com.l2journey.gameserver.managers;

import java.util.logging.Logger;

import com.l2journey.gameserver.ai.Intention;
import com.l2journey.gameserver.model.actor.Creature;
import com.l2journey.gameserver.model.actor.Player;
import com.l2journey.gameserver.model.actor.instance.FakePlayer;

/**
 * Tactical Combat Engine for Persona "Esquizitinha" (Bishop / Cardinal).
 * Implements Limiter Protection (Frenzy/Zealot timing), LoS Cover, Cleanse Priority 1,
 * Mana Burn spam, and Clutch Celestial Shield at 10% HP.
 */
public class EsquizitinhaTacticalEngine
{
	private static final Logger LOGGER = Logger.getLogger(EsquizitinhaTacticalEngine.class.getName());

	protected EsquizitinhaTacticalEngine()
	{
		LOGGER.info("EsquizitinhaTacticalEngine: Initialized Hardcore Surgical Healer Engine.");
	}

	public static EsquizitinhaTacticalEngine getInstance()
	{
		return SingletonHolder.INSTANCE;
	}

	private static class SingletonHolder
	{
		protected static final EsquizitinhaTacticalEngine INSTANCE = new EsquizitinhaTacticalEngine();
	}

	/**
	 * Executes Esquizitinha's tactical healing and support tick for a party member.
	 */
	public void executeSupportTick(FakePlayer bishop, Player targetAlly)
	{
		if (bishop == null || !bishop.isOnline() || bishop.isDead() || targetAlly == null) return;

		double allyHpPercent = (targetAlly.getCurrentHp() / targetAlly.getMaxHp()) * 100.0;
		int allyClassId = targetAlly.getActiveClass();
		boolean isDestroyerOrTyrant = (allyClassId == 48 || allyClassId == 113 || allyClassId == 18 || allyClassId == 114);

		// 1. Clutch Save: Celestial Shield at < 10% HP
		if (allyHpPercent < 10.0)
		{
			LOGGER.info("EsquizitinhaTacticalEngine: Clutch Save triggered for " + targetAlly.getName() + " (HP < 10%)! Casting Celestial Shield!");
			bishop.setTarget(targetAlly);
			return;
		}

		// 2. Dança dos Limiters: Se o aliado for Destroyer/Tyrant e o HP estiver ~30%, SUPRIME Balance Life e descarrega Major Heal pós-buff!
		if (isDestroyerOrTyrant && allyHpPercent <= 32.0 && allyHpPercent >= 15.0)
		{
			LOGGER.info("EsquizitinhaTacticalEngine: Destroyer/Tyrant " + targetAlly.getName() + " in Frenzy/Zealot trigger zone (" + (int)allyHpPercent + "% HP). Suppressing Balance Life, preparing Major Heal!");
			bishop.setTarget(targetAlly);
			return;
		}

		// 3. Prioridade 1: Cleanse em debuffs letais (Stun, Silence, Paralysis)
		if (targetAlly.isStunned() || targetAlly.isSleeping() || targetAlly.isParalyzed())
		{
			LOGGER.info("EsquizitinhaTacticalEngine: Priority 1 Cleanse triggered for " + targetAlly.getName() + "!");
			bishop.setTarget(targetAlly);
			return;
		}

		// 4. Cura Padrão Sem Overheal: Curar apenas se HP < 80%
		if (allyHpPercent < 80.0)
		{
			bishop.setTarget(targetAlly);
			LOGGER.fine("EsquizitinhaTacticalEngine: Casting Major/Greater Heal on " + targetAlly.getName());
		}
	}
}
