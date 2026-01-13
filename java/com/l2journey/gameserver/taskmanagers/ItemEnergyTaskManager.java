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
package com.l2journey.gameserver.taskmanagers;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import com.l2journey.commons.threads.ThreadPool;
import com.l2journey.gameserver.model.actor.Player;
import com.l2journey.gameserver.model.item.instance.Item;

/**
 * Task manager for agathion energy consumption.
 * Handles the periodic energy decrease for equipped agathion bracelet items
 * when an agathion is actively summoned.
 * @author KingHanker
 */
public class ItemEnergyTaskManager implements Runnable
{
	private static final Map<Item, Long> ITEMS = new ConcurrentHashMap<>();
	private static final int ENERGY_CONSUMPTION_RATE = 60000; // 1 minute
	private static boolean _working = false;
	
	protected ItemEnergyTaskManager()
	{
		ThreadPool.scheduleAtFixedRate(this, 1000, 1000);
	}
	
	@Override
	public void run()
	{
		if (_working)
		{
			return;
		}
		_working = true;
		
		if (!ITEMS.isEmpty())
		{
			final long currentTime = System.currentTimeMillis();
			final Iterator<Entry<Item, Long>> iterator = ITEMS.entrySet().iterator();
			Entry<Item, Long> entry;
			
			while (iterator.hasNext())
			{
				entry = iterator.next();
				if (currentTime > entry.getValue())
				{
					iterator.remove();
					
					final Item item = entry.getKey();
					final Player player = item.asPlayer();
					if ((player == null) || player.isInOfflineMode())
					{
						continue;
					}
					
					// Only consume energy if agathion is still active
					if (player.getAgathionId() > 0)
					{
						item.decreaseAgathionEnergy(item.isEquipped());
					}
					else
					{
						// Agathion was dismissed, stop consuming energy
						item.stopConsumeEnergyTask();
					}
				}
			}
		}
		
		_working = false;
	}
	
	/**
	 * Adds an item to the energy consumption queue.
	 * @param item the agathion bracelet item
	 */
	public void add(Item item)
	{
		if (!ITEMS.containsKey(item))
		{
			ITEMS.put(item, System.currentTimeMillis() + ENERGY_CONSUMPTION_RATE);
		}
	}
	
	/**
	 * Removes an item from the energy consumption queue.
	 * @param item the agathion bracelet item
	 */
	public void remove(Item item)
	{
		ITEMS.remove(item);
	}
	
	public static ItemEnergyTaskManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final ItemEnergyTaskManager INSTANCE = new ItemEnergyTaskManager();
	}
}
