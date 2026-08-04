package com.l2journey.gameserver.managers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.l2journey.commons.database.DatabaseFactory;
import com.l2journey.commons.threads.ThreadPool;
import com.l2journey.commons.util.Rnd;
import com.l2journey.gameserver.ai.Intention;
import com.l2journey.gameserver.data.xml.PlayerTemplateData;
import com.l2journey.gameserver.data.xml.impl.FakePlayerEquipmentData;
import com.l2journey.gameserver.model.World;
import com.l2journey.gameserver.model.WorldObject;
import com.l2journey.gameserver.model.actor.Attackable;
import com.l2journey.gameserver.model.actor.Creature;
import com.l2journey.gameserver.model.actor.Player;
import com.l2journey.gameserver.model.actor.appearance.PlayerAppearance;
import com.l2journey.gameserver.model.actor.instance.FakePlayer;
import com.l2journey.gameserver.model.actor.templates.PlayerTemplate;
import com.l2journey.gameserver.model.groups.Party;
import com.l2journey.gameserver.model.groups.PartyDistributionType;
import com.l2journey.gameserver.model.item.enums.ItemProcessType;
import com.l2journey.gameserver.model.item.instance.Item;
import com.l2journey.gameserver.network.enums.ChatType;
import com.l2journey.gameserver.network.serverpackets.CreatureSay;

/**
 * Autonomous AI Co-op Companion Manager (LLM Companion PoC & Tactical Commands).
 * Controls a 24/7 persistent AI partner ("PaladinBot") with a 3-state machine:
 * - ACTIVE_COOP: Follows and assists human player in Party when requested.
 * - ASSIGNED_MISSION: Carries out user-assigned tasks when human is offline.
 * - AUTONOMOUS_SOLO: Autonomous PvE level progression (1 to 40), class transfers (Knight -> Paladin), and vendor loot management.
 */
public class LLMCompanionManager
{
	private static final Logger LOGGER = Logger.getLogger(LLMCompanionManager.class.getName());
	
	public enum CompanionState
	{
		ACTIVE_COOP,
		ASSIGNED_MISSION,
		AUTONOMOUS_SOLO
	}

	public static class CompanionMember
	{
		private final String _name;
		private final String _role; // TANKER, ARCHER, HEALER
		private final int _baseClassId;
		private final int _firstClassId;
		private final int _secondClassId;
		private FakePlayer _botInstance;

		public CompanionMember(String name, String role, int baseClassId, int firstClassId, int secondClassId)
		{
			_name = name;
			_role = role;
			_baseClassId = baseClassId;
			_firstClassId = firstClassId;
			_secondClassId = secondClassId;
		}

		public String getName() { return _name; }
		public String getRole() { return _role; }
		public int getBaseClassId() { return _baseClassId; }
		public int getFirstClassId() { return _firstClassId; }
		public int getSecondClassId() { return _secondClassId; }
		public FakePlayer getBotInstance() { return _botInstance; }
		public void setBotInstance(FakePlayer bot) { _botInstance = bot; }
	}

	private static final String COMPANION_NAME = "PaladinBot";
	private static final String TARGET_HUMAN_NAME = "Tiago";

	private final List<CompanionMember> _trio = List.of(
		new CompanionMember("PaladinBot", "TANKER", 0, 9, 90),
		new CompanionMember("HawkeyeBot", "ARCHER", 0, 7, 92),
		new CompanionMember("BishopBot", "HEALER", 10, 15, 97)
	);

	private CompanionState _state = CompanionState.AUTONOMOUS_SOLO;
	private String _currentMission = "Farm & Solo Leveling (Nv 1-40)";

	protected LLMCompanionManager()
	{
		initDatabaseTable();
		ThreadPool.scheduleAtFixedRate(this::onTick, 5000, 3000);
		LOGGER.info("LLMCompanionManager: Initialized Trio AI Companion Engine (PaladinBot, HawkeyeBot, BishopBot).");
	}

	public static LLMCompanionManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}

	private static class SingletonHolder
	{
		protected static final LLMCompanionManager INSTANCE = new LLMCompanionManager();
	}

	private void initDatabaseTable()
	{
		try (Connection con = DatabaseFactory.getConnection();
		     PreparedStatement ps = con.prepareStatement(
				"CREATE TABLE IF NOT EXISTS `companion_active_missions` (" +
				"  `char_name` VARCHAR(45) NOT NULL," +
				"  `state` VARCHAR(32) NOT NULL DEFAULT 'AUTONOMOUS_SOLO'," +
				"  `current_mission` VARCHAR(255) DEFAULT ''," +
				"  `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP," +
				"  PRIMARY KEY (`char_name`)" +
				") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;"))
		{
			ps.executeUpdate();
			loadStateFromDatabase();
		}
		catch (Exception e)
		{
			LOGGER.log(Level.WARNING, "LLMCompanionManager: Failed to initialize database table", e);
		}
	}

	private void loadStateFromDatabase()
	{
		try (Connection con = DatabaseFactory.getConnection();
		     PreparedStatement ps = con.prepareStatement("SELECT `state`, `current_mission` FROM `companion_active_missions` WHERE `char_name` = ?"))
		{
			ps.setString(1, COMPANION_NAME);
			try (ResultSet rs = ps.executeQuery())
			{
				if (rs.next())
				{
					String stateStr = rs.getString("state");
					try { _state = CompanionState.valueOf(stateStr); } catch (Exception ignored) {}
					String mission = rs.getString("current_mission");
					if (mission != null && !mission.isEmpty()) _currentMission = mission;
				}
			}
		}
		catch (Exception e)
		{
			LOGGER.log(Level.WARNING, "LLMCompanionManager: Failed to load state from DB", e);
		}
	}

	public void saveStateToDatabase()
	{
		try (Connection con = DatabaseFactory.getConnection();
		     PreparedStatement ps = con.prepareStatement(
				"REPLACE INTO `companion_active_missions` (`char_name`, `state`, `current_mission`) VALUES (?, ?, ?)"))
		{
			ps.setString(1, COMPANION_NAME);
			ps.setString(2, _state.name());
			ps.setString(3, _currentMission);
			ps.executeUpdate();
		}
		catch (Exception e)
		{
			LOGGER.log(Level.WARNING, "LLMCompanionManager: Failed to save state to DB", e);
		}
	}

	private void ensureBotsSpawned()
	{
		for (CompanionMember member : _trio)
		{
			if (member.getBotInstance() != null && member.getBotInstance().isOnline()) continue;

			Player p = World.getInstance().getPlayer(member.getName());
			if (p instanceof FakePlayer)
			{
				member.setBotInstance((FakePlayer) p);
				continue;
			}

			try
			{
				PlayerTemplate template = PlayerTemplateData.getInstance().getTemplate(member.getBaseClassId());
				PlayerAppearance app = new PlayerAppearance((byte) (member.getBaseClassId() == 10 ? 1 : 0), (byte) 0, (byte) 0, false);
				FakePlayer bot = new FakePlayer(IdManager.getInstance().getNextId(), template, "LLMCompanion", app);
				bot.setName(member.getName());
				bot.setTitle(member.getRole());
				bot.setHeading(Rnd.get(65536));
				bot.spawnMe(FakeHunterManager.GLUDIO_GK_X + Rnd.get(-50, 50), FakeHunterManager.GLUDIO_GK_Y + Rnd.get(-50, 50), FakeHunterManager.GLUDIO_GK_Z);
				bot.setOnlineStatus(true, true);

				FakePlayerEquipmentData.autoEquip(bot);
				bot.broadcastUserInfo();
				member.setBotInstance(bot);
				LOGGER.info("LLMCompanionManager: Spawned persistent companion " + member.getName() + " (" + member.getRole() + ") at Gludio.");
			}
			catch (Exception e)
			{
				LOGGER.log(Level.SEVERE, "LLMCompanionManager: Failed to spawn companion " + member.getName(), e);
			}
		}
	}

	private void onTick()
	{
		ensureBotsSpawned();

		Player humanLeader = World.getInstance().getPlayer(TARGET_HUMAN_NAME);
		boolean isHumanOnline = (humanLeader != null && humanLeader.isOnline());

		if (!isHumanOnline && _state == CompanionState.ACTIVE_COOP)
		{
			if (_currentMission != null && !_currentMission.startsWith("Farm & Solo"))
			{
				_state = CompanionState.ASSIGNED_MISSION;
			}
			else
			{
				_state = CompanionState.AUTONOMOUS_SOLO;
			}
			saveStateToDatabase();
		}

		checkClassTransfers();
		checkVendorLootSelling();

		switch (_state)
		{
			case ACTIVE_COOP:
				executeActiveCoop(humanLeader);
				break;

			case ASSIGNED_MISSION:
				executeAssignedMission();
				break;

			case AUTONOMOUS_SOLO:
			default:
				executeAutonomousSolo();
				break;
		}
	}

	private void checkClassTransfers()
	{
		for (CompanionMember member : _trio)
		{
			FakePlayer bot = member.getBotInstance();
			if (bot == null || !bot.isOnline()) continue;

			int level = bot.getLevel();
			int classId = bot.getActiveClass();

			// Level 20: 1st Class Transfer
			if (level >= 20 && classId == member.getBaseClassId())
			{
				bot.setClassTemplate(member.getFirstClassId());
				FakePlayerEquipmentData.autoEquip(bot, FakePlayerEquipmentData.Grade.D_GRADE);
				bot.broadcastPacket(new CreatureSay(bot, ChatType.GENERAL, bot.getName(), "[Companion] Completei a 1ª Mudança de Classe! (Nv. 20)."));
				LOGGER.info("LLMCompanionManager: " + member.getName() + " completed 1st Class Transfer.");
			}
			// Level 40: 2nd Class Transfer
			else if (level >= 40 && classId == member.getFirstClassId())
			{
				bot.setClassTemplate(member.getSecondClassId());
				FakePlayerEquipmentData.autoEquip(bot, FakePlayerEquipmentData.Grade.C_GRADE);
				bot.broadcastPacket(new CreatureSay(bot, ChatType.GENERAL, bot.getName(), "[Companion] Completei a 2ª Mudança de Classe! (Nv. 40)."));
				LOGGER.info("LLMCompanionManager: " + member.getName() + " completed 2nd Class Transfer.");
			}
		}
	}

	private void checkVendorLootSelling()
	{
		for (CompanionMember member : _trio)
		{
			FakePlayer bot = member.getBotInstance();
			if (bot == null || !bot.isOnline()) continue;

			List<Item> items = new ArrayList<>(bot.getInventory().getItems());
			if (items.size() < 30) continue;

			long earnedAdena = 0;
			for (Item item : items)
			{
				if (item.isEquipped() || item.getId() == 57) continue;
				long count = item.getCount();
				long price = (long) (item.getReferencePrice() * 0.5 * count);
				if (price <= 0) price = 10 * count;

				earnedAdena += price;
				bot.getInventory().destroyItem(ItemProcessType.SELL, item, count, bot, null);
			}

			if (earnedAdena > 0)
			{
				bot.getInventory().addAdena(ItemProcessType.SELL, earnedAdena, bot, null);
			}
		}
	}

	private void executeActiveCoop(Player humanLeader)
	{
		if (humanLeader == null || !humanLeader.isOnline()) return;

		for (CompanionMember member : _trio)
		{
			FakePlayer bot = member.getBotInstance();
			if (bot == null || !bot.isOnline() || bot.isDead()) continue;

			// Add to party if needed
			if (!bot.isInParty())
			{
				if (humanLeader.isInParty())
				{
					humanLeader.getParty().addPartyMember(bot);
				}
				else
				{
					Party party = new Party(humanLeader, PartyDistributionType.FINDERS_KEEPERS);
					humanLeader.setParty(party);
					party.addPartyMember(bot);
				}
			}

			// Follow leader
			if (!bot.isInsideRadius2D(humanLeader, 3000))
			{
				bot.teleToLocation(humanLeader.getLocation());
				bot.broadcastUserInfo();
			}
			else if (!bot.isInsideRadius2D(humanLeader, 150))
			{
				bot.getAI().setIntention(Intention.MOVE_TO, humanLeader.getLocation());
			}

			// Role-Specific Tactical Behavior
			switch (member.getRole())
			{
				case "TANKER":
					// Aggro/Taunt mobs attacking leader
					World.getInstance().forEachVisibleObjectInRange(bot, Attackable.class, 600, mob -> {
						if ((mob.getTarget() == humanLeader || mob.getTarget() == null) && !mob.isDead())
						{
							bot.setTarget(mob);
							bot.getAI().setIntention(Intention.ATTACK, mob);
						}
					});
					break;

				case "ARCHER":
					// Ranged assist on leader's target
					WorldObject leaderTarget = humanLeader.getTarget();
					if (leaderTarget instanceof Attackable && !((Attackable) leaderTarget).isDead())
					{
						bot.setTarget(leaderTarget);
						bot.getAI().setIntention(Intention.ATTACK, (Creature) leaderTarget);
					}
					break;

				case "HEALER":
					// Emergency Heal party members under 70% HP
					Party party = humanLeader.getParty();
					if (party != null)
					{
						for (Player pMember : party.getMembers())
						{
							if (pMember != null && !pMember.isDead())
							{
								double hpPercent = (pMember.getCurrentHp() / pMember.getMaxHp()) * 100.0;
								if (hpPercent < 70.0)
								{
									bot.setTarget(pMember);
									bot.getAI().setIntention(Intention.CAST, null);
									break;
								}
							}
						}
					}
					break;
			}
		}
	}

	private void executeAssignedMission()
	{
		executeAutonomousSolo();
	}

	private void executeAutonomousSolo()
	{
		for (CompanionMember member : _trio)
		{
			FakePlayer bot = member.getBotInstance();
			if (bot == null || !bot.isOnline() || bot.isDead() || bot.isAttackingNow() || bot.isMoving()) continue;

			World.getInstance().forEachVisibleObjectInRange(bot, Attackable.class, 800, mob -> {
				if (!mob.isDead() && bot.getTarget() == null)
				{
					bot.setTarget(mob);
					bot.getAI().setIntention(Intention.ATTACK, mob);
				}
			});
		}
	}

	public void onPlayerChat(Player sender, String chatType, String target, String text)
	{
		if (sender == null || text == null || target == null) return;
		for (CompanionMember member : _trio)
		{
			if (target.equalsIgnoreCase(member.getName()))
			{
				processWhisperCommand(member, sender, text.trim());
				break;
			}
		}
	}

	private void processWhisperCommand(CompanionMember member, Player sender, String command)
	{
		FakePlayer bot = member.getBotInstance();
		if (bot == null) return;

		String lower = command.toLowerCase();
		if (lower.contains("status"))
		{
			LLMMemoryManager.RelationshipEntry rel = LLMMemoryManager.getInstance().getRelationship(bot.getObjectId(), sender.getObjectId());
			String response = String.format("[%s] Estado: %s | Nv. %d (%s) | Relacionamento com %s: %s (%d/100) | HP: %d/%d | MP: %d/%d | Missão: %s",
				member.getName(), _state.name(), bot.getLevel(), bot.getTemplate().getPlayerClass().toString(),
				sender.getName(), rel.getStatus().name(), rel.getAffinityScore(),
				(long)bot.getCurrentHp(), (long)bot.getMaxHp(),
				(long)bot.getCurrentMp(), (long)bot.getMaxMp(),
				_currentMission);
			sendWhisper(bot, sender, response);
			return;
		}

		if (lower.contains("party") || lower.contains("grupo"))
		{
			LLMMemoryManager.RelationshipEntry rel = LLMMemoryManager.getInstance().getRelationship(bot.getObjectId(), sender.getObjectId());
			if (rel.getAffinityScore() <= -30)
			{
				sendWhisper(bot, sender, "Nem ferrando! Não entro em party com você por conta do nosso histórico (" + rel.getStatus().name() + ").");
				return;
			}
			_state = CompanionState.ACTIVE_COOP;
			saveStateToDatabase();
			executeActiveCoop(sender);
		}
		else if (lower.contains("town") || lower.contains("cidade") || lower.contains("vila"))
		{
			for (CompanionMember m : _trio)
			{
				if (m.getBotInstance() != null)
				{
					m.getBotInstance().teleToLocation(FakeHunterManager.GLUDIO_GK_X, FakeHunterManager.GLUDIO_GK_Y, FakeHunterManager.GLUDIO_GK_Z);
					m.getBotInstance().broadcastUserInfo();
				}
			}
			_state = CompanionState.AUTONOMOUS_SOLO;
			saveStateToDatabase();
		}

		String prompt = buildGamerPrompt(member, sender, command);
		LLMClient.getInstance().generateAsync(prompt, llmResponse -> {
			if (llmResponse != null && !llmResponse.trim().isEmpty())
			{
				sendWhisper(bot, sender, llmResponse.trim());
			}
			else
			{
				sendFallbackWhisper(bot, sender, member.getRole(), lower);
			}
		});
	}

	private String buildGamerPrompt(CompanionMember member, Player sender, String userMessage)
	{
		StringBuilder sb = new StringBuilder();
		sb.append("Roleplay System Prompt:\n");
		sb.append("Você é ").append(member.getName()).append(", jogador ").append(member.getRole()).append(" de Lineage 2.\n");
		sb.append("Responda em Português BR de forma informal, com gírias de gamer de L2 (ss, mob, cata, pt, tancar, farm, bora).\n");
		sb.append("REGRA: Responda em no máximo 1 frase curta (máximo 15 palavras).\n\n");

		sb.append("Status de ").append(member.getName()).append(":\n");
		FakePlayer bot = member.getBotInstance();
		if (bot != null)
		{
			sb.append("Nível: ").append(bot.getLevel()).append(" | HP: ").append((int)bot.getCurrentHp()).append("/").append((int)bot.getMaxHp()).append("\n");
		}
		if (sender != null)
		{
			sb.append("Parceiro: ").append(sender.getName()).append("\n");
			if (bot != null)
			{
				String memContext = LLMMemoryManager.getInstance().getFormattedMemoryContext(bot.getObjectId(), sender.getObjectId(), sender.getName());
				sb.append("\n").append(memContext).append("\n");
			}
		}

		sb.append("\nMensagem de ").append(sender != null ? sender.getName() : "Player").append(": ").append(userMessage).append("\n");
		sb.append("Resposta de ").append(member.getName()).append(":");
		return sb.toString();
	}

	private void sendFallbackWhisper(FakePlayer bot, Player sender, String role, String lowerCommand)
	{
		if (lowerCommand.contains("party") || lowerCommand.contains("grupo"))
		{
			sendWhisper(bot, sender, "Bora pra party! Entrando no grupo e pronto pra agir (" + role + ").");
		}
		else if (lowerCommand.contains("town") || lowerCommand.contains("cidade"))
		{
			sendWhisper(bot, sender, "Retornando para a vila de Gludio!");
		}
		else
		{
			sendWhisper(bot, sender, "Tô pronto! Função (" + role + ") pronta pra ação.");
		}
	}

	private void sendWhisper(FakePlayer bot, Player recipient, String text)
	{
		if (bot == null || recipient == null || !recipient.isOnline()) return;
		recipient.sendPacket(new CreatureSay(bot, ChatType.WHISPER, bot.getName(), text));
	}

	public CompanionState getState()
	{
		return _state;
	}

	public List<CompanionMember> getTrio()
	{
		return _trio;
	}
}
