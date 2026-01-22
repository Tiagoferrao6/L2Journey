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
package com.l2journey.gameserver.data.holders;

import java.util.Collections;
import java.util.Map;

/**
 * Holder for Daily Reward configuration data.
 * Stores reward information for a specific day in the daily reward cycle.
 * @author L2Journey
 */
public class DailyRewardHolder
{
	private final int _day;
	private final Map<Integer, Long> _rewards;
	private String _icon;
	
	/**
	 * Creates a new DailyRewardHolder.
	 * @param day the day number (1-30)
	 * @param rewards map of itemId to count
	 */
	public DailyRewardHolder(int day, Map<Integer, Long> rewards)
	{
		_day = day;
		_rewards = rewards;
		_icon = "";
	}
	
	/**
	 * Gets the day number for this reward.
	 * @return the day number
	 */
	public int getDay()
	{
		return _day;
	}
	
	/**
	 * Gets the rewards map.
	 * @return unmodifiable map of itemId to count
	 */
	public Map<Integer, Long> getRewards()
	{
		return Collections.unmodifiableMap(_rewards);
	}
	
	/**
	 * Sets the display icon for this reward day.
	 * @param icon the icon path
	 */
	public void setIcon(String icon)
	{
		_icon = icon;
	}
	
	/**
	 * Gets the display icon for this reward day.
	 * @return the icon path
	 */
	public String getIcon()
	{
		return _icon;
	}
}
