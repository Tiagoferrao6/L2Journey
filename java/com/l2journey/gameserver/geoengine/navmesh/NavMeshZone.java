package com.l2journey.gameserver.geoengine.navmesh;

import java.util.ArrayList;
import java.util.List;

import com.l2journey.gameserver.model.Location;

public class NavMeshZone
{
	private final String _name;
	private final List<Location> _polygon;
	
	public NavMeshZone(String name)
	{
		_name = name;
		_polygon = new ArrayList<>();
	}
	
	public String getName()
	{
		return _name;
	}
	
	public void addPoint(Location loc)
	{
		_polygon.add(loc);
	}
	
	public boolean isInside(int x, int y)
	{
		boolean result = false;
		for (int i = 0, j = _polygon.size() - 1; i < _polygon.size(); j = i++)
		{
			Location pi = _polygon.get(i);
			Location pj = _polygon.get(j);
			
			if ((pi.getY() > y) != (pj.getY() > y) &&
				(x < (pj.getX() - pi.getX()) * (y - pi.getY()) / (pj.getY() - pi.getY()) + pi.getX()))
			{
				result = !result;
			}
		}
		return result;
	}
}
