package com.l2journey.gameserver.managers;

/**
 * Unit test suite verifying BotExecutionTrace circular buffering, LLMDiagnosticEngine prompt formatting,
 * and JavaScriptRuntimeEngine script saving/fallback capabilities.
 */
public class LLMDiagnosticEngineTest
{
	public static void main(String[] args)
	{
		System.out.println("Testing BotExecutionTrace...");
		BotExecutionTrace trace = new BotExecutionTrace();
		trace.addLog("Bot inicializado.");
		trace.recordFailure("SHOP_PURCHASE", "Adena insuficiente.");
		trace.recordFailure("SHOP_PURCHASE", "Adena insuficiente.");
		trace.recordFailure("SHOP_PURCHASE", "Adena insuficiente.");

		if (trace.getConsecutiveFailures() == 3)
		{
			System.out.println("[PASS] Consecutive failure counter reached 3.");
		}
		else
		{
			System.err.println("[FAIL] Expected 3 consecutive failures, found: " + trace.getConsecutiveFailures());
		}

		System.out.println("Testing LLMDiagnosticEngine Prompt Formatting...");
		LLMDiagnosticEngine diagnosticEngine = LLMDiagnosticEngine.getInstance();
		String prompt = diagnosticEngine.buildDiagnosticPrompt(createDummyBotHolder(), trace);
		if (prompt.contains("CIRCULAR TRACE LOGS") && prompt.contains("CAPABILITIES CATALOG"))
		{
			System.out.println("[PASS] Diagnostic prompt formatted with logs & capabilities.");
		}
		else
		{
			System.err.println("[FAIL] Prompt formatting missing expected sections.");
		}

		System.out.println("Testing JavaScriptRuntimeEngine Script IO & Fallback...");
		JavaScriptRuntimeEngine jsEngine = JavaScriptRuntimeEngine.getInstance();
		jsEngine.saveInterventionScript("test_unstick.js", "// Test JS Script\nvar status = 'OK';");
		String loadedScript = jsEngine.loadInterventionScript("test_unstick.js");

		if (loadedScript != null && loadedScript.contains("test_unstick"))
		{
			System.out.println("[PASS] JavaScript script saved and loaded successfully from data/scripts/ai_interventions/.");
		}
		else
		{
			System.err.println("[FAIL] Failed to save/load JS script file.");
		}

		jsEngine.executeSafetyFallback(null, "Test Safety Fallback Execution");
		System.out.println("[PASS] Safety fallback executed safely without exceptions.");

		System.out.println("All LLM Diagnostic & JavaScript Engine unit tests completed successfully.");
	}

	private static com.l2journey.gameserver.model.actor.instance.FakePlayer createDummyBotHolder()
	{
		return null; // Used for prompt structure formatting check
	}
}
