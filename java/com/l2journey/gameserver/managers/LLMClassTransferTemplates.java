package com.l2journey.gameserver.managers;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * 1st Class Transfer Quest Templates for Human, Elf, and Dark Elf races.
 * Maps base classes (Level 1-20) to their respective 1st Class Transfer Quests and target Class IDs.
 */
public class LLMClassTransferTemplates
{
	private static final Logger LOGGER = Logger.getLogger(LLMClassTransferTemplates.class.getName());

	public static class ClassTransferTemplate
	{
		private final int _questId;
		private final String _questName;
		private final int _startNpcId;
		private final int _targetMobId;
		private final int _requiredItems;
		private final int _rewardItemTokenId;
		private final int _resultClassId;

		public ClassTransferTemplate(int questId, String questName, int startNpcId, int targetMobId, int requiredItems, int rewardItemTokenId, int resultClassId)
		{
			_questId = questId;
			_questName = questName;
			_startNpcId = startNpcId;
			_targetMobId = targetMobId;
			_requiredItems = requiredItems;
			_rewardItemTokenId = rewardItemTokenId;
			_resultClassId = resultClassId;
		}

		public int getQuestId() { return _questId; }
		public String getQuestName() { return _questName; }
		public int getStartNpcId() { return _startNpcId; }
		public int getTargetMobId() { return _targetMobId; }
		public int getRequiredItems() { return _requiredItems; }
		public int getRewardItemTokenId() { return _rewardItemTokenId; }
		public int getResultClassId() { return _resultClassId; }
	}

	private final Map<Integer, ClassTransferTemplate> _templatesByClassId = new HashMap<>();

	protected LLMClassTransferTemplates()
	{
		initTemplates();
		LOGGER.info("LLMClassTransferTemplates: Registered " + _templatesByClassId.size() + " 1st Class Transfer quest templates.");
	}

	public static LLMClassTransferTemplates getInstance()
	{
		return SingletonHolder.INSTANCE;
	}

	private static class SingletonHolder
	{
		protected static final LLMClassTransferTemplates INSTANCE = new LLMClassTransferTemplates();
	}

	private void initTemplates()
	{
		// Human Fighter (Base Class 0) -> Knight (Class 9) [Quest 37]
		_templatesByClassId.put(9, new ClassTransferTemplate(37, "37_PathToHumanKnight", 30037, 20035, 10, 1145, 9));

		// Human Fighter (Base Class 0) -> Warrior (Class 1) [Quest 35]
		_templatesByClassId.put(1, new ClassTransferTemplate(35, "35_PathToWarrior", 30010, 20035, 10, 1138, 1));

		// Human Fighter (Base Class 0) -> Rogue (Class 7) [Quest 39]
		_templatesByClassId.put(7, new ClassTransferTemplate(39, "39_PathToRogue", 30035, 20035, 10, 1153, 7));

		// Elven Fighter (Base Class 18) -> Elven Scout (Class 22) [Quest 40]
		_templatesByClassId.put(22, new ClassTransferTemplate(40, "40_PathToElvenScout", 30328, 20053, 10, 1207, 22));

		// Dark Fighter (Base Class 31) -> Palus Knight (Class 35) [Quest 42]
		_templatesByClassId.put(35, new ClassTransferTemplate(42, "42_PathToPalusKnight", 30384, 20067, 10, 1237, 35));
	}

	public ClassTransferTemplate getTemplateForTargetClass(int targetClassId)
	{
		return _templatesByClassId.get(targetClassId);
	}
}
