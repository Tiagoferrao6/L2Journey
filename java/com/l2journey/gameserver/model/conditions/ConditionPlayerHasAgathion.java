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
import com.l2journey.gameserver.model.skill.Skill;

/**
 * The Class ConditionPlayerHasAgathion. Checks if the player has an active Agathion summoned.
 * @author Diego Martins
 */
public class ConditionPlayerHasAgathion extends Condition
{
	private final boolean _hasAgathion;
	
	/**
	 * Instantiates a new condition player has agathion.
	 * @param hasAgathion true to check if player has an agathion, false to check if player doesn't have one
	 */
	public ConditionPlayerHasAgathion(boolean hasAgathion)
	{
		_hasAgathion = hasAgathion;
	}
	
	@Override
	public boolean testImpl(Creature effector, Creature effected, Skill skill, ItemTemplate item)
	{
		if (!effector.isPlayer())
		{
			return false;
		}
		
		final boolean hasAgathion = effector.asPlayer().getAgathionId() > 0;
		return _hasAgathion == hasAgathion;
	}
}
