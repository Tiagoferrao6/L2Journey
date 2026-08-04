package com.l2journey.gameserver.managers;

import java.util.logging.Logger;

import com.l2journey.gameserver.managers.AutonomousGMManager.GMEventState;

/**
 * Unit & Integration test suite for AutonomousGMManager and LLMStorytellerEngine.
 */
public class AutonomousGMManagerTest
{
	private static final Logger LOGGER = Logger.getLogger(AutonomousGMManagerTest.class.getName());

	public static boolean runTests()
	{
		LOGGER.info("Starting AutonomousGMManager unit and integration tests...");
		boolean allPassed = true;

		try
		{
			// Test 1: Initial state check
			if (AutonomousGMManager.getInstance().getEventState() != GMEventState.IDLE)
			{
				LOGGER.severe("Test 1 Failed: Expected initial state IDLE.");
				allPassed = false;
			}

			// Test 2: Storyteller Fallback Lore
			String lore = LLMStorytellerEngine.getInstance().getFallbackLore("RAID", "Gludio");
			if (!lore.contains("Gludio"))
			{
				LOGGER.severe("Test 2 Failed: Fallback lore missing expected zone name.");
				allPassed = false;
			}

			// Test 3: Event lifecycle trigger
			AutonomousGMManager.getInstance().startDynamicEvent(
				"Teste de Invasao",
				"Lore de teste em Gludio",
				"Gludio",
				-14000, 120000, -3000,
				20001,
				3
			);

			Thread.sleep(200); // Allow async spawn task to run in test context

			GMEventState stateAfterStart = AutonomousGMManager.getInstance().getEventState();
			if (stateAfterStart != GMEventState.IN_PROGRESS && stateAfterStart != GMEventState.STARTING)
			{
				LOGGER.severe("Test 3 Failed: State should be IN_PROGRESS or STARTING, got " + stateAfterStart);
				allPassed = false;
			}

			// Test 4: Event Cleanup
			AutonomousGMManager.getInstance().cleanupEventSpawns();
			if (AutonomousGMManager.getInstance().getEventState() != GMEventState.IDLE)
			{
				LOGGER.severe("Test 4 Failed: Expected state IDLE after cleanup.");
				allPassed = false;
			}

			if (allPassed)
			{
				LOGGER.info("All AutonomousGMManager unit tests PASSED successfully.");
			}
		}
		catch (Exception e)
		{
			LOGGER.severe("Test failure exception: " + e.getMessage());
			allPassed = false;
		}

		return allPassed;
	}
}
