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
package com.l2journey.gameserver.network.serverpackets;

import java.util.Collection;

import com.l2journey.commons.network.WritableBuffer;
import com.l2journey.gameserver.data.sql.ClanTable;
import com.l2journey.gameserver.managers.CastleManager;
import com.l2journey.gameserver.model.siege.Castle;
import com.l2journey.gameserver.network.GameClient;
import com.l2journey.gameserver.network.PacketLogger;
import com.l2journey.gameserver.network.ServerPackets;

/**
 * @author KenM
 */
public class ExShowCastleInfo extends ServerPacket
{
	private final Collection<Castle> _castles;
	
	public ExShowCastleInfo()
	{
		_castles = CastleManager.getInstance().getCastles();
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.EX_SHOW_CASTLE_INFO.writeId(this, buffer);
		buffer.writeInt(_castles.size());
		for (Castle castle : _castles)
		{
			buffer.writeInt(castle.getResidenceId());
			if (castle.getOwnerId() > 0)
			{
				if (ClanTable.getInstance().getClan(castle.getOwnerId()) != null)
				{
					buffer.writeString(ClanTable.getInstance().getClan(castle.getOwnerId()).getName());
				}
				else
				{
					PacketLogger.warning("Castle owner with no name! Castle: " + castle.getName() + " has an OwnerId = " + castle.getOwnerId() + " who does not have a  name!");
					buffer.writeString("");
				}
			}
			else
			{
				buffer.writeString("");
			}
			buffer.writeInt(castle.getTaxPercent());
			buffer.writeInt((int) (castle.getSiege().getSiegeDate().getTimeInMillis() / 1000));
		}
	}
}
