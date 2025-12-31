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
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 * 
 * ---
 * 
 * Portions of this software are derived from the L2JMobius Project, 
 * shared under the MIT License. The original license terms are preserved where 
 * applicable..
 * 
 */
package com.l2journey.gameserver.network.clientpackets;

import com.l2journey.gameserver.data.PetActionData;
import com.l2journey.gameserver.model.actor.Player;
import com.l2journey.gameserver.model.actor.Summon;
import com.l2journey.gameserver.model.actor.enums.player.ShortcutType;
import com.l2journey.gameserver.model.actor.holders.player.Shortcut;
import com.l2journey.gameserver.network.serverpackets.ShortcutRegister;
/**
 * @author KingHanker
 */
public class RequestShortcutReg extends ClientPacket
{
	private ShortcutType _type;
	private int _id;
	private int _slot;
	private int _page;
	private int _level;
	private int _characterType; // 1 - player, 2 - pet
	
	@Override
	protected void readImpl()
	{
		final int typeId = readInt();
		_type = ShortcutType.values()[(typeId < 1) || (typeId > 6) ? 0 : typeId];
		final int slot = readInt();
		_slot = slot % 12;
		_page = slot / 12;
		_id = readInt();
		_level = readInt();
		_characterType = readInt();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getPlayer();
		if ((player == null) || (_page > 10) || (_page < 0))
		{
			return;
		}
		
		// Determine character type based on shortcut type and ID
		int characterType = _characterType;
		
		if (_type == ShortcutType.SKILL)
		{
			// For skills, check if it belongs to player or pet
			if (player.hasSummon())
			{
				final Summon pet = player.getSummon();
				final boolean petHasSkill = pet.getKnownSkill(_id) != null;
				final boolean playerHasSkill = player.getKnownSkill(_id) != null;
				
				if (petHasSkill && !playerHasSkill)
				{
					characterType = 2; // Pet skill
				}
				else
				{
					characterType = 1; // Player skill
				}
			}
			else if (characterType == 0)
			{
				characterType = 1; // Default to player when no pet
			}
		}
		else if (_type == ShortcutType.ACTION)
		{
			// Check if this action ID is a pet/servitor action using PetActionData
			if (PetActionData.isPetAction(_id))
			{
				characterType = 2; // Pet action
			}
			else if (characterType == 0)
			{
				characterType = 1; // Player action
			}
		}
		else if (characterType == 0)
		{
			characterType = 1; // Default to player for other shortcuts
		}
		
		final Shortcut sc = new Shortcut(_slot, _page, _type, _id, _level, characterType);
		player.registerShortcut(sc);
		player.sendPacket(new ShortcutRegister(sc));
	}
}
