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
import com.l2journey.gameserver.network.serverpackets.ExReplyReceivedPost;

/**
 * @author Migi, DS
 */
public class RequestReceivedPost extends ClientPacket
{
	private int _msgId;
	
	@Override
	protected void readImpl()
	{
		_msgId = readInt();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getPlayer();
		if ((player == null) || !Config.ALLOW_MAIL)
		{
			return;
		}
		
		final Message msg = MailManager.getInstance().getMessage(_msgId);
		if (msg == null)
		{
			return;
		}
		
		if (!player.isInsideZone(ZoneId.PEACE) && msg.hasAttachments())
		{
			player.sendPacket(SystemMessageId.YOU_CANNOT_RECEIVE_OR_SEND_MAIL_WITH_ATTACHED_ITEMS_IN_NON_PEACE_ZONE_REGIONS);
			return;
		}
		
		if (msg.getReceiverId() != player.getObjectId())
		{
			PunishmentManager.handleIllegalPlayerAction(player, player + " tried to receive not own post!", Config.DEFAULT_PUNISH);
			return;
		}
		
		if (msg.isDeletedByReceiver())
		{
			return;
		}
		
		player.sendPacket(new ExReplyReceivedPost(msg));
		player.sendPacket(new ExChangePostState(true, _msgId, Message.READED));
		msg.markAsRead();
	}
}
