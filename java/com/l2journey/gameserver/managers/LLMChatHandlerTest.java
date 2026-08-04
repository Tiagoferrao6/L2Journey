package com.l2journey.gameserver.managers;

import java.util.logging.Logger;

import com.l2journey.gameserver.managers.LLMPersonaManager.LLMPersona;

/**
 * Unit & Integration test suite for LLMChatHandler, LLMPersonaManager, and LLMRateLimiter.
 */
public class LLMChatHandlerTest
{
	private static final Logger LOGGER = Logger.getLogger(LLMChatHandlerTest.class.getName());

	public static boolean runTests()
	{
		LOGGER.info("Starting LLMChatHandler unit and integration tests...");
		boolean allPassed = true;

		try
		{
			// Test 1: Persona XML Loading & Fallback
			LLMPersonaManager.getInstance().loadPersonasXml();
			LLMPersona mentor = LLMPersonaManager.getInstance().getPersonaForBot(null);
			if (mentor == null || mentor.getSystemPrompt() == null)
			{
				LOGGER.severe("Test 1 Failed: Persona loading returned null.");
				allPassed = false;
			}

			// Test 2: Rate Limiting
			int testBotId = 77701;
			String channel = "WHISPER";

			LLMRateLimiter.getInstance().reset();
			if (!LLMRateLimiter.getInstance().canSendMessage(testBotId, channel))
			{
				LOGGER.severe("Test 2 Failed: Initial message should be allowed.");
				allPassed = false;
			}

			LLMRateLimiter.getInstance().registerMessage(testBotId, channel);
			if (LLMRateLimiter.getInstance().canSendMessage(testBotId, channel))
			{
				LOGGER.severe("Test 2 Failed: Immediate follow-up message should be rate-limited.");
				allPassed = false;
			}

			LLMRateLimiter.getInstance().reset();
			if (!LLMRateLimiter.getInstance().canSendMessage(testBotId, channel))
			{
				LOGGER.severe("Test 2 Failed: Message should be allowed after reset.");
				allPassed = false;
			}

			// Test 3: Fallback RPG knowledge response
			String campQuery = "Onde fica Abandoned Camp?";
			String fallbackCamp = LLMPersonaManager.getInstance().getFallbackResponse(campQuery);
			if (!fallbackCamp.contains("Gludin"))
			{
				LOGGER.severe("Test 3 Failed: Fallback response missing expected location info.");
				allPassed = false;
			}

			if (allPassed)
			{
				LOGGER.info("All LLMChatHandler unit tests PASSED successfully.");
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
