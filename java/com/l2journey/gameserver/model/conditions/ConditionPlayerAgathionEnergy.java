/*
 * Copyright (c) 2025 L2Journey Project
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR
 * IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.l2journey.gameserver.model.conditions;

import com.l2journey.gameserver.model.actor.Creature;
import com.l2journey.gameserver.model.item.ItemTemplate;
import com.l2journey.gameserver.model.item.instance.Item;
import com.l2journey.gameserver.model.itemcontainer.Inventory;
import com.l2journey.gameserver.model.skill.Skill;

/**
 * Condition to check if the player's equipped agathion bracelet has enough energy. Used to prevent summoning agathions when energy is depleted.
 * @author KingHanker
 */
public class ConditionPlayerAgathionEnergy extends Condition
{
	private final boolean _hasEnergy;
	
	/**
	 * Instantiates a new condition player agathion energy.
	 * @param hasEnergy true to check if player has agathion energy, false to check if player doesn't have any
	 */
	public ConditionPlayerAgathionEnergy(boolean hasEnergy)
	{
		_hasEnergy = hasEnergy;
	}
	
	@Override
	public boolean testImpl(Creature effector, Creature effected, Skill skill, ItemTemplate itemTemplate)
	{
		if (!effector.isPlayer())
		{
			return false;
		}
		
		final Item bracelet = effector.asPlayer().getInventory().getPaperdollItem(Inventory.PAPERDOLL_LBRACELET);
		if (bracelet == null)
		{
			return !_hasEnergy; // No bracelet = no energy
		}
		
		// Check if the bracelet is an agathion item with energy system
		if (!bracelet.isAgathionItem())
		{
			// Bracelet doesn't use energy system - always allow if checking for energy
			return _hasEnergy;
		}
		
		// Bracelet uses energy system - check actual energy
		final boolean hasEnergy = bracelet.getAgathionEnergy() > 0;
		return _hasEnergy == hasEnergy;
	}
}
