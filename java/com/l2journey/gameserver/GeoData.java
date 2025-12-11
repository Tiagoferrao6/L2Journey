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
package com.l2journey.gameserver;

import java.io.FileInputStream;
import java.lang.reflect.Constructor;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.logging.Logger;

import com.l2journey.Config;
import com.l2journey.commons.terrain.geoengine.Direction;
import com.l2journey.commons.terrain.geoengine.NullDriver;
import com.l2journey.commons.terrain.geoengine.abstraction.IGeoDriver;
import com.l2journey.gameserver.data.xml.DoorData;
import com.l2journey.gameserver.model.Location;
import com.l2journey.gameserver.model.WorldObject;
import com.l2journey.gameserver.model.interfaces.ILocational;
import com.l2journey.gameserver.pathfinding.AbstractNodeLoc;
import com.l2journey.gameserver.util.GeoUtils;
import com.l2journey.gameserver.util.LinePointIterator;
import com.l2journey.gameserver.util.Util;

/**
 * @author -Nemesiss-, FBIagent
 */
public class GeoData implements IGeoDriver
{
	private static final Logger LOGGER = Logger.getLogger(GeoData.class.getName());
	private static final int ELEVATED_SEE_OVER_DISTANCE = 2;
	private static final int MAX_SEE_OVER_HEIGHT = 48;
	private static final int SPAWN_Z_DELTA_LIMIT = 100;
	private static final int MAX_STEP_HEIGHT = 48;
	private static final int MULTI_FLOOR_LIMIT = 60;
	
	private final IGeoDriver _driver;
	
	protected GeoData()
	{
		if (Config.PATHFINDING > 0)
		{
			IGeoDriver driver = null;
			try
			{
				Class<?> cls = Class.forName("com.l2journey.commons.terrain.geodriver.GeoDriver");
				if (!IGeoDriver.class.isAssignableFrom(cls))
				{
					throw new ClassCastException("Geodata driver class needs to implement IGeoDriver!");
				}
				Constructor<?> ctor = cls.getConstructor(Properties.class);
				Properties props = new Properties();
				try (FileInputStream fis = new FileInputStream(Paths.get("config/admin/", "geodata.ini").toString()))
				{
					props.load(fis);
				}
				driver = (IGeoDriver) ctor.newInstance(props);
			}
			catch (Exception ex)
			{
				LOGGER.log(java.util.logging.Level.SEVERE, "Failed to load geodata driver!", ex);
				System.exit(1);
			}
			// we do it this way so it's predictable for the compiler
			_driver = driver;
		}
		else
		{
			_driver = new NullDriver(null);
		}
	}
	
	@Override
	public int getGeoX(int worldX)
	{
		return _driver.getGeoX(worldX);
	}
	
	@Override
	public int getGeoY(int worldY)
	{
		return _driver.getGeoY(worldY);
	}
	
	@Override
	public int getWorldX(int geoX)
	{
		return _driver.getWorldX(geoX);
	}
	
	@Override
	public int getWorldY(int geoY)
	{
		return _driver.getWorldY(geoY);
	}
	
	@Override
	public boolean hasGeoPos(int geoX, int geoY)
	{
		return _driver.hasGeoPos(geoX, geoY);
	}
	
	@Override
	public int getNearestZ(int geoX, int geoY, int worldZ)
	{
		return _driver.getNearestZ(geoX, geoY, worldZ);
	}
	
	@Override
	public int getNextLowerZ(int geoX, int geoY, int worldZ)
	{
		return _driver.getNextLowerZ(geoX, geoY, worldZ);
	}
	
	@Override
	public int getNextHigherZ(int geoX, int geoY, int worldZ)
	{
		return _driver.getNextHigherZ(geoX, geoY, worldZ);
	}
	
	@Override
	public boolean canEnterNeighbors(int geoX, int geoY, int worldZ, Direction first, Direction... more)
	{
		return _driver.canEnterNeighbors(geoX, geoY, worldZ, first, more);
	}
	
	@Override
	public boolean canEnterAllNeighbors(int geoX, int geoY, int worldZ)
	{
		return _driver.canEnterAllNeighbors(geoX, geoY, worldZ);
	}
	
	// ///////////////////
	// L2J METHODS
	public boolean isNullDriver()
	{
		return _driver instanceof NullDriver;
	}
	
	/**
	 * Gets the height.
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param z the z coordinate
	 * @return the height
	 */
	public int getHeight(int x, int y, int z)
	{
		return getNearestZ(getGeoX(x), getGeoY(y), z);
	}
	
	/**
	 * Gets the spawn height.
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param z the the z coordinate
	 * @return the spawn height
	 */
	public int getSpawnHeight(int x, int y, int z)
	{
		final int geoX = getGeoX(x);
		final int geoY = getGeoY(y);
		final int height = getNearestZ(geoX, geoY, z);
		
		// Limite reduzido para áreas com múltiplos andares
		if ((Math.abs(height - z) > SPAWN_Z_DELTA_LIMIT) || isMultiFloorArea(geoX, geoY))
		{
			return getBestFloorWithLowerPreference(geoX, geoY, z, MULTI_FLOOR_LIMIT);
		}
		
		return height;
	}
	
	private int getBestFloorWithLowerPreference(int geoX, int geoY, int worldZ, int limit)
	{
		int lowerZ = getNextLowerZ(geoX, geoY, worldZ);
		int higherZ = getNextHigherZ(geoX, geoY, worldZ);
		int nearestZ = getNearestZ(geoX, geoY, worldZ);
		
		// PRIORIDADE 1: Andar inferior dentro do limite
		if ((lowerZ != Integer.MIN_VALUE) && (Math.abs(lowerZ - worldZ) <= limit))
		{
			return lowerZ;
		}
		
		// PRIORIDADE 2: Altura mais próxima dentro do limite
		if (Math.abs(nearestZ - worldZ) <= limit)
		{
			return nearestZ;
		}
		
		// PRIORIDADE 3: Andar superior dentro do limite (apenas se necessário)
		if ((higherZ != Integer.MIN_VALUE) && (Math.abs(higherZ - worldZ) <= limit))
		{
			return higherZ;
		}
		
		// Caso nenhum esteja dentro do limite, manter altura original
		return worldZ;
	}
	
	private boolean isMultiFloorArea(int geoX, int geoY)
	{
		// Verificar se há múltiplos níveis de altura nesta célula
		int baseZ = getNearestZ(geoX, geoY, 0);
		int higherZ = getNextHigherZ(geoX, geoY, baseZ);
		
		// Se há um nível significativamente mais alto, é uma área multi-andar
		return (higherZ != Integer.MIN_VALUE) && ((higherZ - baseZ) > 80);
	}
	
	/**
	 * Gets the spawn height.
	 * @param location the location
	 * @return the spawn height
	 */
	public int getSpawnHeight(Location location)
	{
		return getSpawnHeight(location.getX(), location.getY(), location.getZ());
	}
	
	/**
	 * Can see target. Doors as target always return true. Checks doors between.
	 * @param cha the character
	 * @param target the target
	 * @return {@code true} if the character can see the target (LOS), {@code false} otherwise
	 */
	public boolean canSeeTarget(WorldObject cha, WorldObject target)
	{
		if (target.isDoor())
		{
			// can always see doors :o
			return true;
		}
		
		return canSeeTarget(cha.getX(), cha.getY(), cha.getZ(), cha.getInstanceId(), target.getX(), target.getY(), target.getZ(), target.getInstanceId());
	}
	
	/**
	 * Can see target. Checks doors between.
	 * @param cha the character
	 * @param worldPosition the world position
	 * @return {@code true} if the character can see the target at the given world position, {@code false} otherwise
	 */
	public boolean canSeeTarget(WorldObject cha, ILocational worldPosition)
	{
		return canSeeTarget(cha.getX(), cha.getY(), cha.getZ(), cha.getInstanceId(), worldPosition.getX(), worldPosition.getY(), worldPosition.getZ());
	}
	
	/**
	 * Can see target. Checks doors between.
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param z the z coordinate
	 * @param instanceId
	 * @param tx the target's x coordinate
	 * @param ty the target's y coordinate
	 * @param tz the target's z coordinate
	 * @param tInstanceId the target's instanceId
	 * @return
	 */
	public boolean canSeeTarget(int x, int y, int z, int instanceId, int tx, int ty, int tz, int tInstanceId)
	{
		if ((instanceId != tInstanceId))
		{
			return false;
		}
		return canSeeTarget(x, y, z, instanceId, tx, ty, tz);
	}
	
	/**
	 * Can see target. Checks doors between.
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param z the z coordinate
	 * @param instanceId
	 * @param tx the target's x coordinate
	 * @param ty the target's y coordinate
	 * @param tz the target's z coordinate
	 * @return {@code true} if there is line of sight between the given coordinate sets, {@code false} otherwise
	 */
	public boolean canSeeTarget(int x, int y, int z, int instanceId, int tx, int ty, int tz)
	{
		if (DoorData.getInstance().checkIfDoorsBetween(x, y, z, tx, ty, tz, instanceId, true))
		{
			return false;
		}
		return canSeeTarget(x, y, z, tx, ty, tz);
	}
	
	@SuppressWarnings("unused")
	private int getLosGeoZ(int prevX, int prevY, int prevGeoZ, int curX, int curY, Direction dir)
	{
		boolean can = true;
		
		switch (dir)
		{
			case NORTH_EAST:
				can = canEnterNeighbors(prevX, prevY - 1, prevGeoZ, Direction.EAST) && canEnterNeighbors(prevX + 1, prevY, prevGeoZ, Direction.NORTH);
				break;
			case NORTH_WEST:
				can = canEnterNeighbors(prevX, prevY - 1, prevGeoZ, Direction.WEST) && canEnterNeighbors(prevX - 1, prevY, prevGeoZ, Direction.NORTH);
				break;
			case SOUTH_EAST:
				can = canEnterNeighbors(prevX, prevY + 1, prevGeoZ, Direction.EAST) && canEnterNeighbors(prevX + 1, prevY, prevGeoZ, Direction.SOUTH);
				break;
			case SOUTH_WEST:
				can = canEnterNeighbors(prevX, prevY + 1, prevGeoZ, Direction.WEST) && canEnterNeighbors(prevX - 1, prevY, prevGeoZ, Direction.SOUTH);
				break;
			
		}
		if (can && canEnterNeighbors(prevX, prevY, prevGeoZ, dir))
		{
			return getNearestZ(curX, curY, prevGeoZ);
		}
		
		return getNextHigherZ(curX, curY, prevGeoZ);
	}
	
	/**
	 * Can see target. Does not check doors between.
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param z the z coordinate
	 * @param tx the target's x coordinate
	 * @param ty the target's y coordinate
	 * @param tz the target's z coordinate
	 * @return {@code true} if there is line of sight between the given coordinate sets, {@code false} otherwise
	 */
	public boolean canSeeTarget(int x, int y, int z, int tx, int ty, int tz)
	{
		int geoX = getGeoX(x);
		int geoY = getGeoY(y);
		int tGeoX = getGeoX(tx);
		int tGeoY = getGeoY(ty);
		
		z = getNearestZ(geoX, geoY, z);
		tz = getNearestZ(tGeoX, tGeoY, tz);
		
		if ((geoX == tGeoX) && (geoY == tGeoY))
		{
			if (hasGeoPos(tGeoX, tGeoY))
			{
				return z == tz;
			}
			
			return true;
		}
		
		double fullDist = Util.calculateDistance(geoX, geoY, 0, tGeoX, tGeoY, 0, false, false);
		
		if (tz > z)
		{
			int tmp = tx;
			tx = x;
			x = tmp;
			
			tmp = ty;
			ty = y;
			y = tmp;
			
			tmp = tz;
			tz = z;
			z = tmp;
			
			tmp = tGeoX;
			tGeoX = geoX;
			geoX = tmp;
			
			tmp = tGeoY;
			tGeoY = geoY;
			geoY = tmp;
		}
		
		int fullZDiff = tz - z;
		
		LinePointIterator pointIter = new LinePointIterator(geoX, geoY, tGeoX, tGeoY);
		// first point is guaranteed to be available, skip it, we can always see our own position
		pointIter.next();
		int prevX = pointIter.x();
		int prevY = pointIter.y();
		int prevMoveNearestZ = z;
		int ptIndex = 0;
		
		while (pointIter.next())
		{
			int curX = pointIter.x();
			int curY = pointIter.y();
			
			// the current position has geodata
			if (hasGeoPos(curX, curY))
			{
				double percentageDist = Util.calculateDistance(geoX, geoY, 0, curX, curY, 0, false, false) / fullDist;
				int beeCurZ = (int) (z + (fullZDiff * percentageDist));
				int beeCurNearestZ = getNearestZ(curX, curY, beeCurZ);
				int moveCurNearestZ;
				if (canEnterNeighbors(prevX, prevY, prevMoveNearestZ, GeoUtils.computeDirection(prevX, prevY, curX, curY)))
				{
					moveCurNearestZ = getNearestZ(curX, curY, prevMoveNearestZ);
				}
				else
				{
					moveCurNearestZ = beeCurNearestZ;
				}
				
				int maxHeight;
				// Check if the current Z is within the allowed height range from the start Z.
				// This is the core of the height-difference check to prevent attacks across floors.
				if (Math.abs(beeCurNearestZ - z) > 700)
				{
					return false;
				}
				if ((ptIndex < ELEVATED_SEE_OVER_DISTANCE) && (fullDist >= ELEVATED_SEE_OVER_DISTANCE))
				{
					maxHeight = z + MAX_SEE_OVER_HEIGHT;
					++ptIndex;
				}
				else
				{
					maxHeight = beeCurZ + MAX_SEE_OVER_HEIGHT;
				}
				
				boolean canSeeThrough = false;
				if ((beeCurNearestZ <= maxHeight) && (moveCurNearestZ <= beeCurNearestZ))
				{
					Direction dir = GeoUtils.computeDirection(prevX, prevY, curX, curY);
					
					// check diagonal step
					switch (dir)
					{
						case NORTH_EAST:
							canSeeThrough = (getNearestZ(prevX, prevY - 1, beeCurZ) <= maxHeight) || (getNearestZ(prevX + 1, prevY, beeCurZ) <= maxHeight);
							break;
						case NORTH_WEST:
							canSeeThrough = (getNearestZ(prevX, prevY - 1, beeCurZ) <= maxHeight) || (getNearestZ(prevX - 1, prevY, beeCurZ) <= maxHeight);
							break;
						case SOUTH_EAST:
							canSeeThrough = (getNearestZ(prevX, prevY + 1, beeCurZ) <= maxHeight) || (getNearestZ(prevX + 1, prevY, beeCurZ) <= maxHeight);
							break;
						case SOUTH_WEST:
							canSeeThrough = (getNearestZ(prevX, prevY + 1, beeCurZ) <= maxHeight) || (getNearestZ(prevX - 1, prevY, beeCurZ) <= maxHeight);
							break;
						default:
							canSeeThrough = true;
							break;
					}
				}
				
				if (!canSeeThrough)
				{
					return false;
				}
				
				prevMoveNearestZ = moveCurNearestZ;
			}
			
			prevX = curX;
			prevY = curY;
		}
		
		return true;
	}
	
	/**
	 * Verifies if the is a path between origin's location and destination, if not returns the closest location.
	 * @param origin the origin
	 * @param destination the destination
	 * @return the destination if there is a path or the closes location
	 */
	public Location moveCheck(ILocational origin, ILocational destination)
	{
		return moveCheck(origin.getX(), origin.getY(), origin.getZ(), destination.getX(), destination.getY(), destination.getZ(), origin.getInstanceId());
	}
	
	/**
	 * Move check.
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param z the z coordinate
	 * @param tx the target's x coordinate
	 * @param ty the target's y coordinate
	 * @param tz the target's z coordinate
	 * @param instanceId the instance id
	 * @return the last Location (x,y,z) where player can walk - just before wall
	 */
	public Location moveCheck(int x, int y, int z, int tx, int ty, int tz, int instanceId)
	{
		// Usar a validação rigorosa primeiro
		if (validateMovementPath(x, y, z, tx, ty, tz, instanceId))
		{
			return new Location(tx, ty, tz);
		}
		
		// Se falhou, usar o método original para encontrar ponto mais próximo
		return findNearestValidLocation(x, y, z, tx, ty, tz, instanceId);
	}
	
	private Location findNearestValidLocation(int fromX, int fromY, int fromZ, int toX, int toY, int toZ, int instanceId)
	{
		int geoX = getGeoX(fromX);
		int geoY = getGeoY(fromY);
		fromZ = getNearestZ(geoX, geoY, fromZ);
		int tGeoX = getGeoX(toX);
		int tGeoY = getGeoY(toY);
		toZ = getNearestZ(tGeoX, tGeoY, toZ);
		
		if (DoorData.getInstance().checkIfDoorsBetween(fromX, fromY, fromZ, toX, toY, toZ, instanceId, false))
		{
			return new Location(fromX, fromY, getHeight(fromX, fromY, fromZ));
		}
		
		LinePointIterator pointIter = new LinePointIterator(geoX, geoY, tGeoX, tGeoY);
		// first point is guaranteed to be available
		pointIter.next();
		int prevX = pointIter.x();
		int prevY = pointIter.y();
		int prevZ = fromZ;
		Location lastValid = new Location(fromX, fromY, fromZ);
		
		while (pointIter.next())
		{
			int curX = pointIter.x();
			int curY = pointIter.y();
			int curZ = getNearestZ(curX, curY, prevZ);
			
			if (hasGeoPos(prevX, prevY))
			{
				Direction dir = GeoUtils.computeDirection(prevX, prevY, curX, curY);
				boolean canEnter = false;
				if (canEnterNeighbors(prevX, prevY, prevZ, dir))
				{
					// check diagonal movement
					switch (dir)
					{
						case NORTH_EAST:
							canEnter = canEnterNeighbors(prevX, prevY - 1, prevZ, Direction.EAST) && canEnterNeighbors(prevX + 1, prevY, prevZ, Direction.NORTH);
							break;
						case NORTH_WEST:
							canEnter = canEnterNeighbors(prevX, prevY - 1, prevZ, Direction.WEST) && canEnterNeighbors(prevX - 1, prevY, prevZ, Direction.NORTH);
							break;
						case SOUTH_EAST:
							canEnter = canEnterNeighbors(prevX, prevY + 1, prevZ, Direction.EAST) && canEnterNeighbors(prevX + 1, prevY, prevZ, Direction.SOUTH);
							break;
						case SOUTH_WEST:
							canEnter = canEnterNeighbors(prevX, prevY + 1, prevZ, Direction.WEST) && canEnterNeighbors(prevX - 1, prevY, prevZ, Direction.SOUTH);
							break;
						default:
							canEnter = true;
							break;
					}
				}
				
				if (!canEnter)
				{
					// can't move, return previous valid location
					return lastValid;
				}
			}
			
			// Atualizar última localização válida
			lastValid = new Location(getWorldX(curX), getWorldY(curY), curZ);
			
			prevX = curX;
			prevY = curY;
			prevZ = curZ;
		}
		
		if (hasGeoPos(prevX, prevY) && (prevZ != toZ))
		{
			// different floors, return last valid location
			return lastValid;
		}
		
		return lastValid;
	}
	
	public boolean canMove(AbstractNodeLoc actor, WorldObject target)
	{
		return canMove(actor.getX(), actor.getY(), actor.getZ(), target.getX(), target.getY(), target.getZ(), target.getInstanceId());
	}
	
	public boolean canMove(WorldObject actor, WorldObject target)
	{
		return canMove(actor.getX(), actor.getY(), actor.getZ(), target.getX(), target.getY(), target.getZ(), target.getInstanceId());
	}
	
	/**
	 * Checks if its possible to move from one location to another.
	 * @param fromX the X coordinate to start checking from
	 * @param fromY the Y coordinate to start checking from
	 * @param fromZ the Z coordinate to start checking from
	 * @param toX the X coordinate to end checking at
	 * @param toY the Y coordinate to end checking at
	 * @param toZ the Z coordinate to end checking at
	 * @param instanceId the instance ID
	 * @return {@code true} if the character at start coordinates can move to end coordinates, {@code false} otherwise
	 */
	public boolean canMove(int fromX, int fromY, int fromZ, int toX, int toY, int toZ, int instanceId)
	{
		return validateMovementPath(fromX, fromY, fromZ, toX, toY, toZ, instanceId);
	}
	
	/**
	 * Verifica rigorosamente se pode mover em uma direção, considerando todos os bloqueios
	 * @param geoX
	 * @param geoY
	 * @param worldZ
	 * @param direction
	 * @return
	 */
	public boolean canMoveToDirection(int geoX, int geoY, int worldZ, Direction direction)
	{
		if (!hasGeoPos(geoX, geoY))
		{
			return true; // Sem geodata, movimento livre
		}
		
		// Verificar se a direção principal está bloqueada
		if (!_driver.canEnterNeighbors(geoX, geoY, worldZ, direction))
		{
			return false;
		}
		
		// Verificação extra para diagonais
		if (direction.isDiagonal())
		{
			return canMoveDiagonal(geoX, geoY, worldZ, direction);
		}
		
		return true;
	}
	
	/**
	 * Verificação rigorosa para movimento diagonal
	 * @param geoX
	 * @param geoY
	 * @param worldZ
	 * @param diagonalDir
	 * @return
	 */
	private boolean canMoveDiagonal(int geoX, int geoY, int worldZ, Direction diagonalDir)
	{
		switch (diagonalDir)
		{
			case NORTH_EAST:
				return _driver.canEnterNeighbors(geoX, geoY, worldZ, Direction.NORTH, Direction.EAST) && _driver.canEnterNeighbors(geoX, geoY - 1, worldZ, Direction.EAST) && _driver.canEnterNeighbors(geoX + 1, geoY, worldZ, Direction.NORTH);
			
			case NORTH_WEST:
				return _driver.canEnterNeighbors(geoX, geoY, worldZ, Direction.NORTH, Direction.WEST) && _driver.canEnterNeighbors(geoX, geoY - 1, worldZ, Direction.WEST) && _driver.canEnterNeighbors(geoX - 1, geoY, worldZ, Direction.NORTH);
			
			case SOUTH_EAST:
				return _driver.canEnterNeighbors(geoX, geoY, worldZ, Direction.SOUTH, Direction.EAST) && _driver.canEnterNeighbors(geoX, geoY + 1, worldZ, Direction.EAST) && _driver.canEnterNeighbors(geoX + 1, geoY, worldZ, Direction.SOUTH);
			
			case SOUTH_WEST:
				return _driver.canEnterNeighbors(geoX, geoY, worldZ, Direction.SOUTH, Direction.WEST) && _driver.canEnterNeighbors(geoX, geoY + 1, worldZ, Direction.WEST) && _driver.canEnterNeighbors(geoX - 1, geoY, worldZ, Direction.SOUTH);
			
			default:
				return true;
		}
	}
	
	/**
	 * Valida toda a trajetória do movimento com verificações rigorosas
	 * @param fromX
	 * @param fromY
	 * @param fromZ
	 * @param toX
	 * @param toY
	 * @param toZ
	 * @param instanceId
	 * @return
	 */
	public boolean validateMovementPath(int fromX, int fromY, int fromZ, int toX, int toY, int toZ, int instanceId)
	{
		int geoX = getGeoX(fromX);
		int geoY = getGeoY(fromY);
		fromZ = getNearestZ(geoX, geoY, fromZ);
		int tGeoX = getGeoX(toX);
		int tGeoY = getGeoY(toY);
		toZ = getNearestZ(tGeoX, tGeoY, toZ);
		
		if (DoorData.getInstance().checkIfDoorsBetween(fromX, fromY, fromZ, toX, toY, toZ, instanceId, false))
		{
			return false;
		}
		
		LinePointIterator pointIter = new LinePointIterator(geoX, geoY, tGeoX, tGeoY);
		
		// Verificar ponto inicial
		if (hasCollisionAt(getWorldX(geoX), getWorldY(geoY), fromZ, instanceId))
		{
			return false;
		}
		
		pointIter.next();
		int prevX = pointIter.x();
		int prevY = pointIter.y();
		int prevZ = fromZ;
		
		while (pointIter.next())
		{
			int curX = pointIter.x();
			int curY = pointIter.y();
			int curZ = getNearestZ(curX, curY, prevZ);
			
			Direction dir = GeoUtils.computeDirection(prevX, prevY, curX, curY);
			
			// Verificação rigorosa de direção
			// Verificar colisão na posição atual
			// Verificação adicional de altura para movimento válido
			if (!canMoveToDirection(prevX, prevY, prevZ, dir) || hasCollisionAt(getWorldX(curX), getWorldY(curY), curZ, instanceId) || (Math.abs(curZ - prevZ) > MAX_STEP_HEIGHT))
			{
				return false;
			}
			
			prevX = curX;
			prevY = curY;
			prevZ = curZ;
		}
		
		return true;
	}
	
	/**
	 * Verifica colisão imediata na posição atual do player
	 * @param x
	 * @param y
	 * @param z
	 * @param instanceId
	 * @return
	 */
	public boolean hasCollisionAt(int x, int y, int z, int instanceId)
	{
		int geoX = getGeoX(x);
		int geoY = getGeoY(y);
		z = getNearestZ(geoX, geoY, z);
		
		// Se não tem geodata, não há colisão
		if (!hasGeoPos(geoX, geoY))
		{
			return false;
		}
		
		// Verificar se está em uma célula bloqueada
		// Verificar se há doors na posição
		if (isBlockedCell(geoX, geoY, z) || DoorData.getInstance().checkIfDoorsBetween(x, y, z, x, y, z, instanceId, false))
		{
			return true;
		}
		
		return false;
	}
	
	/**
	 * Verifica se uma célula está completamente bloqueada
	 * @param geoX
	 * @param geoY
	 * @param worldZ
	 * @return
	 */
	private boolean isBlockedCell(int geoX, int geoY, int worldZ)
	{
		// Célula está bloqueada se não pode mover em nenhuma direção
		return !_driver.canEnterAllNeighbors(geoX, geoY, worldZ);
	}
	
	public int traceTerrainZ(int x, int y, int z, int tx, int ty)
	{
		int geoX = getGeoX(x);
		int geoY = getGeoY(y);
		z = getNearestZ(geoX, geoY, z);
		int tGeoX = getGeoX(tx);
		int tGeoY = getGeoY(ty);
		
		LinePointIterator pointIter = new LinePointIterator(geoX, geoY, tGeoX, tGeoY);
		// first point is guaranteed to be available
		pointIter.next();
		int prevZ = z;
		
		while (pointIter.next())
		{
			int curX = pointIter.x();
			int curY = pointIter.y();
			int curZ = getNearestZ(curX, curY, prevZ);
			
			prevZ = curZ;
		}
		
		return prevZ;
	}
	
	/**
	 * Checks if its possible to move from one location to another.
	 * @param from the {@code ILocational} to start checking from
	 * @param toX the X coordinate to end checking at
	 * @param toY the Y coordinate to end checking at
	 * @param toZ the Z coordinate to end checking at
	 * @return {@code true} if the character at start coordinates can move to end coordinates, {@code false} otherwise
	 */
	public boolean canMove(ILocational from, int toX, int toY, int toZ)
	{
		return canMove(from.getX(), from.getY(), from.getZ(), toX, toY, toZ, from.getInstanceId());
	}
	
	/**
	 * Checks if its possible to move from one location to another.
	 * @param from the {@code ILocational} to start checking from
	 * @param to the {@code ILocational} to end checking at
	 * @return {@code true} if the character at start coordinates can move to end coordinates, {@code false} otherwise
	 */
	public boolean canMove(ILocational from, ILocational to)
	{
		return canMove(from, to.getX(), to.getY(), to.getZ());
	}
	
	/**
	 * Checks the specified position for available geodata.
	 * @param x the X coordinate
	 * @param y the Y coordinate
	 * @return {@code true} if there is geodata for the given coordinates, {@code false} otherwise
	 */
	public boolean hasGeo(int x, int y)
	{
		return hasGeoPos(getGeoX(x), getGeoY(y));
	}
	
	public static GeoData getInstance()
	{
		return SingletonHolder._instance;
	}
	
	private static class SingletonHolder
	{
		protected final static GeoData _instance = new GeoData();
	}
}