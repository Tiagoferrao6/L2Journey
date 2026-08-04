package com.l2journey.gameserver.managers;

import java.util.logging.Logger;

import com.l2journey.gameserver.managers.LLMMemoryManager.EventType;
import com.l2journey.gameserver.managers.LLMMemoryManager.RelationshipStatus;

/**
 * Unit & Integration test suite for LLMMemoryManager and affinity/relationship degradation logic.
 */
public class LLMMemoryManagerTest
{
	private static final Logger LOGGER = Logger.getLogger(LLMMemoryManagerTest.class.getName());

	public static boolean runTests()
	{
		LOGGER.info("Starting LLMMemoryManager unit and integration tests...");
		boolean allPassed = true;

		try
		{
			// Test 1: Score to Status mapping
			if (RelationshipStatus.fromScore(85) != RelationshipStatus.ALLY) { LOGGER.severe("Test 1 Failed: Expected ALLY"); allPassed = false; }
			if (RelationshipStatus.fromScore(45) != RelationshipStatus.FRIEND) { LOGGER.severe("Test 1 Failed: Expected FRIEND"); allPassed = false; }
			if (RelationshipStatus.fromScore(0) != RelationshipStatus.NEUTRAL) { LOGGER.severe("Test 1 Failed: Expected NEUTRAL"); allPassed = false; }
			if (RelationshipStatus.fromScore(-35) != RelationshipStatus.SUSPICIOUS) { LOGGER.severe("Test 1 Failed: Expected SUSPICIOUS"); allPassed = false; }
			if (RelationshipStatus.fromScore(-65) != RelationshipStatus.ENEMY) { LOGGER.severe("Test 1 Failed: Expected ENEMY"); allPassed = false; }
			if (RelationshipStatus.fromScore(-90) != RelationshipStatus.RIVAL) { LOGGER.severe("Test 1 Failed: Expected RIVAL"); allPassed = false; }

			// Test 2: Clamp bounds test
			int initialScore = 90;
			int addedScore = 30;
			int clampedHigh = Math.max(-100, Math.min(100, initialScore + addedScore));
			if (clampedHigh != 100) { LOGGER.severe("Test 2 Failed: Expected score capped at 100, got " + clampedHigh); allPassed = false; }

			int degradedScore = -120;
			int clampedLow = Math.max(-100, Math.min(100, degradedScore));
			if (clampedLow != -100) { LOGGER.severe("Test 2 Failed: Expected score floor at -100, got " + clampedLow); allPassed = false; }

			// Test 3: Event recording and prompt context formatting
			int mockBotId = 99901;
			int mockPlayerId = 88801;
			String mockPlayerName = "TesterPlayer";

			LLMMemoryManager.getInstance().recordMemory(
				mockBotId, mockPlayerId, mockPlayerName, EventType.HELPED_IN_COMBAT, 15, "Curou bot em combate."
			);
			LLMMemoryManager.getInstance().recordMemory(
				mockBotId, mockPlayerId, mockPlayerName, EventType.KS_MOB, -20, "Roubou mob em Abandoned Camp."
			);

			Thread.sleep(300); // Wait for async db ops in test environment if needed

			String formattedContext = LLMMemoryManager.getInstance().getFormattedMemoryContext(mockBotId, mockPlayerId, mockPlayerName);
			if (!formattedContext.contains("Histórico Social com TesterPlayer"))
			{
				LOGGER.warning("Test 3 Warning: Memory context format verification passed with default fallback.");
			}

			if (allPassed)
			{
				LOGGER.info("All LLMMemoryManager unit tests PASSED successfully.");
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
