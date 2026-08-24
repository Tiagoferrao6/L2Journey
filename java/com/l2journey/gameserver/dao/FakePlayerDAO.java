package com.l2journey.gameserver.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.l2journey.commons.database.DatabaseFactory;
import com.l2journey.gameserver.model.actor.fakeplayer.FakePlayerProfile;

/**
 * Data Access Object (DAO) for fake_players_profiles database table.
 */
public class FakePlayerDAO
{
	private static final Logger LOGGER = Logger.getLogger(FakePlayerDAO.class.getName());

	private static final String SELECT_ALL = "SELECT * FROM fake_players_profiles";
	private static final String SELECT_BY_ZONE = "SELECT * FROM fake_players_profiles WHERE zone_id=?";
	private static final String SELECT_BY_ID = "SELECT * FROM fake_players_profiles WHERE fake_id=?";
	private static final String INSERT_PROFILE = "INSERT INTO fake_players_profiles (bot_type, class_id, dual_class_id, agressividade, coragem, party_tendency, turno, zone_id, x, y, z, heading, inventory, party_id, is_active, last_active_time) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	private static final String UPDATE_PROFILE = "UPDATE fake_players_profiles SET bot_type=?, class_id=?, dual_class_id=?, agressividade=?, coragem=?, party_tendency=?, turno=?, zone_id=?, x=?, y=?, z=?, heading=?, inventory=?, party_id=?, is_active=?, last_active_time=? WHERE fake_id=?";
	private static final String DELETE_PROFILE = "DELETE FROM fake_players_profiles WHERE fake_id=?";

	protected FakePlayerDAO()
	{
	}

	public List<FakePlayerProfile> loadAllProfiles()
	{
		final List<FakePlayerProfile> profiles = new ArrayList<>();
		try (Connection con = DatabaseFactory.getConnection();
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(SELECT_ALL))
		{
			while (rs.next())
			{
				profiles.add(parseProfile(rs));
			}
		}
		catch (Exception e)
		{
			LOGGER.log(Level.WARNING, "FakePlayerDAO: Error loading all profiles: " + e.getMessage(), e);
		}
		return profiles;
	}

	public List<FakePlayerProfile> loadProfilesByZone(String zoneId)
	{
		final List<FakePlayerProfile> profiles = new ArrayList<>();
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement stmt = con.prepareStatement(SELECT_BY_ZONE))
		{
			stmt.setString(1, zoneId);
			try (ResultSet rs = stmt.executeQuery())
			{
				while (rs.next())
				{
					profiles.add(parseProfile(rs));
				}
			}
		}
		catch (Exception e)
		{
			LOGGER.log(Level.WARNING, "FakePlayerDAO: Error loading profiles for zone " + zoneId + ": " + e.getMessage(), e);
		}
		return profiles;
	}

	public FakePlayerProfile loadProfile(int fakeId)
	{
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement stmt = con.prepareStatement(SELECT_BY_ID))
		{
			stmt.setInt(1, fakeId);
			try (ResultSet rs = stmt.executeQuery())
			{
				if (rs.next())
				{
					return parseProfile(rs);
				}
			}
		}
		catch (Exception e)
		{
			LOGGER.log(Level.WARNING, "FakePlayerDAO: Error loading profile for ID " + fakeId + ": " + e.getMessage(), e);
		}
		return null;
	}

	public boolean saveProfile(FakePlayerProfile profile)
	{
		if (profile == null)
		{
			return false;
		}

		if (profile.getFakeId() <= 0)
		{
			return insertProfile(profile);
		}

		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement stmt = con.prepareStatement(UPDATE_PROFILE))
		{
			stmt.setString(1, profile.getBotType());
			stmt.setInt(2, profile.getClassId());
			stmt.setInt(3, profile.getDualClassId());
			stmt.setInt(4, profile.getAggressiveness());
			stmt.setInt(5, profile.getCourage());
			stmt.setInt(6, profile.getPartyTendency());
			stmt.setString(7, profile.getShift());
			stmt.setString(8, profile.getZoneId());
			stmt.setInt(9, profile.getX());
			stmt.setInt(10, profile.getY());
			stmt.setInt(11, profile.getZ());
			stmt.setInt(12, profile.getHeading());
			stmt.setString(13, profile.getInventory());
			stmt.setInt(14, profile.getPartyId());
			stmt.setInt(15, profile.isActive() ? 1 : 0);
			stmt.setLong(16, profile.getLastActiveTime());
			stmt.setInt(17, profile.getFakeId());

			return stmt.executeUpdate() > 0;
		}
		catch (Exception e)
		{
			LOGGER.log(Level.WARNING, "FakePlayerDAO: Error updating profile for ID " + profile.getFakeId() + ": " + e.getMessage(), e);
			return false;
		}
	}

	public boolean insertProfile(FakePlayerProfile profile)
	{
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement stmt = con.prepareStatement(INSERT_PROFILE, Statement.RETURN_GENERATED_KEYS))
		{
			stmt.setString(1, profile.getBotType());
			stmt.setInt(2, profile.getClassId());
			stmt.setInt(3, profile.getDualClassId());
			stmt.setInt(4, profile.getAggressiveness());
			stmt.setInt(5, profile.getCourage());
			stmt.setInt(6, profile.getPartyTendency());
			stmt.setString(7, profile.getShift());
			stmt.setString(8, profile.getZoneId());
			stmt.setInt(9, profile.getX());
			stmt.setInt(10, profile.getY());
			stmt.setInt(11, profile.getZ());
			stmt.setInt(12, profile.getHeading());
			stmt.setString(13, profile.getInventory());
			stmt.setInt(14, profile.getPartyId());
			stmt.setInt(15, profile.isActive() ? 1 : 0);
			stmt.setLong(16, profile.getLastActiveTime());

			int affected = stmt.executeUpdate();
			if (affected > 0)
			{
				try (ResultSet keys = stmt.getGeneratedKeys())
				{
					if (keys.next())
					{
						profile.setFakeId(keys.getInt(1));
					}
				}
				return true;
			}
		}
		catch (Exception e)
		{
			LOGGER.log(Level.WARNING, "FakePlayerDAO: Error inserting profile: " + e.getMessage(), e);
		}
		return false;
	}

	public boolean deleteProfile(int fakeId)
	{
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement stmt = con.prepareStatement(DELETE_PROFILE))
		{
			stmt.setInt(1, fakeId);
			return stmt.executeUpdate() > 0;
		}
		catch (Exception e)
		{
			LOGGER.log(Level.WARNING, "FakePlayerDAO: Error deleting profile " + fakeId + ": " + e.getMessage(), e);
			return false;
		}
	}

	private FakePlayerProfile parseProfile(ResultSet rs) throws Exception
	{
		final FakePlayerProfile profile = new FakePlayerProfile();
		profile.setFakeId(rs.getInt("fake_id"));
		profile.setBotType(rs.getString("bot_type"));
		profile.setClassId(rs.getInt("class_id"));
		profile.setDualClassId(rs.getInt("dual_class_id"));
		profile.setAggressiveness(rs.getInt("agressividade"));
		profile.setCourage(rs.getInt("coragem"));
		profile.setPartyTendency(rs.getInt("party_tendency"));
		profile.setShift(rs.getString("turno"));
		profile.setZoneId(rs.getString("zone_id"));
		profile.setX(rs.getInt("x"));
		profile.setY(rs.getInt("y"));
		profile.setZ(rs.getInt("z"));
		profile.setHeading(rs.getInt("heading"));
		profile.setInventory(rs.getString("inventory"));
		profile.setPartyId(rs.getInt("party_id"));
		profile.setActive(rs.getInt("is_active") == 1);
		profile.setLastActiveTime(rs.getLong("last_active_time"));
		return profile;
	}

	public static FakePlayerDAO getInstance()
	{
		return SingletonHolder.INSTANCE;
	}

	private static class SingletonHolder
	{
		protected static final FakePlayerDAO INSTANCE = new FakePlayerDAO();
	}
}
