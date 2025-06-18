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

import com.l2journey.Config;
import com.l2journey.gameserver.handler.ITargetTypeHandler;
import com.l2journey.gameserver.model.WorldObject;
import com.l2journey.gameserver.model.actor.Creature;
import com.l2journey.gameserver.model.actor.Player;
import com.l2journey.gameserver.model.skill.Skill;
import com.l2journey.gameserver.model.skill.targets.TargetType;
import com.l2journey.gameserver.util.LocationUtil;

/**
 * @author UnAfraid
 */
public class PartyNotMe implements ITargetTypeHandler
{
	@Override
	public List<WorldObject> getTargetList(Skill skill, Creature creature, boolean onlyFirst, Creature target)
	{
		final List<WorldObject> targetList = new LinkedList<>();
		if (creature.getParty() != null)
		{
			final List<Player> partyList = creature.getParty().getMembers();
			for (Player partyMember : partyList)
			{
				if ((partyMember != null) && !partyMember.isDead() && (partyMember != creature) && LocationUtil.checkIfInRange(Config.ALT_PARTY_RANGE, creature, partyMember, true) && ((skill.getAffectRange() <= 0) || LocationUtil.checkIfInRange(skill.getAffectRange(), creature, partyMember, true)))
				{
					targetList.add(partyMember);
					if ((partyMember.getSummon() != null) && !partyMember.getSummon().isDead())
					{
						targetList.add(partyMember.getSummon());
					}
				}
			}
		}
		return targetList;
	}
	
	@Override
	public Enum<TargetType> getTargetType()
	{
		return TargetType.PARTY_NOTME;
	}
}
