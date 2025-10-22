/*
 * Copyright (c) 2025 L2Journey Project
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 * 
 * ---
 * 
 * Portions of this software are derived from the L2JMobius Project, 
 * shared under the MIT License. The original license terms are preserved where 
 * applicable..
 * 
 */
package com.l2journey.gameserver.model.achievements;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.l2journey.commons.database.DatabaseFactory;

/**
 * Player Achievement Manager - Handles achievement completion and claiming status
 * @author KingHanker
 */
public class PlayerAchievements
{
	private static final Logger LOGGER = Logger.getLogger(PlayerAchievements.class.getName());
	
	// SQL Queries
	private static final String SELECT_QUERY = "SELECT * FROM character_achievements WHERE charId = ?";
	private static final String INSERT_QUERY = "INSERT INTO character_achievements (charId, achievementId, completed, claimed, dateCompleted, dateClaimed) VALUES (?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE completed = VALUES(completed), claimed = VALUES(claimed), dateCompleted = VALUES(dateCompleted), dateClaimed = VALUES(dateClaimed)";
	private static final String DELETE_QUERY = "DELETE FROM character_achievements WHERE charId = ?";
	
	private final int _charId;
	private final Map<Integer, AchievementStatus> _achievements = new ConcurrentHashMap<>();
	
	public static class AchievementStatus
	{
		public boolean completed;
		public boolean claimed;
		public long dateCompleted;
		public long dateClaimed;
		
		public AchievementStatus(boolean completed, boolean claimed, long dateCompleted, long dateClaimed)
		{
			this.completed = completed;
			this.claimed = claimed;
			this.dateCompleted = dateCompleted;
			this.dateClaimed = dateClaimed;
		}
	}
	
	public PlayerAchievements(int charId)
	{
		_charId = charId;
		loadAchievements();
	}
	
	/**
	 * Load achievements from database
	 */
	private void loadAchievements()
	{
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement st = con.prepareStatement(SELECT_QUERY))
		{
			st.setInt(1, _charId);
			try (ResultSet rs = st.executeQuery())
			{
				while (rs.next())
				{
					final int achievementId = rs.getInt("achievementId");
					final boolean completed = rs.getBoolean("completed");
					final boolean claimed = rs.getBoolean("claimed");
					final long dateCompleted = rs.getTimestamp("dateCompleted") != null ? rs.getTimestamp("dateCompleted").getTime() : 0;
					final long dateClaimed = rs.getTimestamp("dateClaimed") != null ? rs.getTimestamp("dateClaimed").getTime() : 0;
					
					_achievements.put(achievementId, new AchievementStatus(completed, claimed, dateCompleted, dateClaimed));
				}
			}
		}
		catch (SQLException e)
		{
			LOGGER.log(Level.WARNING, getClass().getSimpleName() + ": Couldn't load achievements for charId: " + _charId, e);
		}
	}
	
	/**
	 * Check if achievement is completed
	 * @param achievementId the achievement ID to check
	 * @return true if achievement is completed, false otherwise
	 */
	public boolean isCompleted(int achievementId)
	{
		final AchievementStatus status = _achievements.get(achievementId);
		return (status != null) && status.completed;
	}
	
	/**
	 * Check if achievement is claimed
	 * @param achievementId the achievement ID to check
	 * @return true if achievement is claimed, false otherwise
	 */
	public boolean isClaimed(int achievementId)
	{
		final AchievementStatus status = _achievements.get(achievementId);
		return (status != null) && status.claimed;
	}
	
	/**
	 * Mark achievement as completed
	 * @param achievementId the achievement ID to mark as completed
	 */
	public void setCompleted(int achievementId)
	{
		AchievementStatus status = _achievements.get(achievementId);
		if (status == null)
		{
			status = new AchievementStatus(true, false, System.currentTimeMillis(), 0);
			_achievements.put(achievementId, status);
		}
		else
		{
			status.completed = true;
			status.dateCompleted = System.currentTimeMillis();
		}
		
		saveAchievement(achievementId, status);
	}
	
	/**
	 * Mark achievement as claimed
	 * @param achievementId the achievement ID to mark as claimed
	 */
	public void setClaimed(int achievementId)
	{
		AchievementStatus status = _achievements.get(achievementId);
		if (status == null)
		{
			// Should not happen, but create entry just in case
			status = new AchievementStatus(true, true, System.currentTimeMillis(), System.currentTimeMillis());
			_achievements.put(achievementId, status);
		}
		else
		{
			status.claimed = true;
			status.dateClaimed = System.currentTimeMillis();
		}
		
		saveAchievement(achievementId, status);
	}
	
	/**
	 * Save single achievement to database
	 * @param achievementId the achievement ID to save
	 * @param status the achievement status to save
	 */
	private void saveAchievement(int achievementId, AchievementStatus status)
	{
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement st = con.prepareStatement(INSERT_QUERY))
		{
			st.setInt(1, _charId);
			st.setInt(2, achievementId);
			st.setBoolean(3, status.completed);
			st.setBoolean(4, status.claimed);
			st.setTimestamp(5, status.dateCompleted > 0 ? new Timestamp(status.dateCompleted) : null);
			st.setTimestamp(6, status.dateClaimed > 0 ? new Timestamp(status.dateClaimed) : null);
			st.executeUpdate();
		}
		catch (SQLException e)
		{
			LOGGER.log(Level.WARNING, getClass().getSimpleName() + ": Couldn't save achievement " + achievementId + " for charId: " + _charId, e);
		}
	}
	
	/**
	 * Get achievement status for display purposes
	 * @param achievementId the achievement ID
	 * @return "IN PROGRESS", "COMPLETED", or "CLAIMED"
	 */
	public String getDisplayStatus(int achievementId)
	{
		final AchievementStatus status = _achievements.get(achievementId);
		if (status == null)
		{
			return "IN PROGRESS";
		}
		
		if (status.claimed)
		{
			return "CLAIMED";
		}
		
		if (status.completed)
		{
			return "COMPLETED";
		}
		
		return "IN PROGRESS";
	}
	
	/**
	 * Check if player can claim the achievement (completed but not claimed)
	 * @param achievementId the achievement ID to check
	 * @return true if player can claim the achievement, false otherwise
	 */
	public boolean canClaim(int achievementId)
	{
		final AchievementStatus status = _achievements.get(achievementId);
		return (status != null) && status.completed && !status.claimed;
	}
	
	/**
	 * Delete all achievements for this character
	 */
	public void deleteAll()
	{
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement st = con.prepareStatement(DELETE_QUERY))
		{
			st.setInt(1, _charId);
			st.executeUpdate();
			_achievements.clear();
		}
		catch (SQLException e)
		{
			LOGGER.log(Level.WARNING, getClass().getSimpleName() + ": Couldn't delete achievements for charId: " + _charId, e);
		}
	}
}
