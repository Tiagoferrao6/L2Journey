package com.l2journey.gameserver.model.fake.ai.bt;

import com.l2journey.gameserver.ai.Intention;
import com.l2journey.gameserver.model.Location;
import com.l2journey.gameserver.model.actor.instance.FakePlayer;

/**
 * Behavior Tree Action Node: Walks the bot to a specific target location using Pathfinding.
 */
public class BTActionWalkToNpc implements BTNode
{
	private final int _targetX;
	private final int _targetY;
	private final int _targetZ;
	private final double _arrivalDistance;

	public BTActionWalkToNpc(int targetX, int targetY, int targetZ, double arrivalDistance)
	{
		_targetX = targetX;
		_targetY = targetY;
		_targetZ = targetZ;
		_arrivalDistance = arrivalDistance;
	}

	@Override
	public BTStatus execute(FakePlayer bot)
	{
		if (bot == null)
		{
			return BTStatus.FAILURE;
		}

		double distance = bot.calculateDistance2D(_targetX, _targetY, 0);
		if (distance <= _arrivalDistance)
		{
			return BTStatus.SUCCESS;
		}

		if (!bot.isMoving())
		{
			bot.getAI().setIntention(Intention.MOVE_TO, new Location(_targetX, _targetY, _targetZ));
		}

		return BTStatus.RUNNING;
	}
}
