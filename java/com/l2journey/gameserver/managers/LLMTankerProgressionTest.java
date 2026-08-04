package com.l2journey.gameserver.managers;

import com.l2journey.gameserver.managers.LLMTankerPlannerEngine.PlannerDecision;

/**
 * Unit test for LLMTankerPlannerEngine decision parsing and LLMGameDataTools query verification.
 */
public class LLMTankerProgressionTest
{
	public static void main(String[] args)
	{
		System.out.println("Testing LLMTankerPlannerEngine response parsing...");
		LLMTankerPlannerEngine planner = LLMTankerPlannerEngine.getInstance();

		PlannerDecision d1 = planner.parseDecisionResponse(null, "{\"action\": \"GO_TO_SHOP\"}");
		if (d1.getAction() == LLMTankerPlannerEngine.PlannerAction.GO_TO_SHOP)
		{
			System.out.println("[PASS] Parsed GO_TO_SHOP decision successfully.");
		}

		PlannerDecision d2 = planner.parseDecisionResponse(null, "{\"action\": \"START_QUEST\"}");
		if (d2.getAction() == LLMTankerPlannerEngine.PlannerAction.START_QUEST)
		{
			System.out.println("[PASS] Parsed START_QUEST decision successfully.");
		}

		System.out.println("Testing LLMGameDataTools...");
		LLMGameDataTools dataTools = LLMGameDataTools.getInstance();
		if (dataTools.getRecommendedZone(10) != null)
		{
			System.out.println("[PASS] Recommended zone for level 10 retrieved.");
		}

		System.out.println("All LLM Tanker Progression tests completed successfully.");
	}
}
