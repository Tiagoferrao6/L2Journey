package com.l2journey.gameserver.model.actor.dna;

import java.util.ArrayList;
import java.util.List;
import com.l2journey.commons.util.Rnd;
import com.l2journey.gameserver.model.Location;

public class HunterRoute
{
	public enum RouteType
	{
		TOWN,
		TRANSIT,
		ZONE,
		EXCLUSIVE
	}

	public static class WaypointNode
	{
		private final int _x;
		private final int _y;
		private final int _z;
		private final int _jitter;

		public WaypointNode(int x, int y, int z, int jitter)
		{
			_x = x;
			_y = y;
			_z = z;
			_jitter = jitter;
		}

		public int getX()
		{
			return _x;
		}

		public int getY()
		{
			return _y;
		}

		public int getZ()
		{
			return _z;
		}

		public int getJitter()
		{
			return _jitter;
		}

		public Location getRandomizedLocation()
		{
			if (_jitter <= 0)
			{
				return new Location(_x, _y, _z);
			}
			int offsetX = Rnd.get(-_jitter, _jitter);
			int offsetY = Rnd.get(-_jitter, _jitter);
			return new Location(_x + offsetX, _y + offsetY, _z);
		}
	}

	private final String _routeId;
	private final RouteType _type;
	private final List<WaypointNode> _nodes = new ArrayList<>();

	public HunterRoute(String routeId, RouteType type)
	{
		_routeId = routeId;
		_type = type;
	}

	public String getRouteId()
	{
		return _routeId;
	}

	public RouteType getType()
	{
		return _type;
	}

	public List<WaypointNode> getNodes()
	{
		return _nodes;
	}

	public void addNode(WaypointNode node)
	{
		_nodes.add(node);
	}
}
