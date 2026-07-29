package com.l2journey.gameserver.model.actor.dna;

/**
 * Data model representing the psychological DNA and behavioral traits of a Fake Hunter bot.
 */
public class HunterDNA
{
	private final String _profileId;
	private final int _preservation;
	private final int _sociability;
	private final int _greed;
	private final int _aggressiveness;
	private final int _altruism;
	private final String _shift;

	public HunterDNA(String profileId, int preservation, int sociability, int greed, int aggressiveness, int altruism, String shift)
	{
		_profileId = profileId;
		_preservation = Math.max(0, Math.min(100, preservation));
		_sociability = Math.max(0, Math.min(100, sociability));
		_greed = Math.max(0, Math.min(100, greed));
		_aggressiveness = Math.max(0, Math.min(100, aggressiveness));
		_altruism = Math.max(0, Math.min(100, altruism));
		_shift = (shift != null && !shift.isEmpty()) ? shift.toUpperCase() : "ALL_DAY";
	}

	public String getProfileId()
	{
		return _profileId;
	}

	public int getPreservation()
	{
		return _preservation;
	}

	public int getSociability()
	{
		return _sociability;
	}

	public int getGreed()
	{
		return _greed;
	}

	public int getAggressiveness()
	{
		return _aggressiveness;
	}

	public int getAltruism()
	{
		return _altruism;
	}

	public String getShift()
	{
		return _shift;
	}

	@Override
	public String toString()
	{
		return "HunterDNA[" + _profileId + ", Pres:" + _preservation + ", Soc:" + _sociability + 
				", Aggro:" + _aggressiveness + ", Shift:" + _shift + "]";
	}
}
