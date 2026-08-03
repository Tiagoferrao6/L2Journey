package com.l2journey.gameserver.managers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.l2journey.Config;
import com.l2journey.commons.database.DatabaseFactory;
import com.l2journey.gameserver.data.xml.PlayerTemplateData;
import com.l2journey.gameserver.data.xml.impl.FakePlayerEquipmentData;
import com.l2journey.gameserver.managers.IdManager;
import com.l2journey.gameserver.model.actor.Player;
import com.l2journey.gameserver.model.actor.appearance.PlayerAppearance;
import com.l2journey.gameserver.model.actor.instance.MercenaryInstance;
import com.l2journey.gameserver.model.actor.templates.PlayerTemplate;
import com.l2journey.gameserver.model.groups.Party;
import com.l2journey.gameserver.model.groups.PartyMessageType;
import com.l2journey.gameserver.model.item.enums.ItemProcessType;

/**
 * Single Player Mercenary Manager (Healer MVP).
 * Handles hiring on-demand, contract reload/resync (1 Adena fee), and database persistence.
 */
public class MercenaryManager
{
	private static final Logger LOGGER = Logger.getLogger(MercenaryManager.class.getName());
	private final Map<Integer, MercenaryInstance> _activeMercenaries = new ConcurrentHashMap<>();

	protected MercenaryManager()
	{
		LOGGER.info(getClass().getSimpleName() + ": Initialized (Single-Player Mercenary System).");
	}

	public MercenaryInstance getActiveMercenary(int ownerCharId)
	{
		return _activeMercenaries.get(ownerCharId);
	}

	/**
	 * Hire a new Mercenary Healer at the owner's current level.
	 */
	public boolean hireMercenary(Player owner, String mercId)
	{
		if (!Config.ENABLE_MERCENARIES)
		{
			owner.sendMessage("Mercenary System is disabled by server configuration.");
			return false;
		}

		if (owner.getAdena() < Config.MERCENARY_HIRE_FEE)
		{
			owner.sendMessage("You need " + Config.MERCENARY_HIRE_FEE + " Adena to hire a mercenary.");
			return false;
		}

		// Dismiss existing mercenary if active and clear previous DB record
		dismissMercenary(owner, true);

		// Deduct fee
		owner.reduceAdena(ItemProcessType.FEE, Config.MERCENARY_HIRE_FEE, owner, true);

		int classId = 97; // Cardinal (Healer)
		int targetLevel = owner.getLevel();
		String name = "Elenora";

		MercenaryInstance merc = spawnMercenaryInstance(owner, mercId, name, classId, targetLevel);
		if (merc != null)
		{
			_activeMercenaries.put(owner.getObjectId(), merc);
			saveMercenaryToDb(owner.getObjectId(), mercId, name, targetLevel, 0, 0, classId);
			owner.sendMessage("Mercenary Healer [" + name + "] hired at level " + targetLevel + "!");
			return true;
		}
		return false;
	}

	/**
	 * Reload/Resynchronize mercenary contract to matching player level for 1 Adena.
	 */
	public boolean reloadContract(Player owner)
	{
		if (!Config.ENABLE_MERCENARIES)
		{
			owner.sendMessage("Mercenary System is disabled.");
			return false;
		}

		if (owner.getAdena() < Config.MERCENARY_HIRE_FEE)
		{
			owner.sendMessage("You need " + Config.MERCENARY_HIRE_FEE + " Adena to reload mercenary contract.");
			return false;
		}

		MercenaryInstance activeMerc = _activeMercenaries.get(owner.getObjectId());
		String mercId = activeMerc != null ? activeMerc.getMercenaryId() : "healer_elenora";

		return hireMercenary(owner, mercId);
	}

	/**
	 * Spawns the mercenary Java instance and adds to party.
	 */
	private MercenaryInstance spawnMercenaryInstance(Player owner, String mercId, String name, int classId, int level)
	{
		try
		{
			PlayerTemplate template = PlayerTemplateData.getInstance().getTemplate(classId);
			if (template == null)
			{
				template = PlayerTemplateData.getInstance().getTemplate(0); // Fallback Fighter
			}

			PlayerAppearance app = new PlayerAppearance((byte) 1, (byte) 0, (byte) 0, true);
			int objectId = IdManager.getInstance().getNextId();

			MercenaryInstance merc = new MercenaryInstance(objectId, template, "Mercenaries", app, owner.getObjectId(), mercId);
			merc.setName(name);
			merc.setTitle("Mercenary Healer");
			merc.getStat().setLevel((byte) level);
			merc.setCurrentHpMp(merc.getMaxHp(), merc.getMaxMp());
			merc.setRunning();

			// Auto Equip Top Grade Gear for Level
			FakePlayerEquipmentData.autoEquip(merc);

			// Spawn next to owner
			merc.spawnMe(owner.getX() + 40, owner.getY() + 40, owner.getZ());

			// Add to Party
			Party party = owner.getParty();
			if (party == null)
			{
				party = new Party(owner, owner.getPartyDistributionType());
				owner.setParty(party);
			}
			party.addPartyMember(merc);
			owner.sendPacket(new com.l2journey.gameserver.network.serverpackets.PartySmallWindowAll(owner, party));

			return merc;
		}
		catch (Exception e)
		{
			LOGGER.log(Level.WARNING, "MercenaryManager: Failed to spawn mercenary!", e);
			return null;
		}
	}

	/**
	 * Dismiss or despawn active mercenary.
	 */
	public void dismissMercenary(Player owner, boolean removeFromDb)
	{
		MercenaryInstance merc = _activeMercenaries.remove(owner.getObjectId());
		if (merc != null)
		{
			if (merc.getParty() != null)
			{
				merc.getParty().removePartyMember(merc, PartyMessageType.LEFT);
			}
			merc.deleteMe();
		}

		if (removeFromDb)
		{
			deleteMercenaryFromDb(owner.getObjectId());
		}
	}

	/**
	 * Load mercenary on player login.
	 */
	public void loadMercenaryOnLogin(Player owner)
	{
		if (!Config.ENABLE_MERCENARIES)
		{
			return;
		}

		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement ps = con.prepareStatement("SELECT mercenary_id, name, level, exp, sp, class_id FROM character_mercenaries WHERE char_id = ?"))
		{
			ps.setInt(1, owner.getObjectId());
			try (ResultSet rs = ps.executeQuery())
			{
				if (rs.next())
				{
					String mercId = rs.getString("mercenary_id");
					String name = rs.getString("name");
					int level = rs.getInt("level");
					int classId = rs.getInt("class_id");

					MercenaryInstance merc = spawnMercenaryInstance(owner, mercId, name, classId, level);
					if (merc != null)
					{
						_activeMercenaries.put(owner.getObjectId(), merc);
						LOGGER.info("MercenaryManager: Loaded mercenary [" + name + "] for player [" + owner.getName() + "].");
					}
				}
			}
		}
		catch (Exception e)
		{
			LOGGER.log(Level.WARNING, "MercenaryManager: Error loading mercenary on login!", e);
		}
	}

	private void saveMercenaryToDb(int charId, String mercId, String name, int level, long exp, int sp, int classId)
	{
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement ps = con.prepareStatement("INSERT INTO character_mercenaries (char_id, mercenary_id, name, level, exp, sp, class_id) VALUES (?, ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE level=?, exp=?, sp=?"))
		{
			ps.setInt(1, charId);
			ps.setString(2, mercId);
			ps.setString(3, name);
			ps.setInt(4, level);
			ps.setLong(5, exp);
			ps.setInt(6, sp);
			ps.setInt(7, classId);

			ps.setInt(8, level);
			ps.setLong(9, exp);
			ps.setInt(10, sp);

			ps.executeUpdate();
		}
		catch (Exception e)
		{
			LOGGER.log(Level.WARNING, "MercenaryManager: Error saving mercenary to database!", e);
		}
	}

	private void deleteMercenaryFromDb(int charId)
	{
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement ps = con.prepareStatement("DELETE FROM character_mercenaries WHERE char_id = ?"))
		{
			ps.setInt(1, charId);
			ps.executeUpdate();
		}
		catch (Exception e)
		{
			LOGGER.log(Level.WARNING, "MercenaryManager: Error deleting mercenary from database!", e);
		}
	}

	public static MercenaryManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}

	private static class SingletonHolder
	{
		protected static final MercenaryManager INSTANCE = new MercenaryManager();
	}
}
