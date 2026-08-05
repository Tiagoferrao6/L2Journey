package com.l2journey.gameserver.managers;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.l2journey.gameserver.model.actor.instance.FakePlayer;

/**
 * Meta-cognitive LLM Diagnostic Watchdog Engine (Level 2 Autonomy).
 * Detects recurring bot failures (>= 3 consecutive failures), formats diagnostic prompts
 * containing execution traces and capabilities, and executes dynamic JavaScript recovery interventions.
 */
public class LLMDiagnosticEngine
{
	private static final Logger LOGGER = Logger.getLogger(LLMDiagnosticEngine.class.getName());
	private static final int FAILURE_THRESHOLD = 3;

	protected LLMDiagnosticEngine()
	{
		LOGGER.info("LLMDiagnosticEngine: Initialized Meta-Cognitive JS Diagnostic Watchdog.");
	}

	public static LLMDiagnosticEngine getInstance()
	{
		return SingletonHolder.INSTANCE;
	}

	private static class SingletonHolder
	{
		protected static final LLMDiagnosticEngine INSTANCE = new LLMDiagnosticEngine();
	}

	/**
	 * Evaluates bot execution health and triggers diagnostic intervention if consecutive failure threshold (3) is reached.
	 */
	public boolean checkBotHealth(FakePlayer bot)
	{
		if (bot == null || !bot.isOnline()) return false;

		BotExecutionTrace trace = LLMCompanionManager.getInstance().getBotTrace(bot);
		if (trace != null && trace.getConsecutiveFailures() >= FAILURE_THRESHOLD)
		{
			LOGGER.warning("LLMDiagnosticEngine: DETECTED " + trace.getConsecutiveFailures() + " CONSECUTIVE FAILURES for "
				+ bot.getName() + " [Category: " + trace.getLastFailureCategory() + "]. Triggering LLM Self-Healing Watchdog.");
			
			String prompt = buildDiagnosticPrompt(bot, trace);
			triggerDiagnosticIntervention(bot, trace, prompt);
			return true;
		}
		return false;
	}

	/**
	 * Constructs a comprehensive meta-cognitive diagnostic prompt containing bot state, trace logs, and capabilities.
	 */
	public String buildDiagnosticPrompt(FakePlayer bot, BotExecutionTrace trace)
	{
		StringBuilder sb = new StringBuilder();
		sb.append("=== LLM COMPANION SELF-HEALING DIAGNOSTIC PROMPT ===\n");
		sb.append("Bot Name: ").append(bot != null ? bot.getName() : "PaladinBot").append("\n");
		sb.append("Class: ").append(bot != null ? bot.getActiveClass() : 0).append(" (Nv. ").append(bot != null ? bot.getLevel() : 1).append(")\n");
		sb.append("Location: X=").append(bot != null ? bot.getX() : 0).append(", Y=").append(bot != null ? bot.getY() : 0).append(", Z=").append(bot != null ? bot.getZ() : 0).append("\n");
		sb.append("Adena: ").append(bot != null ? bot.getInventory().getAdena() : 0).append("\n");
		sb.append("Consecutive Failures: ").append(trace != null ? trace.getConsecutiveFailures() : 0).append("\n");
		sb.append("Last Failure Category: ").append(trace != null ? trace.getLastFailureCategory() : "NONE").append("\n\n");

		sb.append("--- CIRCULAR TRACE LOGS (LAST 30 EVENTS) ---\n");
		sb.append(trace != null ? trace.getFormattedTrace() : "");
		sb.append("\n");

		sb.append("--- CAPABILITIES CATALOG & ACCESSIBLE JS BINDINGS ---\n");
		sb.append("- bot.clearAIIntention(): Reset bot AI intention\n");
		sb.append("- bot.teleToLocation(x, y, z): Emergency teleport\n");
		sb.append("- LLMCompanionManager.getInstance().applyShopCooldown(bot): Set 60s shop cooldown\n");
		sb.append("- TownWaypointMeshManager.getInstance().navigateBotAlongRoute(bot, routeKey, null)\n\n");

		sb.append("--- RESPONSE INSTRUCTIONS ---\n");
		sb.append("Formule a intervenção em formato JSON:\n");
		sb.append("{\n");
		sb.append("  \"script_name\": \"unstick_bot.js\",\n");
		sb.append("  \"reason\": \"Motivo da intervenção\",\n");
		sb.append("  \"js_code\": \"bot.getAI().setIntention(com.l2journey.gameserver.ai.Intention.IDLE, null); LLMCompanionManager.getInstance().applyShopCooldown(bot); bot.teleToLocation(-14347, 123622, -3120);\"\n");
		sb.append("}\n");

		return sb.toString();
	}

	/**
	 * Triggers dynamic diagnostic recovery, generating or executing a JavaScript intervention script.
	 */
	public void triggerDiagnosticIntervention(FakePlayer bot, BotExecutionTrace trace, String prompt)
	{
		// Default fallback self-healing script code if offline or API unavailable
		String defaultJsCode = "bot.getAI().setIntention(com.l2journey.gameserver.ai.Intention.IDLE, null);\n"
			+ "LLMCompanionManager.getInstance().applyShopCooldown(bot);\n"
			+ "bot.teleToLocation(-14347, 123622, -3120);";

		String scriptName = "unstick_" + bot.getName().toLowerCase() + ".js";

		// Save script file to data/scripts/ai_interventions/
		JavaScriptRuntimeEngine.getInstance().saveInterventionScript(scriptName, defaultJsCode);

		// Execute script via JavaScriptRuntimeEngine
		boolean success = JavaScriptRuntimeEngine.getInstance().executeIntervention(bot, scriptName, defaultJsCode);
		if (success)
		{
			trace.recordSuccess();
			trace.addLog("AUTORRECUPERAÇÃO: Intervenção JS '" + scriptName + "' executada com sucesso.");
			LOGGER.info("LLMDiagnosticEngine: Successfully executed self-healing JS intervention for " + bot.getName());
		}
		else
		{
			LOGGER.severe("LLMDiagnosticEngine: Failed to execute self-healing JS intervention for " + bot.getName());
		}
	}
}
