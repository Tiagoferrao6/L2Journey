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
package com.l2journey.commons.terrain.geodriver.blocks;

import java.nio.ByteBuffer;

import com.l2journey.commons.terrain.geodriver.Cell;
import com.l2journey.commons.terrain.geodriver.IBlock;
import com.l2journey.commons.terrain.geodriver.Utils;
import com.l2journey.commons.terrain.geoengine.Direction;

/**
 * @author FBIagent
 */
public final class ComplexBlock implements IBlock
{
	private final int _bbPos;
	private final ByteBuffer _bb;
	
	/**
	 * Initializes a new instance of this block reading the specified buffer.
	 * @param bb the buffer
	 */
	public ComplexBlock(ByteBuffer bb)
	{
		_bbPos = bb.position();
		_bb = bb;
		_bb.position(_bbPos + (IBlock.BLOCK_CELLS * 2));
	}
	
	private short _getCellData(int geoX, int geoY)
	{
		return _bb.getShort(_bbPos + ((((geoX % IBlock.BLOCK_CELLS_X) * IBlock.BLOCK_CELLS_Y) + (geoY % IBlock.BLOCK_CELLS_Y)) * 2));
	}
	
	private byte _getCellNSWE(int geoX, int geoY)
	{
		return (byte) (_getCellData(geoX, geoY) & 0x000F);
	}
	
	private int _getCellHeight(int geoX, int geoY)
	{
		short height = (short) (_getCellData(geoX, geoY) & 0x0fff0);
		return height >> 1;
	}
	
	@Override
	public boolean hasGeoPos(int geoX, int geoY)
	{
		return true;
	}
	
	@Override
	public int getNearestZ(int geoX, int geoY, int worldZ)
	{
		return _getCellHeight(geoX, geoY);
	}
	
	@Override
	public int getNextLowerZ(int geoX, int geoY, int worldZ)
	{
		int cellHeight = _getCellHeight(geoX, geoY);
		return cellHeight <= worldZ ? cellHeight : worldZ;
	}
	
	@Override
	public int getNextHigherZ(int geoX, int geoY, int worldZ)
	{
		int cellHeight = _getCellHeight(geoX, geoY);
		return cellHeight >= worldZ ? cellHeight : worldZ;
	}
	
	@Override
	public boolean canMoveIntoDirections(int geoX, int geoY, int worldZ, Direction first, Direction... more)
	{
		return Utils.canMoveIntoDirections(_getCellNSWE(geoX, geoY), first, more);
	}
	
	@Override
	public boolean canMoveIntoAllDirections(int geoX, int geoY, int worldZ)
	{
		return _getCellNSWE(geoX, geoY) == Cell.FLAG_NSWE_ALL;
	}
}
