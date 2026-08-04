package com.l2journey.gameserver.managers;

/**
 * Data Model for AI Persona Profiles.
 * Decouples cognitive personality traits and tactical behaviors from character class.
 */
public class AIPersonaProfile
{
	public enum CombatStyle
	{
		CALCULATING_KITER,
		HARDCORE_HEALER,
		FRONTLINE_TANKER_AOE
	}

	private final String _name;
	private final String _archetype;
	private final CombatStyle _combatStyle;
	private final String _description;

	public AIPersonaProfile(String name, String archetype, CombatStyle combatStyle, String description)
	{
		_name = name;
		_archetype = archetype;
		_combatStyle = combatStyle;
		_description = description;
	}

	public String getName()
	{
		return _name;
	}

	public String getArchetype()
	{
		return _archetype;
	}

	public CombatStyle getCombatStyle()
	{
		return _combatStyle;
	}

	public String getDescription()
	{
		return _description;
	}
}
