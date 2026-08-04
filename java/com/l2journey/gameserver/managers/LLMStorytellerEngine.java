package com.l2journey.gameserver.managers;

import java.util.function.Consumer;
import java.util.logging.Logger;

/**
 * Storyteller Engine for generating live dynamic event narrative descriptions
 * via LLM (Gemini / Ollama) API or fallback RPG templates.
 */
public class LLMStorytellerEngine
{
	private static final Logger LOGGER = Logger.getLogger(LLMStorytellerEngine.class.getName());

	protected LLMStorytellerEngine()
	{
		LOGGER.info("LLMStorytellerEngine: Initialized Storyteller Narrative Generator.");
	}

	public static LLMStorytellerEngine getInstance()
	{
		return SingletonHolder.INSTANCE;
	}

	private static class SingletonHolder
	{
		protected static final LLMStorytellerEngine INSTANCE = new LLMStorytellerEngine();
	}

	/**
	 * Generates event narrative lore asynchronously via LLM or fallback template.
	 */
	public void generateEventLoreAsync(String eventType, String zoneName, Consumer<String> callback)
	{
		String prompt = buildStorytellerPrompt(eventType, zoneName);
		LLMClient.getInstance().generateAsync(prompt, response -> {
			if (response != null && !response.trim().isEmpty())
			{
				callback.accept(response.trim());
			}
			else
			{
				callback.accept(getFallbackLore(eventType, zoneName));
			}
		});
	}

	private String buildStorytellerPrompt(String eventType, String zoneName)
	{
		StringBuilder sb = new StringBuilder();
		sb.append("System Prompt:\n");
		sb.append("Você é o Game Master e Dungeon Master supremo de Lineage 2.\n");
		sb.append("Crie uma narrativa épica e dramática em Português BR para um evento de tipo '").append(eventType).append("' que está acontecendo na região '").append(zoneName).append("'.\n");
		sb.append("REGRA: Máximo 2 frases curtas imersivas.\n\n");
		sb.append("Narrativa do GM:");
		return sb.toString();
	}

	public String getFallbackLore(String eventType, String zoneName)
	{
		if ("RAID".equalsIgnoreCase(eventType))
		{
			return "Uma horda sombria surge nas sombras de " + zoneName + "! Monstros famintos ameaçam os cidadãos. Defendam o reino!";
		}
		if ("BOUNTY".equalsIgnoreCase(eventType))
		{
			return "Um monstro mutante lendário foi avistado em " + zoneName + "! Quem o derrotar receberá uma recompensa valiosa.";
		}
		return "Forças misteriosas se concentram em " + zoneName + "! Unam-se para enfrentar o perigo iminente.";
	}
}
