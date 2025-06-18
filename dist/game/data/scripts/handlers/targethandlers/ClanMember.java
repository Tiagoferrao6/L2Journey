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
import com.l2journey.gameserver.model.World;
import com.l2journey.gameserver.model.WorldObject;
import com.l2journey.gameserver.model.actor.Creature;
import com.l2journey.gameserver.model.actor.Npc;
import com.l2journey.gameserver.model.skill.Skill;
import com.l2journey.gameserver.model.skill.targets.TargetType;

/**
 * @author UnAfraid
 */
public class ClanMember implements ITargetTypeHandler
{
	@Override
	public List<WorldObject> getTargetList(Skill skill, Creature creature, boolean onlyFirst, Creature target)
	{
		final List<WorldObject> targetList = new LinkedList<>();
		if (creature.isNpc())
		{
			// for buff purposes, returns friendly mobs nearby and mob itself
			final Npc npc = creature.asNpc();
			if ((npc.getTemplate().getClans() == null) || npc.getTemplate().getClans().isEmpty())
			{
				targetList.add(creature);
				return targetList;
			}
			
			for (Npc newTarget : World.getInstance().getVisibleObjectsInRange(creature, Npc.class, skill.getCastRange()))
			{
				if (newTarget.isNpc() && npc.isInMyClan(newTarget))
				{
					targetList.add(newTarget);
					break;
				}
			}
			
			if (targetList.isEmpty())
			{
				targetList.add(npc);
			}
		}
		
		return targetList;
	}
	
	@Override
	public Enum<TargetType> getTargetType()
	{
		return TargetType.CLAN_MEMBER;
	}
}
