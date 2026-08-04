package com.l2journey.gameserver.managers;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.l2journey.gameserver.model.actor.Player;
import com.l2journey.gameserver.model.quest.QuestState;
import com.l2journey.gameserver.model.quest.State;

/**
 * Parses and serializes a character's active quests, condition variables,
 * quest items, and target objectives into JSON for consumption by LLM planners.
 */
public class LLMQuestStateParser
{
	private static final Logger LOGGER = Logger.getLogger(LLMQuestStateParser.class.getName());

	public static class QuestGoalInfo
	{
		private final String _questName;
		private final int _cond;
		private final String _state;
		private final String _targetObjective;

		public QuestGoalInfo(String questName, int cond, String state, String targetObjective)
		{
			_questName = questName;
			_cond = cond;
			_state = state;
			_targetObjective = targetObjective;
		}

		public String getQuestName() { return _questName; }
		public int getCond() { return _cond; }
		public String getState() { return _state; }
		public String getTargetObjective() { return _targetObjective; }
	}

	protected LLMQuestStateParser()
	{
		LOGGER.info("LLMQuestStateParser: Initialized Quest Perception Engine.");
	}

	public static LLMQuestStateParser getInstance()
	{
		return SingletonHolder.INSTANCE;
	}

	private static class SingletonHolder
	{
		protected static final LLMQuestStateParser INSTANCE = new LLMQuestStateParser();
	}

	/**
	 * Extracts all active started quests for a player.
	 */
	public List<QuestGoalInfo> getActiveQuestGoals(Player player)
	{
		List<QuestGoalInfo> activeGoals = new ArrayList<>();
		if (player == null) return activeGoals;

		for (com.l2journey.gameserver.model.quest.Quest q : player.getAllActiveQuests())
		{
			QuestState qs = player.getQuestState(q.getName());
			if (qs != null && qs.isStarted())
			{
				String qName = qs.getQuestName();
				int cond = qs.getCond();
				String targetObj = getTargetObjective(qName, cond);
				activeGoals.add(new QuestGoalInfo(qName, cond, "STARTED", targetObj));
			}
		}

		return activeGoals;
	}

	/**
	 * Serializes active quests to a JSON string representation.
	 */
	public String toJson(Player player)
	{
		if (player == null) return "{\"player\":\"Unknown\",\"activeQuests\":[]}";

		List<QuestGoalInfo> goals = getActiveQuestGoals(player);
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("\"player\":\"").append(escapeJson(player.getName())).append("\",");
		sb.append("\"level\":").append(player.getLevel()).append(",");
		sb.append("\"classId\":").append(player.getActiveClass()).append(",");
		sb.append("\"activeQuests\":[");

		for (int i = 0; i < goals.size(); i++)
		{
			QuestGoalInfo g = goals.get(i);
			sb.append("{");
			sb.append("\"questName\":\"").append(escapeJson(g.getQuestName())).append("\",");
			sb.append("\"cond\":").append(g.getCond()).append(",");
			sb.append("\"objective\":\"").append(escapeJson(g.getTargetObjective())).append("\"");
			sb.append("}");
			if (i < goals.size() - 1) sb.append(",");
		}

		sb.append("]}");
		return sb.toString();
	}

	private String getTargetObjective(String questName, int cond)
	{
		String nameLower = questName != null ? questName.toLowerCase() : "";
		if (nameLower.contains("warrior") || nameLower.contains("35_"))
		{
			if (cond == 1) return "Falar com Master Harris em Gludin para iniciar o treino.";
			if (cond == 2) return "Derrotar Tracker Skeletons em Abandoned Camp e coletar 10 Medals of Tracker.";
			if (cond == 3) return "Entregar Medals of Tracker ao Master Harris para receber a Medal of Warrior.";
			return "Troca de Classe pronta para Warrior!";
		}
		if (nameLower.contains("knight") || nameLower.contains("37_"))
		{
			if (cond == 1) return "Falar com High Priest Raymond na Igreja de Gludio.";
			if (cond == 2) return "Caçar Poison Spiders em Gludio e obter 10 Spider Silk Tokens.";
			if (cond == 3) return "Retornar ao High Priest Raymond e receber Mark of Duty.";
			return "Troca de Classe pronta para Human Knight!";
		}
		if (nameLower.contains("rogue") || nameLower.contains("39_"))
		{
			if (cond == 1) return "Falar com Captain Bezique no portão oeste de Gludin.";
			if (cond == 2) return "Caçar Kasha Bears e coletar 10 Kasha Bear Claws.";
			if (cond == 3) return "Retornar ao Captain Bezique e receber Bezique's Letter.";
			return "Troca de Classe pronta para Rogue!";
		}

		return "Progredir na missão " + questName + " (Etapa " + cond + ").";
	}

	private String escapeJson(String input)
	{
		if (input == null) return "";
		return input.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r");
	}
}
