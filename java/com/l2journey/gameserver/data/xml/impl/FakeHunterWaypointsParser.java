package com.l2journey.gameserver.data.xml.impl;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.l2journey.commons.util.IXmlReader;
import com.l2journey.gameserver.model.actor.dna.HunterRoute;
import com.l2journey.gameserver.model.actor.dna.HunterRoute.RouteType;
import com.l2journey.gameserver.model.actor.dna.HunterRoute.WaypointNode;

public class FakeHunterWaypointsParser implements IXmlReader
{
	private static final Logger LOGGER = Logger.getLogger(FakeHunterWaypointsParser.class.getName());
	private final Map<String, HunterRoute> _routes = new ConcurrentHashMap<>();

	protected FakeHunterWaypointsParser()
	{
		load();
	}

	@Override
	public synchronized void load()
	{
		_routes.clear();
		parseDatapackFile("config/npcs/fake_hunter_waypoints.xml");
		LOGGER.info(getClass().getSimpleName() + ": Loaded " + _routes.size() + " fake hunter waypoint routes.");
	}

	@Override
	public void parseDocument(Document document, File file)
	{
		forEach(document, n ->
		{
			if ("waypoints".equalsIgnoreCase(n.getNodeName()))
			{
				forEach(n, routeNode ->
				{
					if ("route".equalsIgnoreCase(routeNode.getNodeName()))
					{
						String id = parseString(routeNode.getAttributes(), "id", "default_route");
						String typeStr = parseString(routeNode.getAttributes(), "type", "ZONE");
						RouteType type = RouteType.ZONE;
						try
						{
							type = RouteType.valueOf(typeStr.toUpperCase());
						}
						catch (Exception e)
						{
							type = RouteType.ZONE;
						}

						HunterRoute route = new HunterRoute(id, type);

						forEach(routeNode, node ->
						{
							if ("node".equalsIgnoreCase(node.getNodeName()))
							{
								int x = parseInteger(node.getAttributes(), "x");
								int y = parseInteger(node.getAttributes(), "y");
								int z = parseInteger(node.getAttributes(), "z");
								int jitter = parseInteger(node.getAttributes(), "jitter", 100);
								route.addNode(new WaypointNode(x, y, z, jitter));
							}
						});

						_routes.put(id, route);
					}
				});
			}
		});
	}

	public HunterRoute getRoute(String id)
	{
		return _routes.get(id);
	}

	public static FakeHunterWaypointsParser getInstance()
	{
		return SingletonHolder.INSTANCE;
	}

	private static class SingletonHolder
	{
		protected static final FakeHunterWaypointsParser INSTANCE = new FakeHunterWaypointsParser();
	}
}
