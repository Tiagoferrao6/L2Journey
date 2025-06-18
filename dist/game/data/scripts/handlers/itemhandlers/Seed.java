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
package handlers.itemhandlers;

import com.l2journey.Config;
import com.l2journey.gameserver.handler.IItemHandler;
import com.l2journey.gameserver.managers.CastleManorManager;
import com.l2journey.gameserver.managers.MapRegionManager;
import com.l2journey.gameserver.model.WorldObject;
import com.l2journey.gameserver.model.actor.Playable;
import com.l2journey.gameserver.model.actor.Player;
import com.l2journey.gameserver.model.actor.instance.Chest;
import com.l2journey.gameserver.model.actor.instance.Monster;
import com.l2journey.gameserver.model.item.instance.Item;
import com.l2journey.gameserver.model.skill.holders.SkillHolder;
import com.l2journey.gameserver.network.SystemMessageId;
import com.l2journey.gameserver.network.serverpackets.ActionFailed;

/**
 * @author l3x
 */
public class Seed implements IItemHandler
{
	@Override
	public boolean useItem(Playable playable, Item item, boolean forceUse)
	{
		if (!Config.ALLOW_MANOR)
		{
			return false;
		}
		else if (!playable.isPlayer())
		{
			playable.sendPacket(SystemMessageId.YOUR_PET_CANNOT_CARRY_THIS_ITEM);
			return false;
		}
		
		final WorldObject tgt = playable.getTarget();
		if ((tgt == null) || !tgt.isNpc())
		{
			playable.sendPacket(SystemMessageId.INVALID_TARGET);
			return false;
		}
		else if (!tgt.isMonster() || tgt.asMonster().isRaid() || (tgt instanceof Chest))
		{
			playable.sendPacket(SystemMessageId.THE_TARGET_IS_UNAVAILABLE_FOR_SEEDING);
			return false;
		}
		
		final Monster target = tgt.asMonster();
		if (target.isDead())
		{
			playable.sendPacket(SystemMessageId.INVALID_TARGET);
			return false;
		}
		else if (target.isSeeded())
		{
			playable.sendPacket(ActionFailed.STATIC_PACKET);
			return false;
		}
		
		final com.l2journey.gameserver.model.Seed seed = CastleManorManager.getInstance().getSeed(item.getId());
		if (seed == null)
		{
			return false;
		}
		else if (seed.getCastleId() != MapRegionManager.getInstance().getAreaCastle(playable)) // TODO: replace me with tax zone
		{
			playable.sendPacket(SystemMessageId.THIS_SEED_MAY_NOT_BE_SOWN_HERE);
			return false;
		}
		
		final Player player = playable.asPlayer();
		target.setSeeded(seed, player);
		
		final SkillHolder[] skills = item.getTemplate().getSkills();
		if (skills != null)
		{
			for (SkillHolder sk : skills)
			{
				player.useMagic(sk.getSkill(), false, false);
			}
		}
		return true;
	}
}