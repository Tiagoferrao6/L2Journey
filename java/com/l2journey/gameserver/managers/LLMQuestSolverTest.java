package com.l2journey.gameserver.managers;

import java.util.logging.Logger;

import com.l2journey.gameserver.managers.LLMClassTransferTemplates.ClassTransferTemplate;

/**
 * Unit & Integration test suite for LLM Quest Solver & Class Change Automation.
 */
public class LLMQuestSolverTest
{
	private static final Logger LOGGER = Logger.getLogger(LLMQuestSolverTest.class.getName());

	public static boolean runTests()
	{
		LOGGER.info("Starting LLMQuestSolver unit and integration tests...");
		boolean allPassed = true;

		try
		{
			// Test 1: Class Transfer Templates Mapping
			ClassTransferTemplate knightTpl = LLMClassTransferTemplates.getInstance().getTemplateForTargetClass(9);
			if (knightTpl == null || knightTpl.getQuestId() != 37)
			{
				LOGGER.severe("Test 1 Failed: Expected Quest 37 for Knight.");
				allPassed = false;
			}

			ClassTransferTemplate warriorTpl = LLMClassTransferTemplates.getInstance().getTemplateForTargetClass(1);
			if (warriorTpl == null || warriorTpl.getQuestId() != 35)
			{
				LOGGER.severe("Test 1 Failed: Expected Quest 35 for Warrior.");
				allPassed = false;
			}

			ClassTransferTemplate scoutTpl = LLMClassTransferTemplates.getInstance().getTemplateForTargetClass(22);
			if (scoutTpl == null || scoutTpl.getQuestId() != 40)
			{
				LOGGER.severe("Test 1 Failed: Expected Quest 40 for Elven Scout.");
				allPassed = false;
			}

			// Test 2: Quest State Parser JSON output
			String json = LLMQuestStateParser.getInstance().toJson(null);
			if (!json.contains("activeQuests"))
			{
				LOGGER.severe("Test 2 Failed: QuestStateParser JSON missing activeQuests field.");
				allPassed = false;
			}

			if (allPassed)
			{
				LOGGER.info("All LLMQuestSolver unit tests PASSED successfully.");
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
