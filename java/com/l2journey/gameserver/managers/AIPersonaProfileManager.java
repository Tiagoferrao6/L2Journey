package com.l2journey.gameserver.managers;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import com.l2journey.gameserver.managers.AIPersonaProfile.CombatStyle;

/**
 * AI Persona Profile Manager (`AIPersonaProfileManager`).
 * Registers, loads, and serves AI Personas (Crystal, Esquizitinha, Shirou) to OOG sessions.
 */
public class AIPersonaProfileManager
{
	private static final Logger LOGGER = Logger.getLogger(AIPersonaProfileManager.class.getName());
	private final Map<String, AIPersonaProfile> _personas = new ConcurrentHashMap<>();

	protected AIPersonaProfileManager()
	{
		registerDefaultPersonas();
		LOGGER.info("AIPersonaProfileManager: Initialized with " + _personas.size() + " AI Persona Profiles.");
	}

	public static AIPersonaProfileManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}

	private static class SingletonHolder
	{
		protected static final AIPersonaProfileManager INSTANCE = new AIPersonaProfileManager();
	}

	private void registerDefaultPersonas()
	{
		// 1. Crystal (Silver Ranger - Caçadora Oportunista e Calculista)
		_personas.put("CRYSTAL", new AIPersonaProfile(
			"Crystal",
			"Silver Ranger",
			CombatStyle.CALCULATING_KITER,
			"Caçadora oportunista, posicionamento max-range, kiting, KS Stunning Shot, PvP Hit&Run + Entangle."
		));

		// 2. Esquizitinha (Bishop / Cardinal - Babá Hardcore e Cirúrgica)
		_personas.put("ESQUIZITINHA", new AIPersonaProfile(
			"Esquizitinha",
			"Bishop",
			CombatStyle.HARDCORE_HEALER,
			"Babá hardcore cirúrgica, Dança dos Limiters (Frenzy/Zealot timing), LoS Cover, Cleanse prioridade 1, Mana Burn, Celestial Shield."
		));

		// 3. Shirou (Warlord / Paladin - Trator da Linha de Frente)
		_personas.put("SHIROU", new AIPersonaProfile(
			"Shirou",
			"Warlord / Paladin",
			CombatStyle.FRONTLINE_TANKER_AOE,
			"Trator da linha de frente, trains 5-10 mobs, Howl + AoE skills, Aggression, Angelic Icon, Shock Stomp KS reaction, UD + Sacrifice."
		));
	}

	public AIPersonaProfile getPersona(String name)
	{
		if (name == null) return null;
		return _personas.get(name.toUpperCase());
	}

	public Map<String, AIPersonaProfile> getAllPersonas()
	{
		return _personas;
	}
}
