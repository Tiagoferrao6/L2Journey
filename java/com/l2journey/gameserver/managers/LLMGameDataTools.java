package com.l2journey.gameserver.managers;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.l2journey.gameserver.data.xml.ItemData;
import com.l2journey.gameserver.data.xml.NpcData;
import com.l2journey.gameserver.model.Location;
import com.l2journey.gameserver.model.actor.holders.npc.DropGroupHolder;
import com.l2journey.gameserver.model.actor.holders.npc.DropHolder;
import com.l2journey.gameserver.model.actor.templates.NpcTemplate;
import com.l2journey.gameserver.model.item.ItemTemplate;

/**
 * Exposes game data querying functions (NpcData, DropData, ItemData) for LLM decision engine.
 */
public class LLMGameDataTools
{
	private static final Logger LOGGER = Logger.getLogger(LLMGameDataTools.class.getName());

	public static class MobDropInfo
	{
		private final int _mobId;
		private final String _mobName;
		private final int _mobLevel;
		private final double _chancePercent;

		public MobDropInfo(int mobId, String mobName, int mobLevel, double chancePercent)
		{
			_mobId = mobId;
			_mobName = mobName;
			_mobLevel = mobLevel;
			_chancePercent = chancePercent;
		}

		public int getMobId() { return _mobId; }
		public String getMobName() { return _mobName; }
		public int getMobLevel() { return _mobLevel; }
		public double getChancePercent() { return _chancePercent; }
	}

	protected LLMGameDataTools()
	{
		LOGGER.info("LLMGameDataTools: Initialized Game Data LLM Query Helper.");
	}

	public static LLMGameDataTools getInstance()
	{
		return SingletonHolder.INSTANCE;
	}

	private static class SingletonHolder
	{
		protected static final LLMGameDataTools INSTANCE = new LLMGameDataTools();
	}

	/**
	 * Returns a list of mobs that drop a given item by name or ID.
	 */
	public List<MobDropInfo> queryMobDrops(int itemId)
	{
		List<MobDropInfo> result = new ArrayList<>();
		ItemTemplate item = ItemData.getInstance().getTemplate(itemId);
		if (item == null) return result;

		for (NpcTemplate npc : NpcData.getInstance().getAllNpcOfClassType("Monster"))
		{
			if (npc == null) continue;

			if (npc.getDropList() != null)
			{
				for (DropHolder drop : npc.getDropList())
				{
					if (drop.getItemId() == itemId)
					{
						result.add(new MobDropInfo(npc.getId(), npc.getName(), npc.getLevel(), drop.getChance()));
					}
				}
			}
		}

		return result;
	}

	/**
	 * Returns recommended hunting zone coordinates based on player level.
	 */
	public Location getRecommendedZone(int playerLevel)
	{
		if (playerLevel < 15)
		{
			return new Location(-14780, 123800, -3120); // Gludio Outskirts
		}
		else if (playerLevel < 30)
		{
			return new Location(-23500, 115000, -3600); // Ruins of Agony
		}
		else
		{
			return new Location(-18000, 138000, -3700); // Ruins of Despair
		}
	}
}
