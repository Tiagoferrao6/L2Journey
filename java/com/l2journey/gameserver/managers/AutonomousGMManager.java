package com.l2journey.gameserver.managers;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.l2journey.commons.threads.ThreadPool;
import com.l2journey.commons.util.Rnd;
import com.l2journey.gameserver.data.xml.NpcData;
import com.l2journey.gameserver.model.World;
import com.l2journey.gameserver.model.actor.Npc;
import com.l2journey.gameserver.model.actor.Player;
import com.l2journey.gameserver.model.actor.templates.NpcTemplate;
import com.l2journey.gameserver.model.item.enums.ItemProcessType;
import com.l2journey.gameserver.network.enums.ChatType;
import com.l2journey.gameserver.network.serverpackets.CreatureSay;

/**
 * Autonomous Game Master (GM) & Storyteller Orchestrator Engine.
 * Schedules dynamic live events, broadcasts global announcements, spawns event mobs/bosses,
 * and handles event reward distribution and cleanup.
 */
public class AutonomousGMManager
{
	private static final Logger LOGGER = Logger.getLogger(AutonomousGMManager.class.getName());

	public enum GMEventState
	{
		IDLE,
		STARTING,
		IN_PROGRESS,
		CLEANUP
	}

	public static class DynamicGMEvent
	{
		private final String _title;
		private final String _lore;
		private final String _zoneName;
		private final int _x;
		private final int _y;
		private final int _z;
		private final int _bossNpcId;
		private final List<Npc> _spawnedNpcs = new ArrayList<>();
		private Npc _bossInstance;
		private final long _startTime;

		public DynamicGMEvent(String title, String lore, String zoneName, int x, int y, int z, int bossNpcId)
		{
			_title = title;
			_lore = lore;
			_zoneName = zoneName;
			_x = x;
			_y = y;
			_z = z;
			_bossNpcId = bossNpcId;
			_startTime = System.currentTimeMillis();
		}

		public String getTitle() { return _title; }
		public String getLore() { return _lore; }
		public String getZoneName() { return _zoneName; }
		public int getX() { return _x; }
		public int getY() { return _y; }
		public int getZ() { return _z; }
		public int getBossNpcId() { return _bossNpcId; }
		public List<Npc> getSpawnedNpcs() { return _spawnedNpcs; }
		public Npc getBossInstance() { return _bossInstance; }
		public void setBossInstance(Npc boss) { _bossInstance = boss; }
		public long getStartTime() { return _startTime; }
	}

	private GMEventState _state = GMEventState.IDLE;
	private DynamicGMEvent _currentEvent = null;

	protected AutonomousGMManager()
	{
		LOGGER.info("AutonomousGMManager: Initialized Autonomous GM & Storyteller Director.");
	}

	public static AutonomousGMManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}

	private static class SingletonHolder
	{
		protected static final AutonomousGMManager INSTANCE = new AutonomousGMManager();
	}

	public GMEventState getEventState()
	{
		return _state;
	}

	public DynamicGMEvent getCurrentEvent()
	{
		return _currentEvent;
	}

	/**
	 * Triggers a narrative live event by generating lore via Gemini LLM Storyteller and broadcasting announcements.
	 */
	public void triggerNarrativeEvent(String eventType, String zoneName, int x, int y, int z, int bossNpcId)
	{
		String title = "Invasão em " + zoneName;
		LLMStorytellerEngine.getInstance().generateEventLoreAsync(eventType, zoneName, lore -> {
			startDynamicEvent(title, lore, zoneName, x, y, z, bossNpcId, 5);
		});
	}

	/**
	 * Starts a dynamic narrative event orchestrated by the Autonomous GM.
	 */
	public synchronized void startDynamicEvent(String title, String lore, String zoneName, int x, int y, int z, int bossNpcId, int minionCount)
	{
		if (_state != GMEventState.IDLE)
		{
			LOGGER.warning("AutonomousGMManager: Cannot start event, current state is " + _state);
			return;
		}

		_state = GMEventState.STARTING;
		_currentEvent = new DynamicGMEvent(title, lore, zoneName, x, y, z, bossNpcId);

		LOGGER.info("AutonomousGMManager: Starting event [" + title + "] in " + zoneName);

		// 1. Broadcast dramatic announcement to all online players
		broadcastGMAnnouncement("⚔️ [EVENTO NARRATIVO GM] " + title + "!", true);
		broadcastGMAnnouncement("📜 Lore: " + lore, false);
		broadcastGMAnnouncement("📍 Localização: " + zoneName + " - Unam-se para derrotar a ameaça!", false);

		// 2. Spawn event boss and minions
		ThreadPool.execute(() -> {
			try
			{
				NpcTemplate bossTemplate = NpcData.getInstance().getTemplate(bossNpcId > 0 ? bossNpcId : 20001);
				if (bossTemplate != null)
				{
					Npc boss = new Npc(bossTemplate);
					boss.spawnMe(x, y, z);
					_currentEvent.setBossInstance(boss);
					_currentEvent.getSpawnedNpcs().add(boss);
				}

				// Spawn minions around boss
				NpcTemplate minionTemplate = NpcData.getInstance().getTemplate(20035);
				if (minionTemplate != null)
				{
					for (int i = 0; i < Math.min(minionCount, 10); i++)
					{
						Npc minion = new Npc(minionTemplate);
						minion.spawnMe(x + Rnd.get(-150, 150), y + Rnd.get(-150, 150), z);
						_currentEvent.getSpawnedNpcs().add(minion);
					}
				}

				_state = GMEventState.IN_PROGRESS;
				LOGGER.info("AutonomousGMManager: Event [" + title + "] is now IN_PROGRESS.");

				// Schedule 10-minute maximum event duration timeout
				ThreadPool.schedule(() -> {
					if (_state == GMEventState.IN_PROGRESS)
					{
						broadcastGMAnnouncement("⌛ O tempo do evento [" + title + "] esgotou!", false);
						cleanupEventSpawns();
					}
				}, 600000); // 10 minutes
			}
			catch (Exception e)
			{
				LOGGER.log(Level.SEVERE, "AutonomousGMManager: Error spawning event entities", e);
				cleanupEventSpawns();
			}
		});
	}

	/**
	 * Broadcasts a global announcement to all online players in custom RPG GM formatting.
	 */
	public void broadcastGMAnnouncement(String text, boolean critical)
	{
		ChatType type = critical ? ChatType.CRITICAL_ANNOUNCEMENT : ChatType.ANNOUNCEMENT;
		CreatureSay msg = new CreatureSay(0, type, "[GM Director]", text);
		for (Player player : World.getInstance().getPlayers())
		{
			if (player != null && player.isOnline())
			{
				player.sendPacket(msg);
			}
		}
	}

	/**
	 * Triggered when the event boss is slain by players.
	 */
	public synchronized void onBossDefeated(Npc boss, Player killer)
	{
		if (_state != GMEventState.IN_PROGRESS || _currentEvent == null) return;

		String victorName = killer != null ? killer.getName() : "Herois de Aden";
		broadcastGMAnnouncement("🎉 [VITÓRIA DO EVENTO] " + victorName + " derrotou o chefe " + _currentEvent.getTitle() + "!", true);

		// Grant reward Adena (capped to prevent inflation)
		if (killer != null && killer.isOnline())
		{
			long rewardAdena = 10000;
			killer.getInventory().addAdena(ItemProcessType.QUEST, rewardAdena, killer, null);
			killer.sendMessage("[GM Event Reward] Você recebeu " + rewardAdena + " Adena por liderar a vitória no evento!");
		}

		// Schedule clean-up
		ThreadPool.schedule(this::cleanupEventSpawns, 5000);
	}

	/**
	 * Despawns residual event entities and resets state to IDLE.
	 */
	public synchronized void cleanupEventSpawns()
	{
		_state = GMEventState.CLEANUP;
		if (_currentEvent != null)
		{
			for (Npc npc : _currentEvent.getSpawnedNpcs())
			{
				if (npc != null && !npc.isDead())
				{
					npc.deleteMe();
				}
			}
			_currentEvent.getSpawnedNpcs().clear();
			LOGGER.info("AutonomousGMManager: Cleaned up event spawns for [" + _currentEvent.getTitle() + "]");
		}

		_currentEvent = null;
		_state = GMEventState.IDLE;
		broadcastGMAnnouncement("✨ O evento GM terminou. O mundo de Aden retorna à paz.", false);
	}
}
