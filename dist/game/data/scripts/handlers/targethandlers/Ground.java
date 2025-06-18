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
import com.l2journey.gameserver.model.actor.Player;
import com.l2journey.gameserver.model.effects.EffectType;
import com.l2journey.gameserver.model.skill.Skill;
import com.l2journey.gameserver.model.skill.targets.TargetType;
import com.l2journey.gameserver.model.zone.ZoneId;

/**
 * @author St3eT
 */
public class Ground implements ITargetTypeHandler
{
	@Override
	public List<WorldObject> getTargetList(Skill skill, Creature creature, boolean onlyFirst, Creature target)
	{
		final List<WorldObject> targetList = new LinkedList<>();
		final Player player = creature.asPlayer();
		final int maxTargets = skill.getAffectLimit();
		final boolean srcInArena = (creature.isInsideZone(ZoneId.PVP) && !creature.isInsideZone(ZoneId.SIEGE));
		World.getInstance().forEachVisibleObject(creature, Creature.class, character ->
		{
			if ((character != null) && character.isInsideRadius2D(player.getCurrentSkillWorldPosition(), skill.getAffectRange()))
			{
				if (!Skill.checkForAreaOffensiveSkills(creature, character, skill, srcInArena))
				{
					return;
				}
				
				if (character.isDoor())
				{
					return;
				}
				
				targetList.add(character);
				
				if ((maxTargets > 0) && (targetList.size() >= maxTargets))
				{
					return;
				}
			}
		});
		
		if (targetList.isEmpty() && skill.hasEffectType(EffectType.SUMMON_NPC))
		{
			targetList.add(creature);
		}
		
		return targetList;
	}
	
	@Override
	public Enum<TargetType> getTargetType()
	{
		return TargetType.GROUND;
	}
}