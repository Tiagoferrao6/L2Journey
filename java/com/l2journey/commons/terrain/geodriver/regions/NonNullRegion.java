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
package com.l2journey.commons.terrain.geodriver.regions;

import java.nio.ByteBuffer;

import com.l2journey.commons.terrain.geodriver.IBlock;
import com.l2journey.commons.terrain.geodriver.IRegion;
import com.l2journey.commons.terrain.geodriver.blocks.ComplexBlock;
import com.l2journey.commons.terrain.geodriver.blocks.FlatBlock;
import com.l2journey.commons.terrain.geodriver.blocks.MultilayerBlock;
import com.l2journey.commons.terrain.geoengine.Direction;

/**
 * @author FBIagent
 */
public final class NonNullRegion implements IRegion
{
	private final IBlock[] _blocks = new IBlock[IRegion.REGION_BLOCKS];
	
	/**
	 * Initializes a new instance of this region reading from the specified<br>
	 * buffer.
	 * @param bb the buffer
	 */
	public NonNullRegion(ByteBuffer bb)
	{
		int blockType;
		for (int regionBlockOffset = 0; regionBlockOffset < IRegion.REGION_BLOCKS; ++regionBlockOffset)
		{
			blockType = bb.get();
			if (blockType == IBlock.Type.FLAT.ordinal())
			{
				_blocks[regionBlockOffset] = new FlatBlock(bb);
			}
			else if (blockType == IBlock.Type.COMPLEX.ordinal())
			{
				_blocks[regionBlockOffset] = new ComplexBlock(bb);
			}
			else if (blockType == IBlock.Type.MULTILAYER.ordinal())
			{
				_blocks[regionBlockOffset] = new MultilayerBlock(bb);
			}
			else
			{
				throw new RuntimeException("L2JGeoDriver: Invalid block type!");
			}
		}
	}
	
	private IBlock _getBlock(int geoX, int geoY)
	{
		return _blocks[(((geoX / IBlock.BLOCK_CELLS_X) % IRegion.REGION_BLOCKS_X) * IRegion.REGION_BLOCKS_Y) + ((geoY / IBlock.BLOCK_CELLS_Y) % IRegion.REGION_BLOCKS_Y)];
	}
	
	@Override
	public boolean hasGeoPos(int geoX, int geoY)
	{
		return _getBlock(geoX, geoY).hasGeoPos(geoX, geoY);
	}
	
	@Override
	public int getNearestZ(int geoX, int geoY, int worldZ)
	{
		return _getBlock(geoX, geoY).getNearestZ(geoX, geoY, worldZ);
	}
	
	@Override
	public int getNextLowerZ(int geoX, int geoY, int worldZ)
	{
		return _getBlock(geoX, geoY).getNextLowerZ(geoX, geoY, worldZ);
	}
	
	@Override
	public int getNextHigherZ(int geoX, int geoY, int worldZ)
	{
		return _getBlock(geoX, geoY).getNextHigherZ(geoX, geoY, worldZ);
	}
	
	@Override
	public boolean canMoveIntoDirections(int geoX, int geoY, int worldZ, Direction first, Direction... more)
	{
		return _getBlock(geoX, geoY).canMoveIntoDirections(geoX, geoY, worldZ, first, more);
	}
	
	@Override
	public boolean canMoveIntoAllDirections(int geoX, int geoY, int worldZ)
	{
		return _getBlock(geoX, geoY).canMoveIntoAllDirections(geoX, geoY, worldZ);
	}
}
