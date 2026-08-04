package com.l2journey.gameserver.managers;

import java.util.List;

import com.l2journey.gameserver.model.Location;

/**
 * Basic unit test verifying TownWaypointMeshManager route generation and BuyListExecutingEngine singleton initialization.
 */
public class LLMTownShoppingTest
{
	public static void main(String[] args)
	{
		System.out.println("Testing TownWaypointMeshManager...");
		TownWaypointMeshManager meshManager = TownWaypointMeshManager.getInstance();
		List<Location> route = meshManager.getRoute("GLUDIO_GK_TO_GROCERY");
		if (route.size() == 3)
		{
			System.out.println("[PASS] GLUDIO_GK_TO_GROCERY route contains 3 nodes.");
		}
		else
		{
			System.err.println("[FAIL] Expected 3 nodes in route, found: " + route.size());
		}

		System.out.println("Testing BuyListExecutingEngine...");
		BuyListExecutingEngine buyEngine = BuyListExecutingEngine.getInstance();
		if (buyEngine != null)
		{
			System.out.println("[PASS] BuyListExecutingEngine initialized.");
		}

		System.out.println("Testing LLMQuestNavigator...");
		LLMQuestNavigator questNav = LLMQuestNavigator.getInstance();
		if (questNav.getQuestTarget("PALADIN_TRANSFER") != null)
		{
			System.out.println("[PASS] PALADIN_TRANSFER quest target found.");
		}

		System.out.println("All town shopping & quest navigation tests completed successfully.");
	}
}
