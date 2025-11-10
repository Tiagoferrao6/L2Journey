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
package com.l2journey.commons.terrain.geoengine;

import java.util.Properties;
import java.util.logging.Logger;

import com.l2journey.commons.terrain.geoengine.abstraction.IGeoDriver;

/**
 * @author FBIagent
 */
public final class NullDriver implements IGeoDriver
{
	private static final Logger _LOGGER = Logger.getLogger(NullDriver.class.getName());
	
	/**
	 * @param props properties for the driver
	 */
	public NullDriver(Properties props)
	{
		// _LOGGER.info("Using Null GeoDriver.");
		_LOGGER.info("Geodata disabled");
	}
	
	@Override
	public int getGeoX(int worldX)
	{
		return worldX;
	}
	
	@Override
	public int getGeoY(int worldY)
	{
		return worldY;
	}
	
	@Override
	public int getWorldX(int geoX)
	{
		return geoX;
	}
	
	@Override
	public int getWorldY(int geoY)
	{
		return geoY;
	}
	
	@Override
	public boolean hasGeoPos(int geoX, int geoY)
	{
		return false;
	}
	
	@Override
	public int getNearestZ(int geoX, int geoY, int worldZ)
	{
		return worldZ;
	}
	
	@Override
	public int getNextLowerZ(int geoX, int geoY, int worldZ)
	{
		return worldZ;
	}
	
	@Override
	public int getNextHigherZ(int geoX, int geoY, int worldZ)
	{
		return worldZ;
	}
	
	@Override
	public boolean canEnterNeighbors(int geoX, int geoY, int worldZ, Direction first, Direction... more)
	{
		return true;
	}
	
	@Override
	public boolean canEnterAllNeighbors(int geoX, int geoY, int worldZ)
	{
		return true;
	}
}
