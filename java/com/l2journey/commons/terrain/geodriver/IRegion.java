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
public interface IRegion
{
	/**
	 * Region types.
	 * @author FBIagent
	 */
	public enum Type
	{
		/** Null region type */
		NULL,
		/** Non null region type */
		NON_NULL
	}
	
	/** Blocks in a region on the x axis */
	public static final int REGION_BLOCKS_X = 256;
	/** Blocks in a region on the y axis */
	public static final int REGION_BLOCKS_Y = 256;
	/** Blocks in a region */
	public static final int REGION_BLOCKS = REGION_BLOCKS_X * REGION_BLOCKS_Y;
	
	/** Cells in a region on the x axis */
	public static final int REGION_CELLS_X = REGION_BLOCKS_X * IBlock.BLOCK_CELLS_X;
	/** Cells in a regioin on the y axis */
	public static final int REGION_CELLS_Y = REGION_BLOCKS_Y * IBlock.BLOCK_CELLS_Y;
	/** Cells in a region */
	public static final int REGION_CELLS = REGION_CELLS_X * REGION_CELLS_Y;
	
	boolean hasGeoPos(int geoX, int geoY);
	
	int getNearestZ(int geoX, int geoY, int worldZ);
	
	int getNextLowerZ(int geoX, int geoY, int worldZ);
	
	int getNextHigherZ(int geoX, int geoY, int worldZ);
	
	boolean canMoveIntoDirections(int geoX, int geoY, int worldZ, Direction first, Direction... more);
	
	boolean canMoveIntoAllDirections(int geoX, int geoY, int worldZ);
}
