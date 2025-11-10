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
public interface IBlock
{
	/**
	 * Region type.
	 * @author FBIagent
	 */
	public enum Type
	{
		/** Flat block type */
		FLAT,
		/** Complex block type */
		COMPLEX,
		/** Multilayer block type */
		MULTILAYER
	}
	
	/** Cells in a block on the x axis */
	public static final int BLOCK_CELLS_X = 8;
	/** Cells in a block on the y axis */
	public static final int BLOCK_CELLS_Y = 8;
	/** Cells in a block */
	public static final int BLOCK_CELLS = BLOCK_CELLS_X * BLOCK_CELLS_Y;
	
	boolean hasGeoPos(int geoX, int geoY);
	
	int getNearestZ(int geoX, int geoY, int worldZ);
	
	int getNextLowerZ(int geoX, int geoY, int worldZ);
	
	int getNextHigherZ(int geoX, int geoY, int worldZ);
	
	boolean canMoveIntoDirections(int geoX, int geoY, int worldZ, Direction first, Direction... more);
	
	boolean canMoveIntoAllDirections(int geoX, int geoY, int worldZ);
}
