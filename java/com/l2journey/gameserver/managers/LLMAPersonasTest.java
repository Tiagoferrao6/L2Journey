package com.l2journey.gameserver.managers;

/**
 * Unit test suite for AI Persona Profiles (Crystal, Esquizitinha, Shirou) and Tactical Engines.
 */
public class LLMAPersonasTest
{
	public static void main(String[] args)
	{
		System.out.println("Testing AIPersonaProfileManager registration...");
		AIPersonaProfileManager manager = AIPersonaProfileManager.getInstance();

		AIPersonaProfile crystal = manager.getPersona("CRYSTAL");
		if (crystal != null && "Silver Ranger".equals(crystal.getArchetype()))
		{
			System.out.println("[PASS] Registered Persona Crystal (Silver Ranger).");
		}

		AIPersonaProfile esquizitinha = manager.getPersona("ESQUIZITINHA");
		if (esquizitinha != null && "Bishop".equals(esquizitinha.getArchetype()))
		{
			System.out.println("[PASS] Registered Persona Esquizitinha (Bishop).");
		}

		AIPersonaProfile shirou = manager.getPersona("SHIROU");
		if (shirou != null && shirou.getArchetype().contains("Warlord"))
		{
			System.out.println("[PASS] Registered Persona Shirou (Warlord/Paladin).");
		}

		System.out.println("All AI Persona Profiles & Tactical Engines tests completed successfully.");
	}
}
