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

import java.util.Collections;
import java.util.List;

import com.l2journey.Config;
import com.l2journey.gameserver.handler.ITargetTypeHandler;
import com.l2journey.gameserver.model.WorldObject;
import com.l2journey.gameserver.model.actor.Creature;
import com.l2journey.gameserver.model.actor.Player;
import com.l2journey.gameserver.model.effects.EffectType;
import com.l2journey.gameserver.model.skill.Skill;
import com.l2journey.gameserver.model.skill.targets.TargetType;
import com.l2journey.gameserver.network.SystemMessageId;

/**
 * Corpse Mob target handler.
 * @author UnAfraid, Zoey76
 */
public class CorpseMob implements ITargetTypeHandler
{
	@Override
	public List<WorldObject> getTargetList(Skill skill, Creature creature, boolean onlyFirst, Creature target)
	{
		if ((target == null) || !target.isAttackable() || !target.isDead())
		{
			creature.sendPacket(SystemMessageId.THAT_IS_AN_INCORRECT_TARGET);
			return Collections.emptyList();
		}
		
		if (skill.hasEffectType(EffectType.SUMMON) && target.isServitor())
		{
			final Player targetPlayer = target.asPlayer();
			if ((targetPlayer != null) && (targetPlayer.getObjectId() == creature.getObjectId()))
			{
				return Collections.emptyList();
			}
		}
		
		if (skill.hasEffectType(EffectType.HP_DRAIN) && target.asAttackable().isOldCorpse(creature.asPlayer(), Config.CORPSE_CONSUME_SKILL_ALLOWED_TIME_BEFORE_DECAY, true))
		{
			return Collections.emptyList();
		}
		
		return Collections.singletonList(target);
	}
	
	@Override
	public Enum<TargetType> getTargetType()
	{
		return TargetType.CORPSE_MOB;
	}
}
