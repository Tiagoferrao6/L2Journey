package com.l2journey.gameserver.model.fake;

public class EconomyRecipe
{
	private final int _recipeId;
	private final String _name;
	private final long _minFee;
	private final long _maxFee;

	public EconomyRecipe(int recipeId, String name, long minFee, long maxFee)
	{
		_recipeId = recipeId;
		_name = name;
		_minFee = minFee;
		_maxFee = maxFee;
	}

	public int getRecipeId()
	{
		return _recipeId;
	}

	public String getName()
	{
		return _name;
	}

	public long getMinFee()
	{
		return _minFee;
	}

	public long getMaxFee()
	{
		return _maxFee;
	}
}
