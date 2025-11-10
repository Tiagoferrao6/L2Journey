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

/**
 * @author FBIagent
 */
public final class Cell
{
	/** East NSWE flag */
	public static final byte FLAG_NSWE_EAST = 1 << 0;
	/** West NSWE flag */
	public static final byte FLAG_NSWE_WEST = 1 << 1;
	/** South NSWE flag */
	public static final byte FLAG_NSWE_SOUTH = 1 << 2;
	/** North NSWE flag */
	public static final byte FLAG_NSWE_NORTH = 1 << 3;
	
	/** North-East NSWE flags */
	public static final byte FLAG_NSWE_NORTH_EAST = FLAG_NSWE_NORTH | FLAG_NSWE_EAST;
	/** North-West NSWE flags */
	public static final byte FLAG_NSWE_NORTH_WEST = FLAG_NSWE_NORTH | FLAG_NSWE_WEST;
	/** South-East NSWE flags */
	public static final byte FLAG_NSWE_SOUTH_EAST = FLAG_NSWE_SOUTH | FLAG_NSWE_EAST;
	/** South-West NSWE flags */
	public static final byte FLAG_NSWE_SOUTH_WEST = FLAG_NSWE_SOUTH | FLAG_NSWE_WEST;
	
	/** All directions NSWE flags */
	public static final byte FLAG_NSWE_ALL = FLAG_NSWE_EAST | FLAG_NSWE_WEST | FLAG_NSWE_SOUTH | FLAG_NSWE_NORTH;
	
	private Cell()
	{
	}
}
