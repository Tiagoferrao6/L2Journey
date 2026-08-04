package com.l2journey.gameserver.managers;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.l2journey.gameserver.model.actor.Player;
import com.l2journey.gameserver.model.actor.instance.FakePlayer;
import com.l2journey.gameserver.network.enums.ChatType;
import com.l2journey.gameserver.network.serverpackets.CreatureSay;

/**
 * Manages XML personas for LLM bots and handles prompt context assembly & chat packet dispatching.
 */
public class LLMPersonaManager
{
	private static final Logger LOGGER = Logger.getLogger(LLMPersonaManager.class.getName());
	private static final String PERSONAS_XML_PATH = "config/npcs/llm_personas.xml";

	public static class LLMPersona
	{
		private final String _id;
		private final String _tone;
		private final String _language;
		private final String _systemPrompt;

		public LLMPersona(String id, String tone, String language, String systemPrompt)
		{
			_id = id;
			_tone = tone;
			_language = language;
			_systemPrompt = systemPrompt;
		}

		public String getId() { return _id; }
		public String getTone() { return _tone; }
		public String getLanguage() { return _language; }
		public String getSystemPrompt() { return _systemPrompt; }
	}

	private final Map<String, LLMPersona> _personas = new HashMap<>();

	protected LLMPersonaManager()
	{
		loadPersonasXml();
		LOGGER.info("LLMPersonaManager: Loaded " + _personas.size() + " RPG Bot Personas.");
	}

	public static LLMPersonaManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}

	private static class SingletonHolder
	{
		protected static final LLMPersonaManager INSTANCE = new LLMPersonaManager();
	}

	public void loadPersonasXml()
	{
		_personas.clear();
		File xmlFile = new File(PERSONAS_XML_PATH);
		if (!xmlFile.exists())
		{
			xmlFile = new File("dist/game/" + PERSONAS_XML_PATH);
		}

		if (!xmlFile.exists())
		{
			LOGGER.warning("LLMPersonaManager: XML file not found at " + xmlFile.getAbsolutePath() + ". Loading default fallback personas.");
			loadDefaultPersonas();
			return;
		}

		try
		{
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(xmlFile);
			doc.getDocumentElement().normalize();

			NodeList nList = doc.getElementsByTagName("persona");
			for (int i = 0; i < nList.getLength(); i++)
			{
				Element elem = (Element) nList.item(i);
				String id = elem.getAttribute("id");
				String tone = getChildText(elem, "tone", "informal");
				String language = getChildText(elem, "language", "pt_BR");
				String prompt = getChildText(elem, "systemPrompt", "Você é um jogador de Lineage 2.");

				_personas.put(id, new LLMPersona(id, tone, language, prompt));
			}
		}
		catch (Exception e)
		{
			LOGGER.log(Level.WARNING, "LLMPersonaManager: Failed parsing personas XML", e);
			loadDefaultPersonas();
		}
	}

	private void loadDefaultPersonas()
	{
		_personas.put("mentor_paladin", new LLMPersona("mentor_paladin", "honorable, helpful", "pt_BR", "Você é PaladinBot, Paladino em Lineage 2. Ajude os jogadores."));
		_personas.put("hawkeye_archer", new LLMPersona("hawkeye_archer", "tactical, sharp", "pt_BR", "Você é HawkeyeBot, arqueiro em Lineage 2."));
		_personas.put("bishop_healer", new LLMPersona("bishop_healer", "supportive, benevolent", "pt_BR", "Você é BishopBot, sacerdote em Lineage 2."));
	}

	private String getChildText(Element parent, String tagName, String defaultValue)
	{
		NodeList nodes = parent.getElementsByTagName(tagName);
		if (nodes.getLength() > 0)
		{
			return nodes.item(0).getTextContent().trim();
		}
		return defaultValue;
	}

	public LLMPersona getPersonaForBot(FakePlayer bot)
	{
		if (bot == null) return _personas.getOrDefault("mentor_paladin", new LLMPersona("default", "informal", "pt_BR", "Você é um jogador."));
		String name = bot.getName().toLowerCase();
		if (name.contains("hawkeye")) return _personas.getOrDefault("hawkeye_archer", _personas.get("mentor_paladin"));
		if (name.contains("bishop")) return _personas.getOrDefault("bishop_healer", _personas.get("mentor_paladin"));
		return _personas.getOrDefault("mentor_paladin", new LLMPersona("default", "informal", "pt_BR", "Você é um bot."));
	}

	public String buildPersonaPrompt(FakePlayer bot, Player sender, String channel, String userMessage)
	{
		LLMPersona persona = getPersonaForBot(bot);
		StringBuilder sb = new StringBuilder();
		sb.append("System Prompt:\n").append(persona.getSystemPrompt()).append("\n");
		sb.append("Tom: ").append(persona.getTone()).append(" | Canal: ").append(channel).append("\n");
		sb.append("REGRA DE OURO: Responda em no máximo 1 frase em Português BR com gírias de L2.\n\n");

		if (bot != null && sender != null)
		{
			String memoryCtx = LLMMemoryManager.getInstance().getFormattedMemoryContext(bot.getObjectId(), sender.getObjectId(), sender.getName());
			sb.append(memoryCtx).append("\n");
		}

		sb.append("Mensagem de ").append(sender != null ? sender.getName() : "Player").append(": ").append(userMessage).append("\n");
		sb.append("Resposta de ").append(bot != null ? bot.getName() : "Bot").append(":");
		return sb.toString();
	}

	public String getFallbackResponse(String userMessage)
	{
		if (userMessage == null) return "Bora pro farm!";
		String lower = userMessage.toLowerCase();
		if (lower.contains("abandoned camp")) return "Abandoned Camp fica a nordeste da Vila de Gludin! (Coordenadas de Gludio).";
		if (lower.contains("varnish")) return "Varnish dropa bastante em ruins de Despair e Abandoned Camp!";
		if (lower.contains("ajuda") || lower.contains("quest")) return "Tô focado no farm por enquanto, bora juntar pt!";
		return "Bora pra cima dos mobs, foco no level e no farm!";
	}

	public void dispatchBotSay(FakePlayer bot, Player recipient, ChatType chatType, String text)
	{
		if (bot == null || text == null || text.isEmpty()) return;

		if (chatType == ChatType.WHISPER && recipient != null && recipient.isOnline())
		{
			recipient.sendPacket(new CreatureSay(bot, ChatType.WHISPER, bot.getName(), text));
		}
		else if (chatType == ChatType.PARTY && bot.isInParty())
		{
			bot.getParty().broadcastToPartyMembers(bot, new CreatureSay(bot, ChatType.PARTY, bot.getName(), text));
		}
		else
		{
			bot.broadcastPacket(new CreatureSay(bot, chatType, bot.getName(), text));
		}
	}
}
