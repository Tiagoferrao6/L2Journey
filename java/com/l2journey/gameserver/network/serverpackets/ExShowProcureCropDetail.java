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

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.l2journey.commons.network.WritableBuffer;
import com.l2journey.gameserver.managers.CastleManager;
import com.l2journey.gameserver.managers.CastleManorManager;
import com.l2journey.gameserver.model.CropProcure;
import com.l2journey.gameserver.model.siege.Castle;
import com.l2journey.gameserver.network.GameClient;
import com.l2journey.gameserver.network.ServerPackets;

/**
 * @author l3x
 */
public class ExShowProcureCropDetail extends ServerPacket
{
	private final int _cropId;
	private final Map<Integer, CropProcure> _castleCrops = new HashMap<>();
	
	public ExShowProcureCropDetail(int cropId)
	{
		_cropId = cropId;
		for (Castle c : CastleManager.getInstance().getCastles())
		{
			final CropProcure cropItem = CastleManorManager.getInstance().getCropProcure(c.getResidenceId(), cropId, false);
			if ((cropItem != null) && (cropItem.getAmount() > 0))
			{
				_castleCrops.put(c.getResidenceId(), cropItem);
			}
		}
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.EX_SHOW_PROCURE_CROP_DETAIL.writeId(this, buffer);
		buffer.writeInt(_cropId); // crop id
		buffer.writeInt(_castleCrops.size()); // size
		for (Entry<Integer, CropProcure> entry : _castleCrops.entrySet())
		{
			final CropProcure crop = entry.getValue();
			buffer.writeInt(entry.getKey()); // manor name
			buffer.writeLong(crop.getAmount()); // buy residual
			buffer.writeLong(crop.getPrice()); // buy price
			buffer.writeByte(crop.getReward()); // reward type
		}
	}
}
