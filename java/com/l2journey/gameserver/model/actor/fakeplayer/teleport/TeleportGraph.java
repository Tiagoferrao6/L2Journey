package com.l2journey.gameserver.model.actor.fakeplayer.teleport;

import java.util.HashMap;
import java.util.Map;

/**
 * Maps the Gatekeeper network to calculate Adena costs.
 */
public class TeleportGraph
{
	// Key: "FROM_TO", Value: Adena Cost
	private final Map<String, Integer> _edgeCosts = new HashMap<>();
	
	protected TeleportGraph()
	{
		// Basic setup of classical Giran to Gludio route for testing
		_edgeCosts.put("GIRAN_DION", 6800);
		_edgeCosts.put("DION_GLUDIO", 4100);
		_edgeCosts.put("DION_GIRAN", 6800);
		_edgeCosts.put("GLUDIO_DION", 4100);
		
		_edgeCosts.put("GIRAN_ADEN", 13000);
		_edgeCosts.put("ADEN_GIRAN", 13000);
	}
	
	public int getDirectCost(String from, String to)
	{
		return _edgeCosts.getOrDefault(from + "_" + to, 0);
	}
	
	// A real A* search would go here, for now it returns a hardcoded sum for testing
	public int calculateTotalCost(String from, String to)
	{
		if (from.equals("GIRAN") && to.equals("GLUDIO"))
		{
			return getDirectCost("GIRAN", "DION") + getDirectCost("DION", "GLUDIO");
		}
		
		return getDirectCost(from, to);
	}
	
	public static TeleportGraph getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final TeleportGraph INSTANCE = new TeleportGraph();
	}
}
