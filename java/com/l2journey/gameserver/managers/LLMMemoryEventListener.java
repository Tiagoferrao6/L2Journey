package com.l2journey.gameserver.managers;

import java.util.logging.Logger;

import com.l2journey.gameserver.model.actor.Attackable;
import com.l2journey.gameserver.model.actor.Player;
import com.l2journey.gameserver.model.actor.instance.FakePlayer;
import com.l2journey.gameserver.model.item.instance.Item;

/**
 * Event Listener & Hook Manager for registering bot memory events
 * such as Combat Assistance, Kill Stealing (KS), Loot Theft, and PvP Attacks.
 */
public class LLMMemoryEventListener
{
	private static final Logger LOGGER = Logger.getLogger(LLMMemoryEventListener.class.getName());

	protected LLMMemoryEventListener()
	{
		LOGGER.info("LLMMemoryEventListener: Initialized Memory & Relationship Hooks.");
	}

	public static LLMMemoryEventListener getInstance()
	{
		return SingletonHolder.INSTANCE;
	}

	private static class SingletonHolder
	{
		protected static final LLMMemoryEventListener INSTANCE = new LLMMemoryEventListener();
	}

	/**
	 * Triggered when a player heals an LLM bot.
	 */
	public void onPlayerHealBot(Player healer, FakePlayer bot)
	{
		if (healer == null || bot == null || healer.getObjectId() == bot.getObjectId()) return;

		int delta = 15;
		String desc = "Curou-me quando eu estava em perigo.";
		LLMMemoryManager.getInstance().recordMemory(
			bot.getObjectId(),
			healer.getObjectId(),
			healer.getName(),
			LLMMemoryManager.EventType.HELPED_IN_COMBAT,
			delta,
			desc
		);
		LOGGER.info("Memory Event [HELPED_IN_COMBAT]: " + healer.getName() + " healed " + bot.getName());
	}

	/**
	 * Triggered when a player assists an LLM bot in killing a targeted mob.
	 */
	public void onPlayerAssistBot(Player helper, FakePlayer bot, Attackable mob)
	{
		if (helper == null || bot == null || helper.getObjectId() == bot.getObjectId()) return;

		int delta = 15;
		String desc = "Ajudou a derrotar o mob em combate.";
		LLMMemoryManager.getInstance().recordMemory(
			bot.getObjectId(),
			helper.getObjectId(),
			helper.getName(),
			LLMMemoryManager.EventType.HELPED_IN_COMBAT,
			delta,
			desc
		);
		LOGGER.info("Memory Event [HELPED_IN_COMBAT]: " + helper.getName() + " assisted " + bot.getName());
	}

	/**
	 * Triggered when a non-party player steals a mob being attacked by an LLM bot.
	 */
	public void onKillSteal(Player ksPlayer, FakePlayer bot, Attackable mob)
	{
		if (ksPlayer == null || bot == null || ksPlayer.getObjectId() == bot.getObjectId()) return;
		if (bot.isInParty() && bot.getParty().containsPlayer(ksPlayer)) return;

		String mobName = mob != null ? mob.getName() : "mob";
		int delta = -20;
		String desc = "Roubou meu mob (" + mobName + ").";
		LLMMemoryManager.getInstance().recordMemory(
			bot.getObjectId(),
			ksPlayer.getObjectId(),
			ksPlayer.getName(),
			LLMMemoryManager.EventType.KS_MOB,
			delta,
			desc
		);
		LOGGER.info("Memory Event [KS_MOB]: " + ksPlayer.getName() + " stole mob from " + bot.getName());
	}

	/**
	 * Triggered when a non-party player picks up loot belonging to an LLM bot's mob kill.
	 */
	public void onLootStolen(Player thief, FakePlayer bot, Item item)
	{
		if (thief == null || bot == null || thief.getObjectId() == bot.getObjectId()) return;
		if (bot.isInParty() && bot.getParty().containsPlayer(thief)) return;

		String itemName = item != null ? item.getName() : "item";
		int delta = -15;
		String desc = "Pegou o loot do meu mob (" + itemName + ").";
		LLMMemoryManager.getInstance().recordMemory(
			bot.getObjectId(),
			thief.getObjectId(),
			thief.getName(),
			LLMMemoryManager.EventType.STOLE_LOOT,
			delta,
			desc
		);
		LOGGER.info("Memory Event [STOLE_LOOT]: " + thief.getName() + " took loot from " + bot.getName());
	}

	/**
	 * Triggered when a player attacks an LLM bot in PvP/PK mode.
	 */
	public void onPlayerAttackBot(Player attacker, FakePlayer bot)
	{
		if (attacker == null || bot == null || attacker.getObjectId() == bot.getObjectId()) return;

		int delta = -30;
		String desc = "Me atacou em PvP/PK.";
		LLMMemoryManager.getInstance().recordMemory(
			bot.getObjectId(),
			attacker.getObjectId(),
			attacker.getName(),
			LLMMemoryManager.EventType.PK_ATTACK,
			delta,
			desc
		);
		LOGGER.info("Memory Event [PK_ATTACK]: " + attacker.getName() + " attacked " + bot.getName());
	}
}
