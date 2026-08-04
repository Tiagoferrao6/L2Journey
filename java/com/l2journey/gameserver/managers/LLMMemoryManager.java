package com.l2journey.gameserver.managers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.l2journey.commons.database.DatabaseFactory;
import com.l2journey.commons.threads.ThreadPool;

/**
 * LLMMemoryManager handles persistent emotional and relationship state for LLM Bots/FakeHunters.
 * Tracks memory logs (character_llm_memories) and relationship scores/statuses (character_llm_relationships).
 */
public class LLMMemoryManager
{
	private static final Logger LOGGER = Logger.getLogger(LLMMemoryManager.class.getName());

	public enum EventType
	{
		HELPED_IN_COMBAT,
		STOLE_LOOT,
		KS_MOB,
		PK_ATTACK,
		CHAT_COMPLIMENT,
		CHAT_INSULT
	}

	public enum RelationshipStatus
	{
		ALLY,
		FRIEND,
		NEUTRAL,
		SUSPICIOUS,
		ENEMY,
		RIVAL;

		public static RelationshipStatus fromScore(int score)
		{
			if (score >= 80) return ALLY;
			if (score >= 30) return FRIEND;
			if (score >= -29) return NEUTRAL;
			if (score >= -50) return SUSPICIOUS;
			if (score >= -79) return ENEMY;
			return RIVAL;
		}
	}

	public static class MemoryEntry
	{
		private final long _memoryId;
		private final int _botObjectId;
		private final int _targetObjectId;
		private final EventType _eventType;
		private final String _description;
		private final Timestamp _createdAt;

		public MemoryEntry(long memoryId, int botObjectId, int targetObjectId, EventType eventType, String description, Timestamp createdAt)
		{
			_memoryId = memoryId;
			_botObjectId = botObjectId;
			_targetObjectId = targetObjectId;
			_eventType = eventType;
			_description = description;
			_createdAt = createdAt;
		}

		public long getMemoryId() { return _memoryId; }
		public int getBotObjectId() { return _botObjectId; }
		public int getTargetObjectId() { return _targetObjectId; }
		public EventType getEventType() { return _eventType; }
		public String getDescription() { return _description; }
		public Timestamp getCreatedAt() { return _createdAt; }
	}

	public static class RelationshipEntry
	{
		private final int _botObjectId;
		private final int _targetObjectId;
		private final String _targetName;
		private final int _affinityScore;
		private final RelationshipStatus _status;
		private final Timestamp _lastInteractionTime;

		public RelationshipEntry(int botObjectId, int targetObjectId, String targetName, int affinityScore, RelationshipStatus status, Timestamp lastInteractionTime)
		{
			_botObjectId = botObjectId;
			_targetObjectId = targetObjectId;
			_targetName = targetName;
			_affinityScore = affinityScore;
			_status = status;
			_lastInteractionTime = lastInteractionTime;
		}

		public int getBotObjectId() { return _botObjectId; }
		public int getTargetObjectId() { return _targetObjectId; }
		public String getTargetName() { return _targetName; }
		public int getAffinityScore() { return _affinityScore; }
		public RelationshipStatus getStatus() { return _status; }
		public Timestamp getLastInteractionTime() { return _lastInteractionTime; }
	}

	protected LLMMemoryManager()
	{
		initDatabaseTables();
		LOGGER.info("LLMMemoryManager: Initialized Bot Memory & Relationship Engine.");
	}

	public static LLMMemoryManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}

	private static class SingletonHolder
	{
		protected static final LLMMemoryManager INSTANCE = new LLMMemoryManager();
	}

	private void initDatabaseTables()
	{
		try (Connection con = DatabaseFactory.getConnection())
		{
			try (PreparedStatement ps = con.prepareStatement(
				"CREATE TABLE IF NOT EXISTS `character_llm_relationships` (" +
				"  `bot_object_id` INT NOT NULL," +
				"  `target_object_id` INT NOT NULL," +
				"  `target_name` VARCHAR(45) NOT NULL," +
				"  `affinity_score` INT NOT NULL DEFAULT 0," +
				"  `relationship_status` VARCHAR(32) NOT NULL DEFAULT 'NEUTRAL'," +
				"  `last_interaction_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP," +
				"  PRIMARY KEY (`bot_object_id`, `target_object_id`)" +
				") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;"))
			{
				ps.executeUpdate();
			}

			try (PreparedStatement ps = con.prepareStatement(
				"CREATE TABLE IF NOT EXISTS `character_llm_memories` (" +
				"  `memory_id` BIGINT AUTO_INCREMENT PRIMARY KEY," +
				"  `bot_object_id` INT NOT NULL," +
				"  `target_object_id` INT NOT NULL," +
				"  `event_type` VARCHAR(32) NOT NULL," +
				"  `description` TEXT NOT NULL," +
				"  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
				"  KEY `idx_bot_target` (`bot_object_id`, `target_object_id`)" +
				") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;"))
			{
				ps.executeUpdate();
			}
		}
		catch (Exception e)
		{
			LOGGER.log(Level.WARNING, "LLMMemoryManager: Failed initializing database tables", e);
		}
	}

	/**
	 * Records a social/combat memory event asynchronously and adjusts relationship score.
	 */
	public void recordMemory(int botObjectId, int targetObjectId, String targetName, EventType eventType, int affinityDelta, String description)
	{
		ThreadPool.execute(() -> {
			try (Connection con = DatabaseFactory.getConnection())
			{
				// 1. Insert memory record
				try (PreparedStatement ps = con.prepareStatement(
					"INSERT INTO `character_llm_memories` (`bot_object_id`, `target_object_id`, `event_type`, `description`) VALUES (?, ?, ?, ?)"))
				{
					ps.setInt(1, botObjectId);
					ps.setInt(2, targetObjectId);
					ps.setString(3, eventType.name());
					ps.setString(4, description);
					ps.executeUpdate();
				}

				// 2. Fetch current relationship or default
				int currentScore = 0;
				try (PreparedStatement ps = con.prepareStatement(
					"SELECT `affinity_score` FROM `character_llm_relationships` WHERE `bot_object_id` = ? AND `target_object_id` = ?"))
				{
					ps.setInt(1, botObjectId);
					ps.setInt(2, targetObjectId);
					try (ResultSet rs = ps.executeQuery())
					{
						if (rs.next())
						{
							currentScore = rs.getInt("affinity_score");
						}
					}
				}

				int newScore = Math.max(-100, Math.min(100, currentScore + affinityDelta));
				RelationshipStatus newStatus = RelationshipStatus.fromScore(newScore);

				// 3. Upsert relationship
				try (PreparedStatement ps = con.prepareStatement(
					"REPLACE INTO `character_llm_relationships` (`bot_object_id`, `target_object_id`, `target_name`, `affinity_score`, `relationship_status`, `last_interaction_time`) VALUES (?, ?, ?, ?, ?, CURRENT_TIMESTAMP)"))
				{
					ps.setInt(1, botObjectId);
					ps.setInt(2, targetObjectId);
					ps.setString(3, targetName != null ? targetName : "Unknown");
					ps.setInt(4, newScore);
					ps.setString(5, newStatus.name());
					ps.executeUpdate();
				}
			}
			catch (Exception e)
			{
				LOGGER.log(Level.WARNING, "LLMMemoryManager: Error recording memory for bot " + botObjectId + " and target " + targetObjectId, e);
			}
		});
	}

	/**
	 * Retrieves the relationship record for a given bot and target character.
	 */
	public RelationshipEntry getRelationship(int botObjectId, int targetObjectId)
	{
		try (Connection con = DatabaseFactory.getConnection();
		     PreparedStatement ps = con.prepareStatement(
				"SELECT `target_name`, `affinity_score`, `relationship_status`, `last_interaction_time` FROM `character_llm_relationships` WHERE `bot_object_id` = ? AND `target_object_id` = ?"))
		{
			ps.setInt(1, botObjectId);
			ps.setInt(2, targetObjectId);
			try (ResultSet rs = ps.executeQuery())
			{
				if (rs.next())
				{
					String name = rs.getString("target_name");
					int score = rs.getInt("affinity_score");
					String statusStr = rs.getString("relationship_status");
					Timestamp lastTime = rs.getTimestamp("last_interaction_time");
					RelationshipStatus status = RelationshipStatus.fromScore(score);
					try { status = RelationshipStatus.valueOf(statusStr); } catch (Exception ignored) {}
					return new RelationshipEntry(botObjectId, targetObjectId, name, score, status, lastTime);
				}
			}
		}
		catch (Exception e)
		{
			LOGGER.log(Level.WARNING, "LLMMemoryManager: Error fetching relationship", e);
		}

		return new RelationshipEntry(botObjectId, targetObjectId, "Unknown", 0, RelationshipStatus.NEUTRAL, new Timestamp(System.currentTimeMillis()));
	}

	/**
	 * Retrieves the most recent N memories between a bot and target character.
	 */
	public List<MemoryEntry> getRecentMemories(int botObjectId, int targetObjectId, int limit)
	{
		List<MemoryEntry> result = new ArrayList<>();
		try (Connection con = DatabaseFactory.getConnection();
		     PreparedStatement ps = con.prepareStatement(
				"SELECT `memory_id`, `event_type`, `description`, `created_at` FROM `character_llm_memories` WHERE `bot_object_id` = ? AND `target_object_id` = ? ORDER BY `memory_id` DESC LIMIT ?"))
		{
			ps.setInt(1, botObjectId);
			ps.setInt(2, targetObjectId);
			ps.setInt(3, limit);
			try (ResultSet rs = ps.executeQuery())
			{
				while (rs.next())
				{
					long id = rs.getLong("memory_id");
					String typeStr = rs.getString("event_type");
					String desc = rs.getString("description");
					Timestamp created = rs.getTimestamp("created_at");

					EventType type = EventType.HELPED_IN_COMBAT;
					try { type = EventType.valueOf(typeStr); } catch (Exception ignored) {}

					result.add(new MemoryEntry(id, botObjectId, targetObjectId, type, desc, created));
				}
			}
		}
		catch (Exception e)
		{
			LOGGER.log(Level.WARNING, "LLMMemoryManager: Error fetching recent memories", e);
		}
		return result;
	}

	/**
	 * Formats a concise context text string containing relationship status & top 3 memories for LLM prompts.
	 */
	public String getFormattedMemoryContext(int botObjectId, int targetObjectId, String targetName)
	{
		RelationshipEntry rel = getRelationship(botObjectId, targetObjectId);
		List<MemoryEntry> memories = getRecentMemories(botObjectId, targetObjectId, 3);

		StringBuilder sb = new StringBuilder();
		sb.append("Histórico Social com ").append(targetName).append(":\n");
		sb.append("- Relacionamento: ").append(rel.getStatus().name()).append(" (Afinidade: ").append(rel.getAffinityScore()).append("/100)\n");

		if (memories.isEmpty())
		{
			sb.append("- Memórias: Nenhum histórico anterior registrado.\n");
		}
		else
		{
			sb.append("- Memórias Recentes:\n");
			for (int i = 0; i < memories.size(); i++)
			{
				MemoryEntry m = memories.get(i);
				sb.append("  ").append(i + 1).append(") ").append(m.getDescription()).append("\n");
			}
		}

		return sb.toString();
	}
}
