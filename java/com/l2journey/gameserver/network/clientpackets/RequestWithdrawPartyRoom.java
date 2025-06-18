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

import com.l2journey.gameserver.model.actor.Player;
import com.l2journey.gameserver.model.groups.matching.PartyMatchRoom;
import com.l2journey.gameserver.model.groups.matching.PartyMatchRoomList;
import com.l2journey.gameserver.network.SystemMessageId;
import com.l2journey.gameserver.network.serverpackets.ExClosePartyRoom;

/**
 * @author Gnacik
 */
public class RequestWithdrawPartyRoom extends ClientPacket
{
	private int _roomid;
	@SuppressWarnings("unused")
	private int _unk1;
	
	@Override
	protected void readImpl()
	{
		_roomid = readInt();
		_unk1 = readInt();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getPlayer();
		if (player == null)
		{
			return;
		}
		
		final PartyMatchRoom room = PartyMatchRoomList.getInstance().getRoom(_roomid);
		if (room == null)
		{
			return;
		}
		
		if ((player.isInParty() && room.getOwner().isInParty()) && (player.getParty().getLeaderObjectId() == room.getOwner().getParty().getLeaderObjectId()))
		{
			// If user is in party with Room Owner
			// is not removed from Room
			
			// _activeChar.setPartyMatching(0);
			player.broadcastUserInfo();
		}
		else
		{
			room.deleteMember(player);
			
			player.setPartyRoom(0);
			// _activeChar.setPartyMatching(0);
			player.sendPacket(new ExClosePartyRoom());
			player.sendPacket(SystemMessageId.YOU_HAVE_EXITED_THE_PARTY_ROOM);
		}
	}
}
