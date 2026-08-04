package com.l2journey.gameserver.managers;

import java.util.logging.Logger;

import com.l2journey.gameserver.data.xml.impl.FakePlayerEquipmentData;
import com.l2journey.gameserver.model.actor.instance.FakePlayer;
import com.l2journey.gameserver.network.enums.ChatType;
import com.l2journey.gameserver.network.serverpackets.CreatureSay;

/**
 * Handles automated Class Change execution upon quest token presentation or Level 20 milestone completion.
 */
public class LLMClassChangeManager
{
	private static final Logger LOGGER = Logger.getLogger(LLMClassChangeManager.class.getName());

	protected LLMClassChangeManager()
	{
		LOGGER.info("LLMClassChangeManager: Initialized Class Transfer Automation Manager.");
	}

	public static LLMClassChangeManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}

	private static class SingletonHolder
	{
		protected static final LLMClassChangeManager INSTANCE = new LLMClassChangeManager();
	}

	/**
	 * Executes 1st Class Transfer for a bot when Level 20 and target class criteria are met.
	 */
	public boolean performClassTransfer(FakePlayer bot, int targetClassId)
	{
		if (bot == null || !bot.isOnline()) return false;

		if (bot.getLevel() < 20)
		{
			LOGGER.warning("LLMClassChangeManager: Bot " + bot.getName() + " level " + bot.getLevel() + " is under 20.");
			return false;
		}

		int currentClassId = bot.getActiveClass();
		if (currentClassId == targetClassId)
		{
			LOGGER.info("LLMClassChangeManager: Bot " + bot.getName() + " is already class " + targetClassId);
			return true;
		}

		// Perform class template change
		bot.setClassTemplate(targetClassId);

		// Auto equip D-Grade gear upon 1st Class Transfer
		FakePlayerEquipmentData.autoEquip(bot, FakePlayerEquipmentData.Grade.D_GRADE);

		String msg = String.format("[Quest Solver] Completei com sucesso a 1ª Mudança de Classe para %s! (Nv. %d)",
			bot.getTemplate().getPlayerClass().toString(), bot.getLevel());
		bot.broadcastPacket(new CreatureSay(bot, ChatType.GENERAL, bot.getName(), msg));

		LOGGER.info("LLMClassChangeManager: Successfully performed 1st Class Transfer for " + bot.getName() + " to class " + targetClassId);
		return true;
	}
}
