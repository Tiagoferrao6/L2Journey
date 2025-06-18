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
import com.l2journey.gameserver.managers.FortManager;
import com.l2journey.gameserver.model.clan.Clan;
import com.l2journey.gameserver.model.siege.Fort;
import com.l2journey.gameserver.network.GameClient;
import com.l2journey.gameserver.network.ServerPackets;

/**
 * @author KenM
 */
public class ExShowFortressInfo extends ServerPacket
{
	public static final ExShowFortressInfo STATIC_PACKET = new ExShowFortressInfo();
	
	private ExShowFortressInfo()
	{
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.EX_SHOW_FORTRESS_INFO.writeId(this, buffer);
		final List<Fort> forts = FortManager.getInstance().getForts();
		buffer.writeInt(forts.size());
		for (Fort fort : forts)
		{
			final Clan clan = fort.getOwnerClan();
			buffer.writeInt(fort.getResidenceId());
			buffer.writeString(clan != null ? clan.getName() : "");
			buffer.writeInt(fort.getSiege().isInProgress());
			// Time of possession
			buffer.writeInt(fort.getOwnedTime());
		}
	}
}
