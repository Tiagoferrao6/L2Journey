package com.l2journey.gameserver.model.actor.fakeplayer.teleport;

import java.util.logging.Logger;

import com.l2journey.commons.threads.ThreadPool;
import com.l2journey.commons.util.Rnd;
import com.l2journey.gameserver.ai.Intention;
import com.l2journey.gameserver.model.Location;
import com.l2journey.gameserver.model.actor.Player;
import com.l2journey.gameserver.model.actor.fakeplayer.FakeHunterAI;
import com.l2journey.gameserver.model.item.instance.Item;
import com.l2journey.gameserver.model.item.enums.ItemProcessType;
import com.l2journey.gameserver.network.serverpackets.MagicSkillUse;
import com.l2journey.gameserver.network.serverpackets.SetupGauge;
import com.l2journey.gameserver.network.clientpackets.RequestBypassToServer;
import com.l2journey.gameserver.model.skill.Skill;
import com.l2journey.gameserver.data.xml.SkillData;
import com.l2journey.gameserver.managers.MapRegionManager;
import com.l2journey.gameserver.model.actor.enums.player.TeleportWhereType;

public class FakeTeleportEngine
{
	private static final Logger LOGGER = Logger.getLogger(FakeTeleportEngine.class.getName());
	
	/**
	 * Initiates a teleport sequence spanning multiple GKs if necessary.
	 */
	public static void beginTeleportJourney(Player bot, String fromNode, String toNode, Location gkLoc, String gkTargetBypass)
	{
		FakeHunterAI ai = (FakeHunterAI) bot.getAI();
		int requiredAdena = TeleportGraph.getInstance().calculateTotalCost(fromNode, toNode);
		
		if (bot.getAdena() < requiredAdena)
		{
			// Fallback: HUNTING_MODE
			LOGGER.info("FakeTeleportEngine: " + bot.getName() + " does not have " + requiredAdena + " adena. Falling back to HUNTING_MODE.");
			bot.sendMessage("I don't have enough Adena to travel to " + toNode + ". Going to hunt!");
			ai.getProfile().setZoneId("FARM_" + fromNode); // e.g. FARM_GIRAN
			return;
		}
		
		// 4.1 Move to GK
		ai.setIntention(Intention.MOVE_TO, gkLoc);
		
		// Schedule a task to check when bot arrives at GK
		ThreadPool.schedule(new Runnable()
		{
			@Override
			public void run()
			{
				if (bot == null || !bot.isOnline()) return;
				
				if (bot.isInsideRadius3D(gkLoc, 100))
				{
					// Arrived! Simulate human HTML reading delay (4.2)
					bot.getAI().setIntention(Intention.IDLE);
					int humanDelay = Rnd.get(1000, 2500); // 1s to 2.5s delay
					
					ThreadPool.schedule(() ->
					{
						// 4.3 Trigger the bypass
						LOGGER.info("FakeTeleportEngine: " + bot.getName() + " triggering bypass " + gkTargetBypass + " after " + humanDelay + "ms delay.");
						// Since we don't have direct access to process client packets from server side cleanly without a client,
						// we simulate the effect, or if RequestBypassToServer has a public handler, we can use it.
						// For now we will deduct adena and teleport manually to simulate the GK action.
						bot.reduceAdena(ItemProcessType.FEE, requiredAdena, null, false);
						// Fake the teleport (hardcoded for test route Dion to Gludio etc)
						if (toNode.equals("GLUDIO"))
						{
							bot.teleToLocation(-12672, 122776, -3116);
						}
						
					}, humanDelay);
				}
				else
				{
					// Check again in 1s
					ThreadPool.schedule(this, 1000);
				}
			}
		}, 1000);
	}
	
	/**
	 * Uses SOE naturally with cast time.
	 */
	public static void useScrollOfEscape(Player bot)
	{
		Item soe = bot.getInventory().getItemByItemId(736); // Scroll of Escape
		if (soe == null) return;
		
		bot.getAI().setIntention(Intention.IDLE);
		bot.destroyItem(ItemProcessType.DESTROY, soe, 1, bot, false);
		
		// Setup cast
		Skill skill = SkillData.getInstance().getSkill(2013, 1); // SOE Skill ID usually
		int castTime = 20000; // 20s
		
		bot.broadcastPacket(new MagicSkillUse(bot, bot, 2013, 1, castTime, 0));
		bot.broadcastPacket(new SetupGauge(bot.getObjectId(), 0, castTime));
		
		ThreadPool.schedule(() ->
		{
			if (bot.isOnline() && !bot.isDead())
			{
				bot.teleToLocation(MapRegionManager.getInstance().getTeleToLocation(bot, TeleportWhereType.TOWN));
			}
		}, castTime);
	}
}
