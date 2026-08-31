package com.l2journey.gameserver.geoengine.navmesh;

import java.util.ArrayList;
import java.util.List;

import com.l2journey.gameserver.model.Location;

public class NavMeshRoute
{
	private final String _name;
	private final List<Location> _points;
	
	public NavMeshRoute(String name)
	{
		_name = name;
		_points = new ArrayList<>();
	}
	
	public String getName()
	{
		return _name;
	}
	
	public void addPoint(Location loc)
	{
		_points.add(loc);
	}
	
	public List<Location> getPoints()
	{
		return _points;
	}
}
