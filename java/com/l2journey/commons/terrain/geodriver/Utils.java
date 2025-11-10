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
 * 
 * ---
 * 
 * Portions of this software are derived from the L2JMobius Project, 
 * shared under the MIT License. The original license terms are preserved where 
 * applicable..
 * 
 */
package com.l2journey.commons.terrain.geodriver;

import com.l2journey.commons.terrain.geoengine.Direction;

/**
 * @author FBIagent
 */
public final class Utils
{
	private static boolean _nsweContains(byte nswe, byte nsweFlags)
	{
		return (nswe & nsweFlags) == nsweFlags;
	}
	
	private static byte _getNsweFlagsFromDirection(Direction dir)
	{
		switch (dir)
		{
			case NORTH_WEST:
				return Cell.FLAG_NSWE_NORTH_WEST;
			case NORTH_EAST:
				return Cell.FLAG_NSWE_NORTH_EAST;
			case SOUTH_WEST:
				return Cell.FLAG_NSWE_SOUTH_WEST;
			case SOUTH_EAST:
				return Cell.FLAG_NSWE_SOUTH_EAST;
			case NORTH:
				return Cell.FLAG_NSWE_NORTH;
			case EAST:
				return Cell.FLAG_NSWE_EAST;
			case SOUTH:
				return Cell.FLAG_NSWE_SOUTH;
			case WEST:
				return Cell.FLAG_NSWE_WEST;
			default:
				throw new IllegalStateException("This can't happen we have exacly the number of fields in the enum!");
		}
	}
	
	public static boolean canMoveIntoDirections(byte nswe, Direction first, Direction... more)
	{
		if (!_nsweContains(nswe, _getNsweFlagsFromDirection(first)))
		{
			return false;
		}
		
		if (more != null)
		{
			for (Direction dir : more)
			{
				if (!_nsweContains(nswe, _getNsweFlagsFromDirection(dir)))
				{
					return false;
				}
			}
		}
		
		return true;
	}
	
	private Utils()
	{
	}
}
