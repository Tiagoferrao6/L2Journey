package com.l2journey.gameserver.managers;

import java.util.logging.Logger;

import com.l2journey.gameserver.model.World;
import com.l2journey.gameserver.model.actor.Player;
import com.l2journey.gameserver.model.actor.instance.FakePlayer;
import com.l2journey.gameserver.network.enums.ChatType;

/**
 * Router & Handler for intercepting incoming player chat messages (Say2),
 * managing LLM personas, rate limiting, and triggering AI chat responses.
 */
public class LLMChatHandler
{
	private static final Logger LOGGER = Logger.getLogger(LLMChatHandler.class.getName());

	protected LLMChatHandler()
	{
		LOGGER.info("LLMChatHandler: Initialized RPG Socialization & Chat Router.");
	}

	public static LLMChatHandler getInstance()
	{
		return SingletonHolder.INSTANCE;
	}

	private static class SingletonHolder
	{
		protected static final LLMChatHandler INSTANCE = new LLMChatHandler();
	}

	/**
	 * Intercepts incoming chat from Say2 packet.
	 */
	public void handleIncomingChat(Player sender, ChatType chatType, String targetName, String text)
	{
		if (sender == null || text == null || text.trim().isEmpty()) return;

		// 1. Private Message (Whisper / PM) directed to a Bot
		if (chatType == ChatType.WHISPER && targetName != null)
		{
			Player targetPlayer = World.getInstance().getPlayer(targetName);
			if (targetPlayer instanceof FakePlayer)
			{
				FakePlayer bot = (FakePlayer) targetPlayer;
				processBotChatResponse(bot, sender, chatType, text);
			}
		}
		// 2. Party or Clan Chat
		else if (chatType == ChatType.PARTY || chatType == ChatType.CLAN || chatType == ChatType.SHOUT)
		{
			if (sender.isInParty())
			{
				sender.getParty().getMembers().stream()
					.filter(member -> member instanceof FakePlayer)
					.map(member -> (FakePlayer) member)
					.forEach(bot -> processBotChatResponse(bot, sender, chatType, text));
			}
		}
	}

	/**
	 * Processes rate limiting, prompt construction with Persona, and dispatches LLM completion.
	 */
	public void processBotChatResponse(FakePlayer bot, Player sender, ChatType chatType, String text)
	{
		if (bot == null || sender == null) return;

		// Check rate limiting (Task 3)
		if (!LLMRateLimiter.getInstance().canSendMessage(bot.getObjectId(), chatType.name()))
		{
			LOGGER.fine("LLMChatHandler: Rate limit hit for bot " + bot.getName() + " on channel " + chatType.name());
			return;
		}

		// Register message timestamp
		LLMRateLimiter.getInstance().registerMessage(bot.getObjectId(), chatType.name());

		// Build prompt using Persona (Task 2 & 4)
		String prompt = LLMPersonaManager.getInstance().buildPersonaPrompt(bot, sender, chatType.name(), text);

		// Send to LLM Provider (Gemini / Ollama)
		LLMClient.getInstance().generateAsync(prompt, response -> {
			if (response != null && !response.trim().isEmpty())
			{
				LLMPersonaManager.getInstance().dispatchBotSay(bot, sender, chatType, response.trim());
			}
			else
			{
				String fallback = LLMPersonaManager.getInstance().getFallbackResponse(text);
				LLMPersonaManager.getInstance().dispatchBotSay(bot, sender, chatType, fallback);
			}
		});
	}
}
