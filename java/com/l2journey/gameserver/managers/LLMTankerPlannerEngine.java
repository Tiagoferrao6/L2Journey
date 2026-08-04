package com.l2journey.gameserver.managers;

import java.util.logging.Logger;

import com.l2journey.gameserver.model.actor.instance.FakePlayer;

/**
 * Autonomous LLM Decision Planner Engine for the Tanker Companion (PaladinBot).
 * Builds game state snapshots, queries LLMClient asynchronously, and dispatches JSON decisions.
 */
public class LLMTankerPlannerEngine
{
	private static final Logger LOGGER = Logger.getLogger(LLMTankerPlannerEngine.class.getName());

	public enum PlannerAction
	{
		FARM_ZONE,
		GO_TO_SHOP,
		START_QUEST,
		ADVANCE_QUEST,
		REST
	}

	public static class PlannerDecision
	{
		private final PlannerAction _action;
		private final String _target;
		private final String _reason;

		public PlannerDecision(PlannerAction action, String target, String reason)
		{
			_action = action;
			_target = target;
			_reason = reason;
		}

		public PlannerAction getAction() { return _action; }
		public String getTarget() { return _target; }
		public String getReason() { return _reason; }
	}

	protected LLMTankerPlannerEngine()
	{
		LOGGER.info("LLMTankerPlannerEngine: Initialized Tanker LLM Cognitive Planner.");
	}

	public static LLMTankerPlannerEngine getInstance()
	{
		return SingletonHolder.INSTANCE;
	}

	private static class SingletonHolder
	{
		protected static final LLMTankerPlannerEngine INSTANCE = new LLMTankerPlannerEngine();
	}

	/**
	 * Builds a snapshot prompt of the bot's current state and requests an action decision from LLM.
	 */
	public void planNextAction(FakePlayer bot, java.util.function.Consumer<PlannerDecision> callback)
	{
		if (bot == null || !bot.isOnline()) return;

		String snapshot = buildStateSnapshot(bot);
		LOGGER.fine("LLMTankerPlannerEngine: Snapshot for " + bot.getName() + ":\n" + snapshot);

		LLMClient.getInstance().generateAsync(snapshot, llmResponse -> {
			PlannerDecision decision = parseDecisionResponse(bot, llmResponse);
			LOGGER.info("LLMTankerPlannerEngine: Decision for " + bot.getName() + ": " + decision.getAction() + " (" + decision.getReason() + ")");
			if (callback != null) callback.accept(decision);
		});
	}

	public String buildStateSnapshot(FakePlayer bot)
	{
		StringBuilder sb = new StringBuilder();
		sb.append("System Prompt: Você é o planejador cognitivo de IA do PaladinBot (Tanker) no Lineage 2.\n");
		sb.append("Responda estritamente no formato JSON: {\"action\": \"FARM_ZONE|GO_TO_SHOP|START_QUEST|ADVANCE_QUEST|REST\", \"target\": \"nome\", \"reason\": \"motivo\"}\n\n");

		sb.append("Status Atual do PaladinBot:\n");
		sb.append("- Nível: ").append(bot.getLevel()).append("\n");
		sb.append("- Classe: ").append(bot.getTemplate().getPlayerClass().toString()).append("\n");
		sb.append("- HP: ").append((int)bot.getCurrentHp()).append("/").append((int)bot.getMaxHp()).append("\n");
		sb.append("- Adena: ").append(bot.getInventory().getAdena()).append("\n");

		boolean needsShop = BuyListExecutingEngine.getInstance().needsConsumableReplenishment(bot);
		sb.append("- Necessita Suprimentos (Shots/Potions): ").append(needsShop).append("\n");

		if (bot.getLevel() >= 19 && bot.getActiveClass() == 0)
		{
			sb.append("- Quest Recomenada: Q001_PathToKnight (Mudança para Knight Nv 20)\n");
		}
		else if (bot.getLevel() >= 39 && bot.getActiveClass() == 9)
		{
			sb.append("- Quest Recomenada: Q002_PathToPaladin (Mudança para Paladin Nv 40)\n");
		}

		return sb.toString();
	}

	public PlannerDecision parseDecisionResponse(FakePlayer bot, String response)
	{
		if (response != null && response.contains("GO_TO_SHOP"))
		{
			return new PlannerDecision(PlannerAction.GO_TO_SHOP, "Gludio_Grocery", "Suprimentos baixos detectados.");
		}
		else if (response != null && (response.contains("START_QUEST") || response.contains("PathToKnight")))
		{
			return new PlannerDecision(PlannerAction.START_QUEST, "PALADIN_TRANSFER", "Atingiu nível de mudança de classe.");
		}
		else if (bot.getLevel() >= 19 && bot.getActiveClass() == 0)
		{
			// Heuristic fallback for Class Quest
			return new PlannerDecision(PlannerAction.START_QUEST, "PALADIN_TRANSFER", "Fallback heurístico de classe Nv 20.");
		}
		else if (BuyListExecutingEngine.getInstance().needsConsumableReplenishment(bot))
		{
			// Heuristic fallback for Shopping
			return new PlannerDecision(PlannerAction.GO_TO_SHOP, "Gludio_Grocery", "Fallback heurístico de compras.");
		}
		else
		{
			return new PlannerDecision(PlannerAction.FARM_ZONE, "Gludio_Outskirts", "Caça autônoma de mobs.");
		}
	}
}
