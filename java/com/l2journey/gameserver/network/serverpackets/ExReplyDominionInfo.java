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

import java.util.List;

import com.l2journey.commons.network.WritableBuffer;
import com.l2journey.gameserver.managers.CastleManager;
import com.l2journey.gameserver.managers.TerritoryWarManager;
import com.l2journey.gameserver.managers.TerritoryWarManager.Territory;
import com.l2journey.gameserver.network.GameClient;
import com.l2journey.gameserver.network.ServerPackets;

/**
 * @author JIV
 */
public class ExReplyDominionInfo extends ServerPacket
{
	public static final ExReplyDominionInfo STATIC_PACKET = new ExReplyDominionInfo();
	
	private ExReplyDominionInfo()
	{
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.EX_REPLY_DOMINION_INFO.writeId(this, buffer);
		final List<Territory> territoryList = TerritoryWarManager.getInstance().getAllTerritories();
		buffer.writeInt(territoryList.size()); // Territory Count
		for (Territory t : territoryList)
		{
			buffer.writeInt(t.getTerritoryId()); // Territory Id
			buffer.writeString(CastleManager.getInstance().getCastleById(t.getCastleId()).getName().toLowerCase() + "_dominion"); // territory name
			buffer.writeString(t.getOwnerClan().getName());
			buffer.writeInt(t.getOwnedWardIds().size()); // Emblem Count
			for (int i : t.getOwnedWardIds())
			{
				buffer.writeInt(i); // Emblem ID - should be in for loop for emblem count
			}
			buffer.writeInt((int) (TerritoryWarManager.getInstance().getTWStartTimeInMillis() / 1000));
		}
	}
}
