/*
 * This file is part of the L2J Mobius project.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package handlers.targethandlers;

import java.util.LinkedList;
import java.util.List;

import com.l2journey.gameserver.handler.ITargetTypeHandler;
import com.l2journey.gameserver.model.WorldObject;
import com.l2journey.gameserver.model.actor.Creature;
import com.l2journey.gameserver.model.actor.Player;
import com.l2journey.gameserver.model.skill.Skill;
import com.l2journey.gameserver.model.skill.targets.TargetType;
import com.l2journey.gameserver.network.SystemMessageId;

/**
 * @author St3eT
 */
public class TargetParty implements ITargetTypeHandler
{
	@Override
	public List<WorldObject> getTargetList(Skill skill, Creature creature, boolean onlyFirst, Creature target)
	{
		final List<WorldObject> targetList = new LinkedList<>();
		
		// Check for null target or any other invalid target
		if ((target == null) || target.isDead() || (target == creature))
		{
			creature.sendPacket(SystemMessageId.THAT_IS_AN_INCORRECT_TARGET);
			return targetList;
		}
		
		final int radius = skill.getAffectRange();
		final Player player = creature.getTarget().asPlayer();
		if (player.isInParty())
		{
			for (Player partyMember : player.getParty().getMembers())
			{
				if ((partyMember == null))
				{
					continue;
				}
				
				if (Skill.addCharacter(player, partyMember, radius, false))
				{
					targetList.add(partyMember);
				}
				
				if (Skill.addSummon(player, partyMember, radius, false))
				{
					targetList.add(partyMember.getSummon());
				}
			}
		}
		else
		{
			targetList.add(target);
		}
		
		return targetList;
	}
	
	@Override
	public Enum<TargetType> getTargetType()
	{
		return TargetType.TARGET_PARTY;
	}
}