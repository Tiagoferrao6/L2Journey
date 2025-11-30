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
package com.l2journey.gameserver.pathfinding;

import java.util.List;

import com.l2journey.gameserver.model.World;
import com.l2journey.gameserver.pathfinding.cellnodes.CellPathFinding;

/**
 * @author -Nemesiss-
 */
public abstract class PathFinding
{
	public static PathFinding getInstance()
	{
		// if (Config.PATHFINDING == 1)
		// {
		// Higher Memory Usage, Smaller Cpu Usage
		// return GeoPathFinding.getInstance();
		// }
		// Cell pathfinding, calculated directly from geodata files
		return CellPathFinding.getInstance();
	}
	
	public abstract boolean pathNodesExist(short regionoffset);
	
	public abstract List<AbstractNodeLoc> findPath(int x, int y, int z, int tx, int ty, int tz, int instanceId, boolean playable);
	
	/**
	 * Convert geodata position to pathnode position
	 * @param geo_pos
	 * @return pathnode position
	 */
	public short getNodePos(int geo_pos)
	{
		return (short) (geo_pos >> 3); // OK?
	}
	
	/**
	 * Convert node position to pathnode block position
	 * @param node_pos
	 * @return pathnode block position (0...255)
	 */
	public short getNodeBlock(int node_pos)
	{
		return (short) (node_pos % 256);
	}
	
	public byte getRegionX(int node_pos)
	{
		return (byte) ((node_pos >> 8) + World.TILE_X_MIN);
	}
	
	public byte getRegionY(int node_pos)
	{
		return (byte) ((node_pos >> 8) + World.TILE_Y_MIN);
	}
	
	public short getRegionOffset(byte rx, byte ry)
	{
		return (short) ((rx << 5) + ry);
	}
	
	/**
	 * Convert pathnode x to World x position
	 * @param node_x rx
	 * @return
	 */
	public int calculateWorldX(short node_x)
	{
		return World.MAP_MIN_X + (node_x * 128) + 48;
	}
	
	/**
	 * Convert pathnode y to World y position
	 * @param node_y
	 * @return
	 */
	public int calculateWorldY(short node_y)
	{
		return World.MAP_MIN_Y + (node_y * 128) + 48;
	}
	
	public String[] getStat()
	{
		return null;
	}
}
