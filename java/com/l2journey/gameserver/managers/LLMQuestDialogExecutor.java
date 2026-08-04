package com.l2journey.gameserver.managers;

import java.util.logging.Logger;

import com.l2journey.gameserver.model.World;
import com.l2journey.gameserver.model.actor.Npc;
import com.l2journey.gameserver.model.actor.instance.FakePlayer;
import com.l2journey.gameserver.model.quest.Quest;
import com.l2journey.gameserver.model.quest.QuestState;
import com.l2journey.gameserver.model.quest.State;

/**
 * Automates Quest NPC target selection, dialog bypass execution, and quest state progression
 * for LLM autonomous bots.
 */
public class LLMQuestDialogExecutor
{
	private static final Logger LOGGER = Logger.getLogger(LLMQuestDialogExecutor.class.getName());

	protected LLMQuestDialogExecutor()
	{
		LOGGER.info("LLMQuestDialogExecutor: Initialized Quest Dialog Bypass Engine.");
	}

	public static LLMQuestDialogExecutor getInstance()
	{
		return SingletonHolder.INSTANCE;
	}

	private static class SingletonHolder
	{
		protected static final LLMQuestDialogExecutor INSTANCE = new LLMQuestDialogExecutor();
	}

	/**
	 * Automates interaction with a Quest NPC for a bot.
	 */
	public boolean talkToQuestNpc(FakePlayer bot, int npcId, String questName, String eventName)
	{
		if (bot == null || !bot.isOnline()) return false;

		Npc targetNpc = null;
		for (Npc visibleNpc : World.getInstance().getVisibleObjects(bot, Npc.class))
		{
			if (visibleNpc != null && visibleNpc.getId() == npcId)
			{
				targetNpc = visibleNpc;
				break;
			}
		}

		if (targetNpc == null)
		{
			LOGGER.fine("LLMQuestDialogExecutor: Quest NPC ID " + npcId + " not found in visible range of " + bot.getName());
			return false;
		}

		// Ensure bot is close to NPC
		if (!bot.isInsideRadius2D(targetNpc, 150))
		{
			bot.teleToLocation(targetNpc.getLocation());
		}

		bot.setTarget(targetNpc);

		// Execute Quest event bypass
		Quest q = QuestManager.getInstance().getQuest(questName);
		if (q != null)
		{
			QuestState qs = bot.getQuestState(questName);
			if (qs == null)
			{
				qs = q.newQuestState(bot);
				qs.setState(State.STARTED);
				qs.setCond(1);
			}

			q.notifyEvent(eventName, targetNpc, bot);
			LOGGER.info("LLMQuestDialogExecutor: " + bot.getName() + " interacted with Quest NPC " + targetNpc.getName() + " for quest " + questName);
			return true;
		}

		return false;
	}

	/**
	 * Advances active quest condition directly when quest mobs are slain.
	 */
	public void advanceQuestCond(FakePlayer bot, String questName, int nextCond)
	{
		if (bot == null) return;
		QuestState qs = bot.getQuestState(questName);
		if (qs != null && qs.isStarted())
		{
			qs.setCond(nextCond);
			LOGGER.info("LLMQuestDialogExecutor: " + bot.getName() + " quest " + questName + " advanced to cond " + nextCond);
		}
	}
}
