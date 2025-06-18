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

import java.util.Set;

import com.l2journey.Config;
import com.l2journey.gameserver.data.xml.PetDataTable;
import com.l2journey.gameserver.data.xml.SkillData;
import com.l2journey.gameserver.handler.IItemHandler;
import com.l2journey.gameserver.model.actor.Playable;
import com.l2journey.gameserver.model.actor.Player;
import com.l2journey.gameserver.model.actor.instance.Pet;
import com.l2journey.gameserver.model.item.enums.ItemProcessType;
import com.l2journey.gameserver.model.item.instance.Item;
import com.l2journey.gameserver.model.skill.Skill;
import com.l2journey.gameserver.model.skill.holders.SkillHolder;
import com.l2journey.gameserver.network.SystemMessageId;
import com.l2journey.gameserver.network.serverpackets.MagicSkillUse;
import com.l2journey.gameserver.network.serverpackets.SystemMessage;

/**
 * @author Kerberos, Zoey76
 */
public class PetFood implements IItemHandler
{
	@Override
	public boolean useItem(Playable playable, Item item, boolean forceUse)
	{
		if (playable.isPet() && !playable.asPet().canEatFoodId(item.getId()))
		{
			playable.sendPacket(SystemMessageId.THIS_PET_CANNOT_USE_THIS_ITEM);
			return false;
		}
		
		final SkillHolder[] skills = item.getTemplate().getSkills();
		if (skills != null)
		{
			for (SkillHolder sk : skills)
			{
				useFood(playable, sk.getSkillId(), sk.getSkillLevel(), item);
			}
		}
		return true;
	}
	
	private boolean useFood(Playable activeChar, int skillId, int skillLevel, Item item)
	{
		final Skill skill = SkillData.getInstance().getSkill(skillId, skillLevel);
		if (skill != null)
		{
			if (activeChar.isPet())
			{
				final Pet pet = activeChar.asPet();
				if (pet.destroyItem(ItemProcessType.NONE, item.getObjectId(), 1, null, false))
				{
					pet.broadcastPacket(new MagicSkillUse(pet, pet, skillId, skillLevel, 0, 0));
					pet.setCurrentFed(pet.getCurrentFed() + (skill.getFeed() * Config.PET_FOOD_RATE));
					pet.broadcastStatusUpdate();
					if (pet.isHungry())
					{
						pet.sendPacket(SystemMessageId.YOUR_PET_ATE_A_LITTLE_BUT_IS_STILL_HUNGRY);
					}
					return true;
				}
			}
			else if (activeChar.isPlayer())
			{
				final Player player = activeChar.asPlayer();
				if (player.isMounted())
				{
					final Set<Integer> foodIds = PetDataTable.getInstance().getPetData(player.getMountNpcId()).getFood();
					if (foodIds.contains(item.getId()) && player.destroyItem(ItemProcessType.NONE, item.getObjectId(), 1, null, false))
					{
						player.broadcastPacket(new MagicSkillUse(player, player, skillId, skillLevel, 0, 0));
						player.setCurrentFeed(player.getCurrentFeed() + skill.getFeed());
						return true;
					}
				}
				final SystemMessage sm = new SystemMessage(SystemMessageId.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS);
				sm.addItemName(item);
				player.sendPacket(sm);
			}
		}
		return false;
	}
}