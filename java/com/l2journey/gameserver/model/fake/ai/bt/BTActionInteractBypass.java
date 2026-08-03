package com.l2journey.gameserver.model.fake.ai.bt;

import java.util.logging.Logger;

import com.l2journey.commons.util.Rnd;
import com.l2journey.gameserver.model.World;
import com.l2journey.gameserver.model.actor.Npc;
import com.l2journey.gameserver.model.actor.instance.FakePlayer;
import com.l2journey.gameserver.model.actor.instance.Teleporter;

/**
 * Behavior Tree Action Node: Targets an NPC in range, simulates action, and sends a native HTML dialog bypass.
 */
public class BTActionInteractBypass implements BTNode
{
	private static final Logger LOGGER = Logger.getLogger(BTActionInteractBypass.class.getName());

	private final String _bypassCommand;
	private final int _searchRadius;

	public BTActionInteractBypass(String bypassCommand, int searchRadius)
	{
		_bypassCommand = bypassCommand;
		_searchRadius = searchRadius;
	}

	@Override
	public BTStatus execute(FakePlayer bot)
	{
		if (bot == null)
		{
			return BTStatus.FAILURE;
		}

		Npc targetNpc = World.getInstance().getVisibleObjectsInRange(bot, Teleporter.class, _searchRadius)
			.stream()
			.findFirst()
			.orElse(null);

		if (targetNpc == null)
		{
			// Fallback: search any NPC in radius
			targetNpc = World.getInstance().getVisibleObjectsInRange(bot, Npc.class, _searchRadius)
				.stream()
				.findFirst()
				.orElse(null);
		}

		if (targetNpc != null)
		{
			bot.setTarget(targetNpc);
			targetNpc.onAction(bot);
			targetNpc.onBypassFeedback(bot, _bypassCommand);
			LOGGER.info("FakePlayer [" + bot.getName() + "] interacted with NPC [" + targetNpc.getName() + "] using bypass '" + _bypassCommand + "'.");
			return BTStatus.SUCCESS;
		}

		// Direct fallback teleport if NPC is absent
		bot.teleToLocation(-19120 + Rnd.get(-200, 200), 136816 + Rnd.get(-200, 200), -3752);
		LOGGER.info("FakePlayer [" + bot.getName() + "] executed fallback teleport to hunting ground.");
		return BTStatus.SUCCESS;
	}
}
