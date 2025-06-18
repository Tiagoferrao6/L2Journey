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
package com.l2journey.gameserver.network.clientpackets;

import com.l2journey.Config;
import com.l2journey.gameserver.managers.MailManager;
import com.l2journey.gameserver.managers.PunishmentManager;
import com.l2journey.gameserver.model.Message;
import com.l2journey.gameserver.model.actor.Player;
import com.l2journey.gameserver.model.zone.ZoneId;
import com.l2journey.gameserver.network.SystemMessageId;
import com.l2journey.gameserver.network.serverpackets.ExChangePostState;

/**
 * @author Migi, DS
 */
public class RequestDeleteSentPost extends ClientPacket
{
	private static final int BATCH_LENGTH = 4; // length of the one item
	
	int[] _msgIds = null;
	
	@Override
	protected void readImpl()
	{
		final int count = readInt();
		if ((count <= 0) || (count > Config.MAX_ITEM_IN_PACKET) || ((count * BATCH_LENGTH) != remaining()))
		{
			return;
		}
		
		_msgIds = new int[count];
		for (int i = 0; i < count; i++)
		{
			_msgIds[i] = readInt();
		}
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getPlayer();
		if ((player == null) || (_msgIds == null) || !Config.ALLOW_MAIL)
		{
			return;
		}
		
		if (!player.isInsideZone(ZoneId.PEACE))
		{
			player.sendPacket(SystemMessageId.YOU_CANNOT_RECEIVE_OR_SEND_MAIL_WITH_ATTACHED_ITEMS_IN_NON_PEACE_ZONE_REGIONS);
			return;
		}
		
		for (int msgId : _msgIds)
		{
			final Message msg = MailManager.getInstance().getMessage(msgId);
			if (msg == null)
			{
				continue;
			}
			if (msg.getSenderId() != player.getObjectId())
			{
				PunishmentManager.handleIllegalPlayerAction(player, player + " tried to delete not own post!", Config.DEFAULT_PUNISH);
				return;
			}
			
			if (msg.hasAttachments() || msg.isDeletedBySender())
			{
				return;
			}
			
			msg.setDeletedBySender();
		}
		player.sendPacket(new ExChangePostState(false, _msgIds, Message.DELETED));
	}
}
