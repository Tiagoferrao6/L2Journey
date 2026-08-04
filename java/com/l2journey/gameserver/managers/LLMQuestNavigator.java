package com.l2journey.gameserver.managers;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import com.l2journey.gameserver.model.Location;
import com.l2journey.gameserver.model.actor.instance.FakePlayer;

/**
 * Maps Quest NPCs to town and field waypoints and orchestrates end-to-end quest navigation
 * and dialog execution via LLMQuestDialogExecutor.
 */
public class LLMQuestNavigator
{
	private static final Logger LOGGER = Logger.getLogger(LLMQuestNavigator.class.getName());

	public static class QuestNpcTarget
	{
		private final int _npcId;
		private final String _questName;
		private final String _eventName;
		private final Location _location;

		public QuestNpcTarget(int npcId, String questName, String eventName, Location location)
		{
			_npcId = npcId;
			_questName = questName;
			_eventName = eventName;
			_location = location;
		}

		public int getNpcId() { return _npcId; }
		public String getQuestName() { return _questName; }
		public String getEventName() { return _eventName; }
		public Location getLocation() { return _location; }
	}

	private final Map<String, QuestNpcTarget> _questNpcRegistry = new HashMap<>();

	protected LLMQuestNavigator()
	{
		initQuestRegistry();
		LOGGER.info("LLMQuestNavigator: Initialized Quest Location & Navigation Planner.");
	}

	public static LLMQuestNavigator getInstance()
	{
		return SingletonHolder.INSTANCE;
	}

	private static class SingletonHolder
	{
		protected static final LLMQuestNavigator INSTANCE = new LLMQuestNavigator();
	}

	private void initQuestRegistry()
	{
		// Class Transfer Quests
		_questNpcRegistry.put("PALADIN_TRANSFER", new QuestNpcTarget(30031, "Q001_PathToKnight", "30031-01.htm", new Location(-12600, 122500, -3120)));
		_questNpcRegistry.put("HAWKEYE_TRANSFER", new QuestNpcTarget(30032, "Q002_PathToRogue", "30032-01.htm", new Location(-14100, 124100, -3120)));
		_questNpcRegistry.put("BISHOP_TRANSFER", new QuestNpcTarget(30033, "Q003_PathToCleric", "30033-01.htm", new Location(-12600, 122500, -3120)));
	}

	public QuestNpcTarget getQuestTarget(String questKey)
	{
		return _questNpcRegistry.get(questKey);
	}

	/**
	 * Navigates companion bot to target Quest NPC location and executes dialog bypass.
	 */
	public void executeQuestStep(FakePlayer bot, String questKey, Runnable onComplete)
	{
		if (bot == null || !bot.isOnline()) return;

		QuestNpcTarget target = getQuestTarget(questKey);
		if (target == null)
		{
			LOGGER.warning("LLMQuestNavigator: Quest target not registered for key: " + questKey);
			if (onComplete != null) onComplete.run();
			return;
		}

		LOGGER.info("LLMQuestNavigator: " + bot.getName() + " navigating to Quest NPC " + target.getNpcId() + " for " + target.getQuestName());

		TownWaypointMeshManager.getInstance().navigateBotAlongRoute(bot, "GLUDIO_GK_TO_GROCERY", () -> {
			boolean success = LLMQuestDialogExecutor.getInstance().talkToQuestNpc(bot, target.getNpcId(), target.getQuestName(), target.getEventName());
			LOGGER.info("LLMQuestNavigator: Dialog execution result for " + bot.getName() + ": " + success);
			if (onComplete != null) onComplete.run();
		});
	}
}
