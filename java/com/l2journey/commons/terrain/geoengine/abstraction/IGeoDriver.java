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
package com.l2journey.commons.terrain.geoengine.abstraction;

import com.l2journey.commons.terrain.geoengine.Direction;

/**
 * @author FBIagent
 */
public interface IGeoDriver
{
	/**
	 * Translates world x into geo x.
	 * @param worldX world x
	 * @return geo x
	 */
	int getGeoX(int worldX);
	
	/**
	 * Translates world y into geo y.
	 * @param worldY world y
	 * @return geo y
	 */
	int getGeoY(int worldY);
	
	/**
	 * Translates geo x into world x.
	 * @param geoX geo x
	 * @return world x
	 */
	int getWorldX(int geoX);
	
	/**
	 * Translates geo y into world y.
	 * @param geoY geo y
	 * @return world y
	 */
	int getWorldY(int geoY);
	
	/**
	 * Checks the specified geodata position for available geodata.
	 * @param geoX geo x
	 * @param geoY geo y
	 * @return true when geodata is available, false otherwise
	 */
	boolean hasGeoPos(int geoX, int geoY);
	
	/**
	 * Method to get the nearest z value. If there is no geodata available<br>
	 * at the specified position, {@code worldZ} is returned.
	 * @param geoX geo x
	 * @param geoY geo y
	 * @param worldZ world z
	 * @return nearest z or worldZ(see description above)
	 */
	int getNearestZ(int geoX, int geoY, int worldZ);
	
	/**
	 * Method to get the next lower z value. If there is a layer with a z<br>
	 * equals to {@code worldZ} or there is no lower z, {@code worldZ} is<br>
	 * returned.
	 * @param geoX geo x
	 * @param geoY geo y
	 * @param worldZ world z
	 * @return next lower z or worldZ(see description above)
	 */
	int getNextLowerZ(int geoX, int geoY, int worldZ);
	
	/**
	 * Method to get the next higher z value. If there is a layer with a z<br>
	 * equals to {@code worldZ} or there is no higher z, {@code worldZ} is<br>
	 * returned.
	 * @param geoX geo x
	 * @param geoY geo y
	 * @param worldZ world z
	 * @return next higher z or worldZ(see description above)
	 */
	int getNextHigherZ(int geoX, int geoY, int worldZ);
	
	/**
	 * Checks if the neighbor directions specified can be entered from the<br>
	 * specified geodata position.
	 * @param geoX geo x
	 * @param geoY geo y
	 * @param worldZ world z
	 * @param first first direction
	 * @param more more directions
	 * @return true when the specified neighbor directions can be entered
	 */
	boolean canEnterNeighbors(int geoX, int geoY, int worldZ, Direction first, Direction... more);
	
	/**
	 * Checks if all neighbor directions can be entered from the specified<br>
	 * geodata position.
	 * @param geoX geo x
	 * @param geoY geo y
	 * @param worldZ world z
	 * @return true when all neighbor directions can be entered
	 */
	boolean canEnterAllNeighbors(int geoX, int geoY, int worldZ);
}
