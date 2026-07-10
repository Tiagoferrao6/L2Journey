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
 */
package com.l2journey.gameserver.network.serverpackets;

import java.util.List;

import com.l2journey.commons.network.WritableBuffer;
import com.l2journey.gameserver.model.item.instance.Item;
import com.l2journey.gameserver.network.GameClient;
import com.l2journey.gameserver.network.ServerPackets;

/**
 * Packet to send agathion energy information to the client.
 * @author KingHanker
 */
public class ExBrAgathionEnergyInfo extends ServerPacket
{
	private final List<Item> _items;
	
	/**
	 * Creates the packet with a list of items.
	 * @param items the list of agathion items
	 */
	public ExBrAgathionEnergyInfo(List<Item> items)
	{
		_items = items;
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.EX_BR_AGATHION_ENERGY_INFO.writeId(this, buffer);
		
		// Count only items that are actual agathion items
		int count = 0;
		for (Item item : _items)
		{
			if ((item != null) && item.isAgathionItem())
			{
				count++;
			}
		}
		
		buffer.writeInt(count);
		
		for (Item item : _items)
		{
			if ((item == null) || !item.isAgathionItem())
			{
				continue;
			}
			
			final int objectId = item.getObjectId();
			final int displayId = item.getDisplayId();
			final int energy = item.getAgathionEnergy();
			final int maxEnergy = item.getTemplate().getAgathionMaxEnergy();
			
			buffer.writeInt(objectId);
			buffer.writeInt(displayId);
			buffer.writeInt(0x200000); // left bracelet slot
			buffer.writeInt(energy);
			buffer.writeInt(maxEnergy);
		}
	}
}
