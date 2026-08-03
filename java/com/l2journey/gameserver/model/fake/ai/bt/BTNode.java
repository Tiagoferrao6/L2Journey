package com.l2journey.gameserver.model.fake.ai.bt;

import com.l2journey.gameserver.model.actor.instance.FakePlayer;

/**
 * Interface for all Behavior Tree nodes for FakePlayer AI decision making.
 */
public interface BTNode
{
	BTStatus execute(FakePlayer bot);
}
