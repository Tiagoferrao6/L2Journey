package com.l2journey.gameserver.model.fake;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class EconomyProfile
{
	private final String _profileId;
	private final List<EconomyItem> _items = new CopyOnWriteArrayList<>();
	private final List<EconomyRecipe> _recipes = new CopyOnWriteArrayList<>();

	public EconomyProfile(String profileId)
	{
		_profileId = profileId;
	}

	public String getProfileId()
	{
		return _profileId;
	}

	public void addItem(EconomyItem item)
	{
		_items.add(item);
	}

	public List<EconomyItem> getItems()
	{
		return _items;
	}

	public void addRecipe(EconomyRecipe recipe)
	{
		_recipes.add(recipe);
	}

	public List<EconomyRecipe> getRecipes()
	{
		return _recipes;
	}
}
