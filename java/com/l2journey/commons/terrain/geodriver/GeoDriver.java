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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Properties;
import java.util.logging.Logger;

import com.l2journey.commons.terrain.geodriver.regions.NonNullRegion;
import com.l2journey.commons.terrain.geodriver.regions.NullRegion;
import com.l2journey.commons.terrain.geoengine.Direction;
import com.l2journey.commons.terrain.geoengine.MissingPropertyException;
import com.l2journey.commons.terrain.geoengine.abstraction.IGeoDriver;

/**
 * @author FBIagent
 */
public final class GeoDriver implements IGeoDriver
{
	private final Logger _LOGGER = Logger.getLogger(GeoDriver.class.getName());
	
	// world dimensions: 1048576 * 1048576 = 1099511627776
	private static final int WORLD_MIN_X = -655360;
	private static final int WORLD_MAX_X = 393215;
	private static final int WORLD_MIN_Y = -589824;
	private static final int WORLD_MAX_Y = 458751;
	
	/** Regions in the world on the x axis */
	public static final int GEO_REGIONS_X = 32;
	/** Regions in the world on the y axis */
	public static final int GEO_REGIONS_Y = 32;
	/** Region in the world */
	public static final int GEO_REGIONS = GEO_REGIONS_X * GEO_REGIONS_Y;
	
	/** Blocks in the world on the x axis */
	public static final int GEO_BLOCKS_X = GEO_REGIONS_X * IRegion.REGION_BLOCKS_X;
	/** Blocks in the world on the y axis */
	public static final int GEO_BLOCKS_Y = GEO_REGIONS_Y * IRegion.REGION_BLOCKS_Y;
	/** Blocks in the world */
	public static final int GEO_BLOCKS = GEO_REGIONS * IRegion.REGION_BLOCKS;
	
	/** Cells in the world on the x axis */
	public static final int GEO_CELLS_X = GEO_BLOCKS_X * IBlock.BLOCK_CELLS_X;
	/** Cells in the world in the y axis */
	public static final int GEO_CELLS_Y = GEO_BLOCKS_Y * IBlock.BLOCK_CELLS_Y;
	
	/** The regions array */
	private final IRegion[] _regions = new IRegion[GEO_REGIONS];
	
	/**
	 * Initializes a new instance of this geodata driver.<br>
	 * Required properties are "geodataPath".
	 * @param props the driver properties
	 * @throws Exception an error happened
	 */
	public GeoDriver(Properties props) throws Exception
	{
		String filePathFormat = _loadProperty(props, "geodataPath");
		if (!filePathFormat.endsWith("\\") && !filePathFormat.endsWith("/"))
		{
			filePathFormat += File.separator;
		}
		filePathFormat += "%d_%d.l2j";
		
		boolean tryLoadUnspecifiedRegions = Boolean.valueOf(_loadProperty(props, "tryLoadUnspecifiedRegions"));
		
		int loadedRegions = 0;
		for (int geoRegionX = 0; geoRegionX < GEO_REGIONS_X; ++geoRegionX)
		{
			for (int geoRegionY = 0; geoRegionY < GEO_REGIONS_Y; ++geoRegionY)
			{
				int geoRegionOffset = (geoRegionX * GEO_REGIONS_Y) + geoRegionY;
				String filePath = String.format(filePathFormat, geoRegionX, geoRegionY);
				
				try
				{
					boolean regionEnabled = Boolean.valueOf(_loadProperty(props, geoRegionX + "_" + geoRegionY));
					if (regionEnabled)
					{
						_regions[geoRegionOffset] = _loadGeoFile(filePath);
						++loadedRegions;
					}
				}
				catch (MissingPropertyException e)
				{
					if (tryLoadUnspecifiedRegions)
					{
						try
						{
							_regions[geoRegionOffset] = _loadGeoFile(filePath);
							++loadedRegions;
						}
						catch (FileNotFoundException e2)
						{
							_regions[geoRegionOffset] = NullRegion.INSTANCE;
						}
					}
					else
					{
						_regions[geoRegionOffset] = NullRegion.INSTANCE;
					}
				}
			}
		}
		
		_LOGGER.info("Loaded " + loadedRegions + " regions.");
	}
	
	private String _loadProperty(Properties props, String propertyKey)
	{
		String propertyValue = props.getProperty(propertyKey);
		if (propertyValue == null)
		{
			throw new MissingPropertyException(propertyKey);
		}
		return propertyValue;
	}
	
	private NonNullRegion _loadGeoFile(String filePath) throws FileNotFoundException, IOException
	{
		try (RandomAccessFile raf = new RandomAccessFile(filePath, "r");
			FileChannel fc = raf.getChannel())
		{
			MappedByteBuffer mbb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size()).load();
			mbb.order(ByteOrder.LITTLE_ENDIAN);
			NonNullRegion nnr = new NonNullRegion(mbb);
			return nnr;
		}
	}
	
	private void _checkGeoX(int geoX)
	{
		// maximum of GEO_CELLS_X - 1
		if ((geoX < 0) || (geoX >= GEO_CELLS_X))
		{
			throw new IllegalArgumentException();
		}
	}
	
	private void _checkGeoY(int geoY)
	{
		// maximum of GEO_CELLS_Y - 1
		if ((geoY < 0) || (geoY >= GEO_CELLS_Y))
		{
			throw new IllegalArgumentException();
		}
	}
	
	private IRegion _getGeoRegion(int geoX, int geoY)
	{
		_checkGeoX(geoX);
		_checkGeoY(geoY);
		return _regions[((geoX / IRegion.REGION_CELLS_X) * GEO_REGIONS_Y) + (geoY / IRegion.REGION_CELLS_Y)];
	}
	
	@Override
	public int getGeoX(int worldX)
	{
		if ((worldX < WORLD_MIN_X) || (worldX > WORLD_MAX_X))
		{
			throw new IllegalArgumentException();
		}
		return (worldX - WORLD_MIN_X) >> 4;
	}
	
	@Override
	public int getGeoY(int worldY)
	{
		if ((worldY < WORLD_MIN_Y) || (worldY > WORLD_MAX_Y))
		{
			throw new IllegalArgumentException();
		}
		return (worldY - WORLD_MIN_Y) >> 4;
	}
	
	/**
	 * A single geodata position represents 16x16 positions in the game world.<br>
	 * That means we add 8 to the calculated world position, to always return the<br>
	 * middle of the 16x16 world sqaure the geo position represents.
	 */
	@Override
	public int getWorldX(int geoX)
	{
		_checkGeoX(geoX);
		return (geoX << 4) + WORLD_MIN_X + 8;
	}
	
	@Override
	public int getWorldY(int geoY)
	{
		_checkGeoY(geoY);
		return (geoY << 4) + WORLD_MIN_Y + 8;
	}
	
	@Override
	public boolean hasGeoPos(int geoX, int geoY)
	{
		return _getGeoRegion(geoX, geoY).hasGeoPos(geoX, geoY);
	}
	
	@Override
	public int getNearestZ(int geoX, int geoY, int worldZ)
	{
		return _getGeoRegion(geoX, geoY).getNearestZ(geoX, geoY, worldZ);
	}
	
	@Override
	public int getNextLowerZ(int geoX, int geoY, int worldZ)
	{
		return _getGeoRegion(geoX, geoY).getNextLowerZ(geoX, geoY, worldZ);
	}
	
	@Override
	public int getNextHigherZ(int geoX, int geoY, int worldZ)
	{
		return _getGeoRegion(geoX, geoY).getNextHigherZ(geoX, geoY, worldZ);
	}
	
	@Override
	public boolean canEnterNeighbors(int geoX, int geoY, int worldZ, Direction first, Direction... more)
	{
		return _getGeoRegion(geoX, geoY).canMoveIntoDirections(geoX, geoY, worldZ, first, more);
	}
	
	@Override
	public boolean canEnterAllNeighbors(int geoX, int geoY, int worldZ)
	{
		return _getGeoRegion(geoX, geoY).canMoveIntoAllDirections(geoX, geoY, worldZ);
	}
}
