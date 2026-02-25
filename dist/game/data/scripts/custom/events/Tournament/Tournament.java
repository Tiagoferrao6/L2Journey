/*
 * Tournament PvP Event
 * Supports 1x1, 2x2, 3x3, 4x4, 5x5 and 9x9 fight modes.
 * Multiple concurrent fights in instanced arenas.
 * SQL-based ranking system.
 * Zero core modifications.
 */
package custom.events.Tournament;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.l2journey.Config;
import com.l2journey.commons.database.DatabaseFactory;
import com.l2journey.commons.time.SchedulingPattern;
import com.l2journey.commons.time.TimeUtil;
import com.l2journey.commons.util.IXmlReader;
import com.l2journey.gameserver.managers.AntiFeedManager;
import com.l2journey.gameserver.managers.InstanceManager;
import com.l2journey.gameserver.model.Location;
import com.l2journey.gameserver.model.StatSet;
import com.l2journey.gameserver.model.actor.Npc;
import com.l2journey.gameserver.model.actor.Player;
import com.l2journey.gameserver.model.actor.Summon;
import com.l2journey.gameserver.model.actor.enums.creature.Team;
import com.l2journey.gameserver.model.actor.instance.Door;
import com.l2journey.gameserver.model.events.EventType;
import com.l2journey.gameserver.model.events.annotations.RegisterEvent;
import com.l2journey.gameserver.model.events.holders.actor.creature.OnCreatureDeath;
import com.l2journey.gameserver.model.events.holders.actor.player.OnPlayerLogout;
import com.l2journey.gameserver.model.events.listeners.AbstractEventListener;
import com.l2journey.gameserver.model.events.listeners.ConsumerEventListener;
import com.l2journey.gameserver.model.groups.Party;
import com.l2journey.gameserver.model.groups.PartyDistributionType;
import com.l2journey.gameserver.model.instancezone.InstanceWorld;
import com.l2journey.gameserver.model.item.holders.ItemHolder;
import com.l2journey.gameserver.model.olympiad.OlympiadManager;
import com.l2journey.gameserver.model.quest.Event;
import com.l2journey.gameserver.model.quest.QuestTimer;
import com.l2journey.gameserver.model.skill.CommonSkill;
import com.l2journey.gameserver.model.skill.Skill;
import com.l2journey.gameserver.model.zone.ZoneId;
import com.l2journey.gameserver.network.NpcStringId;
import com.l2journey.gameserver.network.serverpackets.ExSendUIEvent;
import com.l2journey.gameserver.network.serverpackets.ExShowScreenMessage;
import com.l2journey.gameserver.network.serverpackets.MagicSkillUse;
import com.l2journey.gameserver.network.serverpackets.NpcHtmlMessage;
import com.l2journey.gameserver.util.Broadcast;

/**
 * PvP Tournament Event with multiple fight modes (1x1 to 9x9).<br>
 * Each fight runs in its own instanced Coliseum arena.<br>
 * Players register via NPC, get matched automatically, and fight for rewards and ranking.
 * @author L2Journey
 */
public class Tournament extends Event
{
	// @formatter:off
	enum EventState { INACTIVE, REGISTERING, FIGHTING }
	enum FightState { PREPARING, FIGHTING, ENDED }
	// @formatter:on
	
	/** Represents a team (or solo player) waiting in the matchmaking queue. */
	static class QueueEntry
	{
		final Player leader;
		final List<Player> members;
		final int fightType;
		final long registerTime;
		
		QueueEntry(Player leader, List<Player> members, int fightType)
		{
			this.leader = leader;
			this.members = new ArrayList<>(members);
			this.fightType = fightType;
			this.registerTime = System.currentTimeMillis();
		}
	}
	
	/** Represents an active fight between two teams. */
	static class Fight
	{
		final int id;
		final int fightType;
		final List<Player> teamBlue;
		final List<Player> teamRed;
		final Map<Player, Integer> kills = new ConcurrentHashMap<>();
		InstanceWorld world;
		volatile FightState state = FightState.PREPARING;
		
		Fight(int id, int fightType, List<Player> teamBlue, List<Player> teamRed)
		{
			this.id = id;
			this.fightType = fightType;
			this.teamBlue = new CopyOnWriteArrayList<>(teamBlue);
			this.teamRed = new CopyOnWriteArrayList<>(teamRed);
			for (Player p : teamBlue)
			{
				kills.put(p, 0);
			}
			for (Player p : teamRed)
			{
				kills.put(p, 0);
			}
		}
		
		List<Player> getAllPlayers()
		{
			final List<Player> all = new ArrayList<>(teamBlue.size() + teamRed.size());
			all.addAll(teamBlue);
			all.addAll(teamRed);
			return all;
		}
	}
	
	// Path
	private static final String HTML_PATH = "data/scripts/custom/events/Tournament/";
	
	// NPC
	private static final int MANAGER = 70012;
	
	// Instance
	private static final int INSTANCE_ID = 3049;
	private static final int BLUE_DOOR_ID = 24190002;
	private static final int RED_DOOR_ID = 24190003;
	private static final Location BLUE_SPAWN_LOC = new Location(147447, 46722, -3416);
	private static final Location RED_SPAWN_LOC = new Location(151536, 46722, -3416);
	
	// Fight types
	private static final int[] ALL_FIGHT_TYPES =
	{
		1,
		2,
		3,
		4,
		5,
		9
	};
	
	// SQL
	private static final String INSERT_OR_UPDATE_RANKING = "INSERT INTO tournament_ranking (char_id, char_name, fight_type, victories, defeats, ties, kills, deaths) VALUES (?, ?, ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE char_name=VALUES(char_name), victories=victories+VALUES(victories), defeats=defeats+VALUES(defeats), ties=ties+VALUES(ties), kills=kills+VALUES(kills), deaths=deaths+VALUES(deaths)";
	private static final String SELECT_RANKING = "SELECT char_name, victories, defeats, ties, kills, deaths FROM tournament_ranking WHERE fight_type=? ORDER BY victories DESC, kills DESC LIMIT 20";
	
	// Configurable settings (loaded from config.xml)
	private static int REGISTRATION_TIME = 15; // minutes
	private static int FIGHT_PREPARE_TIME = 30; // seconds
	private static int MIN_LEVEL = 76;
	private static int MAX_LEVEL = 200;
	private static Location MANAGER_SPAWN_LOC = new Location(83425, 148585, -3406, 32938);
	private static final Map<Integer, Integer> FIGHT_TIMES = new HashMap<>();
	private static final Map<Integer, ItemHolder> FIGHT_REWARDS = new HashMap<>();
	private static final Set<Integer> ENABLED_FIGHT_TYPES = new HashSet<>();
	
	// State
	private static volatile EventState _state = EventState.INACTIVE;
	private static final Map<Integer, List<QueueEntry>> QUEUES = new ConcurrentHashMap<>();
	private static final Map<Integer, Fight> ACTIVE_FIGHTS = new ConcurrentHashMap<>();
	private static final Set<Player> REGISTERED_PLAYERS = ConcurrentHashMap.newKeySet();
	private static final AtomicInteger FIGHT_ID_COUNTER = new AtomicInteger(0);
	private static Npc MANAGER_NPC_INSTANCE = null;
	
	private Tournament()
	{
		addTalkId(MANAGER);
		addStartNpc(MANAGER);
		addFirstTalkId(MANAGER);
		
		// Initialize queues and default settings.
		for (int type : ALL_FIGHT_TYPES)
		{
			QUEUES.put(type, new CopyOnWriteArrayList<>());
			ENABLED_FIGHT_TYPES.add(type);
		}
		
		// Default fight times (seconds).
		FIGHT_TIMES.put(1, 180);
		FIGHT_TIMES.put(2, 180);
		FIGHT_TIMES.put(3, 240);
		FIGHT_TIMES.put(4, 240);
		FIGHT_TIMES.put(5, 300);
		FIGHT_TIMES.put(9, 300);
		
		// Default rewards.
		final ItemHolder defaultReward = new ItemHolder(57, 100000);
		for (int type : ALL_FIGHT_TYPES)
		{
			FIGHT_REWARDS.put(type, defaultReward);
		}
		
		loadConfig();
	}
	
	// =========================================================================
	// Config Loading
	// =========================================================================
	
	private void loadConfig()
	{
		new IXmlReader()
		{
			@Override
			public void load()
			{
				parseDatapackFile(HTML_PATH + "config.xml");
			}
			
			@Override
			public boolean isValidating()
			{
				return false;
			}
			
			@Override
			public void parseDocument(Document document, File file)
			{
				final AtomicInteger count = new AtomicInteger(0);
				forEach(document, "event", eventNode ->
				{
					final StatSet att = new StatSet(parseAttributes(eventNode));
					final String name = att.getString("name", "Tournament");
					
					for (Node node = eventNode.getFirstChild(); node != null; node = node.getNextSibling())
					{
						switch (node.getNodeName())
						{
							case "schedule":
							{
								final StatSet attributes = new StatSet(parseAttributes(node));
								final String pattern = attributes.getString("pattern");
								final SchedulingPattern schedulingPattern = new SchedulingPattern(pattern);
								final StatSet params = new StatSet();
								params.set("Name", name);
								params.set("SchedulingPattern", pattern);
								final long delay = schedulingPattern.getDelayToNextFromNow();
								getTimers().addTimer("Schedule" + count.incrementAndGet(), params, delay + 5000, null, null);
								LOGGER.info("Tournament Event scheduled at " + TimeUtil.getDateTimeString(System.currentTimeMillis() + delay));
								break;
							}
							case "settings":
							{
								final StatSet a = new StatSet(parseAttributes(node));
								REGISTRATION_TIME = a.getInt("registrationTime", REGISTRATION_TIME);
								FIGHT_PREPARE_TIME = a.getInt("prepareTime", FIGHT_PREPARE_TIME);
								MIN_LEVEL = a.getInt("minLevel", MIN_LEVEL);
								MAX_LEVEL = a.getInt("maxLevel", MAX_LEVEL);
								break;
							}
							case "managerLocation":
							{
								final StatSet a = new StatSet(parseAttributes(node));
								MANAGER_SPAWN_LOC = new Location(a.getInt("x"), a.getInt("y"), a.getInt("z"), a.getInt("heading", 0));
								break;
							}
							case "fights":
							{
								ENABLED_FIGHT_TYPES.clear();
								for (Node fightNode = node.getFirstChild(); fightNode != null; fightNode = fightNode.getNextSibling())
								{
									if ("fight".equals(fightNode.getNodeName()))
									{
										final StatSet fa = new StatSet(parseAttributes(fightNode));
										final int type = fa.getInt("type");
										FIGHT_TIMES.put(type, fa.getInt("fightTime", 180));
										
										final String rewardStr = fa.getString("reward", "57,100000");
										final String[] rp = rewardStr.split(",");
										FIGHT_REWARDS.put(type, new ItemHolder(Integer.parseInt(rp[0].trim()), Long.parseLong(rp[1].trim())));
										
										if (fa.getBoolean("enabled", true))
										{
											ENABLED_FIGHT_TYPES.add(type);
										}
									}
								}
								break;
							}
						}
					}
				});
			}
		}.load();
	}
	
	// =========================================================================
	// Scheduling
	// =========================================================================
	
	@Override
	public void onTimerEvent(String event, StatSet params, Npc npc, Player player)
	{
		if (event.startsWith("Schedule"))
		{
			if (IS_INACTIVE())
			{
				eventStart(null);
			}
			final SchedulingPattern schedulingPattern = new SchedulingPattern(params.getString("SchedulingPattern"));
			final long delay = schedulingPattern.getDelayToNextFromNow();
			getTimers().addTimer(event, params, delay + 5000, null, null);
			LOGGER.info("Tournament Event scheduled at " + TimeUtil.getDateTimeString(System.currentTimeMillis() + delay));
		}
	}
	
	// =========================================================================
	// NPC Interaction
	// =========================================================================
	
	@Override
	public String onFirstTalk(Npc npc, Player player)
	{
		if (!IS_REGISTERING())
		{
			return null;
		}
		
		if (REGISTERED_PLAYERS.contains(player))
		{
			startQuestTimer("show-registered", 5, npc, player);
			return "registered.html";
		}
		
		if (isPlayerInFight(player))
		{
			return null;
		}
		
		startQuestTimer("show-main", 5, npc, player);
		return "main.html";
	}
	
	@Override
	public String onEvent(String event, Npc npc, Player player)
	{
		// Block "To Village" restart while player is in a tournament fight.
		// RequestRestartPoint calls notifyEvent("ResurrectPlayer") for players on events.
		// We intentionally do nothing here so the player stays dead until the fight ends.
		if ("ResurrectPlayer".equals(event))
		{
			if (player != null)
			{
				player.sendMessage("You cannot resurrect during a Tournament fight.");
			}
			return null;
		}
		
		// Fight timers (always process).
		if (event.startsWith("fight-"))
		{
			handleFightTimer(event);
			return null;
		}
		
		// Matchmaker timer.
		if ("Matchmaker".equals(event))
		{
			processMatchmaking();
			if (IS_REGISTERING())
			{
				startQuestTimer("Matchmaker", 15000, null, null);
			}
			return null;
		}
		
		// Event end timer.
		if ("EventEnd".equals(event))
		{
			handleEventEnd();
			return null;
		}
		
		// Event end check (waiting for active fights).
		if ("EventEndCheck".equals(event))
		{
			if (ACTIVE_FIGHTS.isEmpty())
			{
				setState(EventState.INACTIVE);
			}
			else
			{
				startQuestTimer("EventEndCheck", 10000, null, null);
			}
			return null;
		}
		
		// NPC dialog - show main page with replacements.
		if ("show-main".equals(event))
		{
			if ((npc != null) && (player != null))
			{
				final NpcHtmlMessage html = new NpcHtmlMessage(npc.getObjectId());
				html.setFile(player, HTML_PATH + "main.html");
				for (int type : ALL_FIGHT_TYPES)
				{
					html.replace("%queue_" + type + "%", String.valueOf(QUEUES.get(type).size()));
					html.replace("%enabled_" + type + "%", ENABLED_FIGHT_TYPES.contains(type) ? "" : "<font color=\"FF0000\">[Disabled]</font>");
				}
				html.replace("%active_fights%", String.valueOf(ACTIVE_FIGHTS.size()));
				player.sendPacket(html);
			}
			return null;
		}
		
		// NPC dialog - show registered page with replacements.
		if ("show-registered".equals(event))
		{
			if ((npc != null) && (player != null))
			{
				final NpcHtmlMessage html = new NpcHtmlMessage(npc.getObjectId());
				html.setFile(player, HTML_PATH + "registered.html");
				html.replace("%fight_type%", findPlayerFightType(player));
				player.sendPacket(html);
			}
			return null;
		}
		
		// Remaining events require player.
		if (player == null)
		{
			return null;
		}
		
		// Registration events: Register_1, Register_2, etc.
		if (event.startsWith("Register_"))
		{
			return handleRegistration(event, player);
		}
		
		// Cancel registration.
		if ("CancelRegistration".equals(event))
		{
			return handleCancelRegistration(player);
		}
		
		// Ranking menu.
		if ("Ranking".equals(event))
		{
			if (npc != null)
			{
				final NpcHtmlMessage html = new NpcHtmlMessage(npc.getObjectId());
				html.setFile(player, HTML_PATH + "ranking.html");
				player.sendPacket(html);
			}
			return null;
		}
		
		// Ranking per fight type.
		if (event.startsWith("Ranking_"))
		{
			try
			{
				final int fightType = Integer.parseInt(event.substring("Ranking_".length()));
				showRanking(player, npc, fightType);
			}
			catch (NumberFormatException e)
			{
				// Ignore.
			}
			return null;
		}
		
		return null;
	}
	
	// =========================================================================
	// Registration
	// =========================================================================
	
	private String handleRegistration(String event, Player player)
	{
		if (!IS_REGISTERING())
		{
			return null;
		}
		
		final int fightType;
		try
		{
			fightType = Integer.parseInt(event.substring("Register_".length()));
		}
		catch (NumberFormatException e)
		{
			return null;
		}
		
		if (!ENABLED_FIGHT_TYPES.contains(fightType))
		{
			player.sendMessage("This fight mode is not enabled.");
			return "registration-failed.html";
		}
		
		if (!canRegister(player))
		{
			return "registration-failed.html";
		}
		
		// Dualbox check.
		if ((Config.DUALBOX_CHECK_MAX_L2EVENT_PARTICIPANTS_PER_IP > 0) && !AntiFeedManager.getInstance().tryAddPlayer(AntiFeedManager.L2EVENT_ID, player, Config.DUALBOX_CHECK_MAX_L2EVENT_PARTICIPANTS_PER_IP))
		{
			return "registration-ip.html";
		}
		
		// Solo registration (1x1).
		if (fightType == 1)
		{
			final List<Player> members = new ArrayList<>();
			members.add(player);
			QUEUES.get(fightType).add(new QueueEntry(player, members, fightType));
			REGISTERED_PLAYERS.add(player);
			player.setRegisteredOnEvent(true);
			addLogoutListener(player);
			return "registration-success.html";
		}
		
		// Team registration (2x2+).
		final Party party = player.getParty();
		if ((party == null) || (party.getMemberCount() != fightType))
		{
			player.sendMessage("You need a party with exactly " + fightType + " members for " + fightType + "x" + fightType + ".");
			// Remove dualbox entry.
			if (Config.DUALBOX_CHECK_MAX_L2EVENT_PARTICIPANTS_PER_IP > 0)
			{
				AntiFeedManager.getInstance().removePlayer(AntiFeedManager.L2EVENT_ID, player);
			}
			return "registration-party.html";
		}
		
		if (!party.isLeader(player))
		{
			player.sendMessage("Only the party leader can register the team.");
			if (Config.DUALBOX_CHECK_MAX_L2EVENT_PARTICIPANTS_PER_IP > 0)
			{
				AntiFeedManager.getInstance().removePlayer(AntiFeedManager.L2EVENT_ID, player);
			}
			return "registration-failed.html";
		}
		
		// Validate all party members.
		for (Player member : party.getMembers())
		{
			if (member.equals(player))
			{
				continue; // Leader already checked.
			}
			if (!canRegister(member))
			{
				player.sendMessage("Party member " + member.getName() + " cannot participate.");
				if (Config.DUALBOX_CHECK_MAX_L2EVENT_PARTICIPANTS_PER_IP > 0)
				{
					AntiFeedManager.getInstance().removePlayer(AntiFeedManager.L2EVENT_ID, player);
				}
				return "registration-failed.html";
			}
			if ((Config.DUALBOX_CHECK_MAX_L2EVENT_PARTICIPANTS_PER_IP > 0) && !AntiFeedManager.getInstance().tryAddPlayer(AntiFeedManager.L2EVENT_ID, member, Config.DUALBOX_CHECK_MAX_L2EVENT_PARTICIPANTS_PER_IP))
			{
				player.sendMessage("Party member " + member.getName() + " - too many registrations from same IP.");
				AntiFeedManager.getInstance().removePlayer(AntiFeedManager.L2EVENT_ID, player);
				return "registration-ip.html";
			}
		}
		
		// Register entire party.
		final List<Player> members = new ArrayList<>(party.getMembers());
		QUEUES.get(fightType).add(new QueueEntry(player, members, fightType));
		for (Player member : members)
		{
			REGISTERED_PLAYERS.add(member);
			member.setRegisteredOnEvent(true);
			addLogoutListener(member);
			if (!member.equals(player))
			{
				member.sendMessage("Your team has been registered for " + fightType + "x" + fightType + " Tournament!");
			}
		}
		return "registration-success.html";
	}
	
	private String handleCancelRegistration(Player player)
	{
		if (!REGISTERED_PLAYERS.contains(player))
		{
			return null;
		}
		
		for (Map.Entry<Integer, List<QueueEntry>> queueEntry : QUEUES.entrySet())
		{
			final List<QueueEntry> queue = queueEntry.getValue();
			QueueEntry toRemove = null;
			for (QueueEntry qe : queue)
			{
				if (qe.members.contains(player))
				{
					toRemove = qe;
					break;
				}
			}
			if (toRemove != null)
			{
				queue.remove(toRemove);
				for (Player member : toRemove.members)
				{
					REGISTERED_PLAYERS.remove(member);
					member.setRegisteredOnEvent(false);
					removeListeners(member);
					if (Config.DUALBOX_CHECK_MAX_L2EVENT_PARTICIPANTS_PER_IP > 0)
					{
						AntiFeedManager.getInstance().removePlayer(AntiFeedManager.L2EVENT_ID, member);
					}
					if (!member.equals(player))
					{
						member.sendMessage("Your team has been unregistered from the Tournament.");
					}
				}
				break;
			}
		}
		return "registration-canceled.html";
	}
	
	private boolean canRegister(Player player)
	{
		if (REGISTERED_PLAYERS.contains(player))
		{
			player.sendMessage("You are already registered in the Tournament.");
			return false;
		}
		if (isPlayerInFight(player))
		{
			player.sendMessage("You are already in a Tournament fight.");
			return false;
		}
		if (player.getLevel() < MIN_LEVEL)
		{
			player.sendMessage("Your level is too low to participate.");
			return false;
		}
		if (player.getLevel() > MAX_LEVEL)
		{
			player.sendMessage("Your level is too high to participate.");
			return false;
		}
		if (player.isOnEvent() || player.isRegisteredOnEvent() || (player.getBlockCheckerArena() > -1))
		{
			player.sendMessage("You are already registered on an event.");
			return false;
		}
		if (player.isFlyingMounted())
		{
			player.sendMessage("You cannot register while flying.");
			return false;
		}
		if (player.isTransformed())
		{
			player.sendMessage("You cannot register while transformed.");
			return false;
		}
		if (!player.isInventoryUnder80(false))
		{
			player.sendMessage("Too many items in your inventory.");
			return false;
		}
		if (player.getWeightPenalty() != 0)
		{
			player.sendMessage("Your inventory is too heavy.");
			return false;
		}
		if (player.isCursedWeaponEquipped() || (player.getKarma() > 0))
		{
			player.sendMessage("People with bad reputation can't register.");
			return false;
		}
		if (player.isInDuel())
		{
			player.sendMessage("You cannot register while in a duel.");
			return false;
		}
		if (player.isInOlympiadMode() || OlympiadManager.getInstance().isRegistered(player))
		{
			player.sendMessage("You cannot participate while registered on the Olympiad.");
			return false;
		}
		if (player.getInstanceId() > 0)
		{
			player.sendMessage("You cannot register while in an instance.");
			return false;
		}
		if (player.isInSiege() || player.isInsideZone(ZoneId.SIEGE))
		{
			player.sendMessage("You cannot register while on a siege.");
			return false;
		}
		if (player.isFishing())
		{
			player.sendMessage("You cannot register while fishing.");
			return false;
		}
		return true;
	}
	
	// =========================================================================
	// Matchmaking
	// =========================================================================
	
	private void processMatchmaking()
	{
		for (int type : ALL_FIGHT_TYPES)
		{
			if (!ENABLED_FIGHT_TYPES.contains(type))
			{
				continue;
			}
			
			final List<QueueEntry> queue = QUEUES.get(type);
			if (queue == null)
			{
				continue;
			}
			
			// Remove invalid entries (offline players).
			queue.removeIf(entry ->
			{
				final boolean invalid = entry.members.stream().anyMatch(p -> (p == null) || (p.isOnlineInt() != 1));
				if (invalid)
				{
					for (Player p : entry.members)
					{
						if (p != null)
						{
							REGISTERED_PLAYERS.remove(p);
							p.setRegisteredOnEvent(false);
							removeListeners(p);
						}
					}
				}
				return invalid;
			});
			
			// Match pairs.
			while (queue.size() >= 2)
			{
				final QueueEntry entry1 = queue.remove(0);
				final QueueEntry entry2 = queue.remove(0);
				createFight(type, entry1, entry2);
			}
		}
	}
	
	private void createFight(int fightType, QueueEntry blue, QueueEntry red)
	{
		final int fightId = FIGHT_ID_COUNTER.incrementAndGet();
		final Fight fight = new Fight(fightId, fightType, blue.members, red.members);
		ACTIVE_FIGHTS.put(fightId, fight);
		
		// Start fight preparation after a brief delay (let matchmaking finish).
		startQuestTimer("fight-prepare-" + fightId, 500, null, null);
	}
	
	// =========================================================================
	// Fight Management
	// =========================================================================
	
	private void handleFightTimer(String event)
	{
		final String[] parts = event.split("-");
		if (parts.length < 3)
		{
			return;
		}
		
		final String action = parts[1];
		final int fightId;
		try
		{
			fightId = Integer.parseInt(parts[2]);
		}
		catch (NumberFormatException e)
		{
			return;
		}
		
		final Fight fight = ACTIVE_FIGHTS.get(fightId);
		if (fight == null)
		{
			return;
		}
		
		switch (action)
		{
			case "prepare":
			{
				handleFightPrepare(fight);
				break;
			}
			case "heal":
			{
				handleFightHeal(fight);
				break;
			}
			case "cd":
			{
				if (parts.length >= 4)
				{
					broadcastToFight(fight, parts[3], 4);
				}
				break;
			}
			case "start":
			{
				handleFightStart(fight);
				break;
			}
			case "endcd":
			{
				if (parts.length >= 4)
				{
					broadcastToFight(fight, parts[3], 4);
				}
				break;
			}
			case "end":
			{
				handleFightEnd(fight);
				break;
			}
			case "cleanup":
			{
				handleFightCleanup(fight);
				break;
			}
		}
	}
	
	private void handleFightPrepare(Fight fight)
	{
		// Validate players are still online.
		fight.teamBlue.removeIf(p -> (p == null) || (p.isOnlineInt() != 1));
		fight.teamRed.removeIf(p -> (p == null) || (p.isOnlineInt() != 1));
		
		if (fight.teamBlue.isEmpty() || fight.teamRed.isEmpty())
		{
			// Cancel fight.
			for (Player p : fight.getAllPlayers())
			{
				if (p != null)
				{
					REGISTERED_PLAYERS.remove(p);
					p.setRegisteredOnEvent(false);
					p.setOnEvent(false);
					removeListeners(p);
					p.sendMessage("Tournament fight canceled - not enough players.");
				}
			}
			ACTIVE_FIGHTS.remove(fight.id);
			checkEventEnd();
			return;
		}
		
		// Create instance.
		final InstanceWorld world = new InstanceWorld();
		world.setInstance(InstanceManager.getInstance().createDynamicInstance(INSTANCE_ID));
		InstanceManager.getInstance().addWorld(world);
		fight.world = world;
		
		// Close doors.
		world.getDoors().forEach(Door::closeMe);
		
		// Setup blue team.
		for (Player p : fight.teamBlue)
		{
			REGISTERED_PLAYERS.remove(p);
			p.setRegisteredOnEvent(false);
			p.setOnEvent(true);
			p.setTeam(Team.BLUE);
			world.addAllowed(p);
			p.leaveParty();
			p.teleToLocation(BLUE_SPAWN_LOC, world.getInstanceId(), 50);
		}
		
		// Setup red team.
		for (Player p : fight.teamRed)
		{
			REGISTERED_PLAYERS.remove(p);
			p.setRegisteredOnEvent(false);
			p.setOnEvent(true);
			p.setTeam(Team.RED);
			world.addAllowed(p);
			p.leaveParty();
			p.teleToLocation(RED_SPAWN_LOC, world.getInstanceId(), 50);
		}
		
		// Create parties for team modes (2x2+).
		if (fight.fightType > 1)
		{
			createParty(fight.teamBlue);
			createParty(fight.teamRed);
		}
		
		// Heal players shortly after teleport.
		startQuestTimer("fight-heal-" + fight.id, 1500, null, null);
		
		// Schedule countdown before fight starts.
		final long prepMs = FIGHT_PREPARE_TIME * 1000L;
		for (int i = 5; i >= 1; i--)
		{
			if (prepMs > (i * 1000L))
			{
				startQuestTimer("fight-cd-" + fight.id + "-" + i, prepMs - (i * 1000L), null, null);
			}
		}
		startQuestTimer("fight-start-" + fight.id, prepMs, null, null);
		
		// Announce.
		broadcastToFightWithEffect(fight, "Tournament " + fight.fightType + "x" + fight.fightType + " - Prepare for battle!", 5);
	}
	
	private void handleFightHeal(Fight fight)
	{
		for (Player p : fight.getAllPlayers())
		{
			if ((p != null) && (fight.world != null) && (p.getInstanceId() == fight.world.getInstanceId()))
			{
				p.setInvul(true);
				p.setImmobilized(true);
				p.fullRestore();
				final Summon summon = p.getSummon();
				if (summon != null)
				{
					summon.setInvul(true);
					summon.setImmobilized(true);
				}
			}
		}
	}
	
	private void handleFightStart(Fight fight)
	{
		if (fight.state != FightState.PREPARING)
		{
			return;
		}
		
		fight.state = FightState.FIGHTING;
		
		// Open doors.
		if (fight.world != null)
		{
			fight.world.openDoor(BLUE_DOOR_ID);
			fight.world.openDoor(RED_DOOR_ID);
		}
		
		// Enable all players and add death listeners.
		for (Player p : fight.getAllPlayers())
		{
			if ((p != null) && (fight.world != null) && (p.getInstanceId() == fight.world.getInstanceId()))
			{
				p.setInvul(false);
				p.setImmobilized(false);
				p.enableAllSkills();
				final Summon summon = p.getSummon();
				if (summon != null)
				{
					summon.setInvul(false);
					summon.setImmobilized(false);
					summon.enableAllSkills();
				}
				addDeathListener(p);
			}
		}
		
		broadcastToFightWithEffect(fight, "Fight!", 3);
		
		// Schedule fight end.
		final int fightTimeSec = FIGHT_TIMES.getOrDefault(fight.fightType, 180);
		final long fightTimeMs = fightTimeSec * 1000L;
		
		// End countdown (last 10 seconds).
		for (int i = 10; i >= 1; i--)
		{
			if (fightTimeMs > (i * 1000L))
			{
				startQuestTimer("fight-endcd-" + fight.id + "-" + i, fightTimeMs - (i * 1000L), null, null);
			}
		}
		startQuestTimer("fight-end-" + fight.id, fightTimeMs, null, null);
		
		// Send UI timer.
		for (Player p : fight.getAllPlayers())
		{
			if (p != null)
			{
				p.sendPacket(new ExSendUIEvent(p, false, false, fightTimeSec, 10, NpcStringId.TIME_REMAINING));
			}
		}
	}
	
	private void handleFightEnd(Fight fight)
	{
		if (fight.state == FightState.ENDED)
		{
			return;
		}
		
		fight.state = FightState.ENDED;
		
		// Cancel remaining fight timers.
		cancelFightTimers(fight);
		
		// Disable all players.
		for (Player p : fight.getAllPlayers())
		{
			if ((p != null) && (fight.world != null) && (p.getInstanceId() == fight.world.getInstanceId()))
			{
				p.setInvul(true);
				p.setImmobilized(true);
				p.disableAllSkills();
				final Summon summon = p.getSummon();
				if (summon != null)
				{
					summon.setInvul(true);
					summon.setImmobilized(true);
					summon.disableAllSkills();
				}
			}
		}
		
		// Close doors.
		if (fight.world != null)
		{
			fight.world.closeDoor(BLUE_DOOR_ID);
			fight.world.closeDoor(RED_DOOR_ID);
		}
		
		// Determine winner.
		int blueAlive = 0;
		int redAlive = 0;
		int blueKills = 0;
		int redKills = 0;
		
		for (Player p : fight.teamBlue)
		{
			if ((p != null) && !p.isDead() && (fight.world != null) && (p.getInstanceId() == fight.world.getInstanceId()))
			{
				blueAlive++;
			}
			blueKills += fight.kills.getOrDefault(p, 0);
		}
		for (Player p : fight.teamRed)
		{
			if ((p != null) && !p.isDead() && (fight.world != null) && (p.getInstanceId() == fight.world.getInstanceId()))
			{
				redAlive++;
			}
			redKills += fight.kills.getOrDefault(p, 0);
		}
		
		// Revive dead players (only visually stand them up - full restore will happen in cleanup).
		for (Player p : fight.getAllPlayers())
		{
			if ((p != null) && p.isDead())
			{
				p.setIsPendingRevive(true);
			}
		}
		
		List<Player> winners = null;
		List<Player> losers = null;
		boolean isTie = false;
		
		if (blueAlive > redAlive)
		{
			winners = fight.teamBlue;
			losers = fight.teamRed;
		}
		else if (redAlive > blueAlive)
		{
			winners = fight.teamRed;
			losers = fight.teamBlue;
		}
		else if (blueKills > redKills)
		{
			winners = fight.teamBlue;
			losers = fight.teamRed;
		}
		else if (redKills > blueKills)
		{
			winners = fight.teamRed;
			losers = fight.teamBlue;
		}
		else
		{
			isTie = true;
		}
		
		if (!isTie)
		{
			final String winTeam = (winners == fight.teamBlue) ? "Blue" : "Red";
			broadcastToFightWithEffect(fight, "Team " + winTeam + " wins!", 7);
			
			// Firework + rewards for winners.
			final Skill skill = CommonSkill.FIREWORK.getSkill();
			final ItemHolder reward = FIGHT_REWARDS.get(fight.fightType);
			for (Player p : winners)
			{
				if ((p != null) && (fight.world != null) && (p.getInstanceId() == fight.world.getInstanceId()))
				{
					p.broadcastPacket(new MagicSkillUse(p, p, skill.getId(), skill.getLevel(), skill.getHitTime(), skill.getReuseDelay()));
					p.broadcastSocialAction(3);
					if (reward != null)
					{
						giveItems(p, reward);
					}
				}
			}
			
			for (Player p : losers)
			{
				if (p != null)
				{
					p.broadcastSocialAction(13);
				}
			}
			
			updateRanking(winners, losers, false, fight);
		}
		else
		{
			broadcastToFightWithEffect(fight, "Draw!", 7);
			for (Player p : fight.getAllPlayers())
			{
				if (p != null)
				{
					p.broadcastSocialAction(13);
				}
			}
			updateRanking(fight.teamBlue, fight.teamRed, true, fight);
		}
		
		// Remove UI timer.
		for (Player p : fight.getAllPlayers())
		{
			if (p != null)
			{
				p.sendPacket(new ExSendUIEvent(p, false, false, 0, 0, NpcStringId.TIME_REMAINING));
			}
		}
		
		// Schedule cleanup.
		startQuestTimer("fight-cleanup-" + fight.id, 7000, null, null);
	}
	
	private void handleFightCleanup(Fight fight)
	{
		// Remove listeners and flags.
		for (Player p : fight.getAllPlayers())
		{
			if (p != null)
			{
				// Revive any players still dead before removing from instance.
				if (p.isDead())
				{
					p.setIsPendingRevive(true);
					p.doRevive();
					p.fullRestore();
				}
				removeListeners(p);
				p.setTeam(Team.NONE);
				p.setOnEvent(false);
				p.setInvul(false);
				p.setImmobilized(false);
				p.enableAllSkills();
				p.leaveParty();
				final Summon summon = p.getSummon();
				if (summon != null)
				{
					summon.setInvul(false);
					summon.setImmobilized(false);
					summon.enableAllSkills();
				}
			}
		}
		
		// Destroy instance.
		if (fight.world != null)
		{
			fight.world.destroy();
		}
		
		ACTIVE_FIGHTS.remove(fight.id);
		checkEventEnd();
	}
	
	// =========================================================================
	// Event End Handling
	// =========================================================================
	
	private void handleEventEnd()
	{
		// Stop accepting registrations.
		setState(EventState.FIGHTING);
		
		// Despawn NPC.
		if (MANAGER_NPC_INSTANCE != null)
		{
			MANAGER_NPC_INSTANCE.deleteMe();
			MANAGER_NPC_INSTANCE = null;
		}
		
		// Final matchmaking round.
		processMatchmaking();
		
		// Clean remaining unmatched queue entries.
		for (List<QueueEntry> queue : QUEUES.values())
		{
			for (QueueEntry entry : queue)
			{
				for (Player p : entry.members)
				{
					if (p != null)
					{
						p.sendMessage("Tournament registration ended. No match found.");
						p.setRegisteredOnEvent(false);
						removeListeners(p);
						REGISTERED_PLAYERS.remove(p);
						if (Config.DUALBOX_CHECK_MAX_L2EVENT_PARTICIPANTS_PER_IP > 0)
						{
							AntiFeedManager.getInstance().removePlayer(AntiFeedManager.L2EVENT_ID, p);
						}
					}
				}
			}
			queue.clear();
		}
		REGISTERED_PLAYERS.clear();
		
		Broadcast.toAllOnlinePlayers("Tournament Event: Registration ended.");
		
		// Check if there are active fights.
		if (ACTIVE_FIGHTS.isEmpty())
		{
			setState(EventState.INACTIVE);
		}
		else
		{
			startQuestTimer("EventEndCheck", 10000, null, null);
		}
	}
	
	private void checkEventEnd()
	{
		if (!IS_REGISTERING() && ACTIVE_FIGHTS.isEmpty())
		{
			setState(EventState.INACTIVE);
		}
	}
	
	// =========================================================================
	// Death / Logout Listeners
	// =========================================================================
	
	@RegisterEvent(EventType.ON_PLAYER_LOGOUT)
	private void onPlayerLogout(OnPlayerLogout event)
	{
		final Player player = event.getPlayer();
		
		// Remove from queues.
		if (REGISTERED_PLAYERS.contains(player))
		{
			handleCancelRegistration(player);
		}
		
		// Handle active fight.
		final Fight fight = findFightByPlayer(player);
		if (fight != null)
		{
			fight.teamBlue.remove(player);
			fight.teamRed.remove(player);
			removeListeners(player);
			player.setTeam(Team.NONE);
			player.setOnEvent(false);
			
			// Check if a team is now empty.
			if (fight.teamBlue.isEmpty() || fight.teamRed.isEmpty())
			{
				if (fight.state == FightState.FIGHTING)
				{
					broadcastToFightWithEffect(fight, "Opponent disconnected!", 5);
					cancelQuestTimer("fight-end-" + fight.id, null, null);
					startQuestTimer("fight-end-" + fight.id, 3000, null, null);
				}
				else if (fight.state == FightState.PREPARING)
				{
					// Cancel fight entirely.
					cancelFightTimers(fight);
					fight.state = FightState.ENDED;
					for (Player p : fight.getAllPlayers())
					{
						if (p != null)
						{
							removeListeners(p);
							p.setTeam(Team.NONE);
							p.setOnEvent(false);
							p.setInvul(false);
							p.setImmobilized(false);
							p.enableAllSkills();
							p.leaveParty();
							p.sendMessage("Tournament fight canceled - opponent disconnected.");
						}
					}
					if (fight.world != null)
					{
						fight.world.destroy();
					}
					ACTIVE_FIGHTS.remove(fight.id);
					checkEventEnd();
				}
			}
		}
	}
	
	@RegisterEvent(EventType.ON_CREATURE_DEATH)
	public void onPlayerDeath(OnCreatureDeath event)
	{
		if (!event.getTarget().isPlayer())
		{
			return;
		}
		
		final Player killed = event.getTarget().asPlayer();
		final Fight fight = findFightByPlayer(killed);
		if ((fight == null) || (fight.state != FightState.FIGHTING))
		{
			return;
		}
		
		// Track kill.
		if (event.getAttacker().isPlayer())
		{
			final Player killer = event.getAttacker().asPlayer();
			fight.kills.merge(killer, 1, Integer::sum);
		}
		
		// Check if all members of one team are dead or gone.
		// Note: Include explicit check for 'killed' player because isDead() may not yet return true when ON_CREATURE_DEATH fires.
		final boolean allBlueDead = fight.teamBlue.stream().allMatch(p -> (p == killed) || p.isDead() || (p.getInstanceId() != fight.world.getInstanceId()));
		final boolean allRedDead = fight.teamRed.stream().allMatch(p -> (p == killed) || p.isDead() || (p.getInstanceId() != fight.world.getInstanceId()));
		
		if (allBlueDead || allRedDead)
		{
			cancelQuestTimer("fight-end-" + fight.id, null, null);
			handleFightEnd(fight);
		}
	}
	
	// =========================================================================
	// Event Start / Stop
	// =========================================================================
	
	@Override
	public boolean eventStart(Player eventMaker)
	{
		if (!IS_INACTIVE())
		{
			return false;
		}
		
		setState(EventState.REGISTERING);
		
		// Cancel existing timers.
		for (List<QuestTimer> timers : getQuestTimers().values())
		{
			for (QuestTimer timer : timers)
			{
				timer.cancel();
			}
		}
		
		// Register at AntiFeedManager.
		if (Config.DUALBOX_CHECK_MAX_L2EVENT_PARTICIPANTS_PER_IP > 0)
		{
			AntiFeedManager.getInstance().registerEvent(AntiFeedManager.L2EVENT_ID);
			AntiFeedManager.getInstance().clear(AntiFeedManager.L2EVENT_ID);
		}
		
		// Clear state.
		REGISTERED_PLAYERS.clear();
		for (List<QueueEntry> queue : QUEUES.values())
		{
			queue.clear();
		}
		ACTIVE_FIGHTS.clear();
		
		// Spawn NPC manager.
		MANAGER_NPC_INSTANCE = addSpawn(MANAGER, MANAGER_SPAWN_LOC, false, 0);
		
		// Start matchmaker timer.
		startQuestTimer("Matchmaker", 15000, null, null);
		
		// Schedule event end.
		startQuestTimer("EventEnd", REGISTRATION_TIME * 60000L, null, null);
		
		// Broadcast.
		Broadcast.toAllOnlinePlayers("Tournament Event: Registration opened for " + REGISTRATION_TIME + " minutes!");
		Broadcast.toAllOnlinePlayers("Tournament Event: Talk to the Tournament Manager in Giran.");
		
		return true;
	}
	
	@Override
	public boolean eventStop()
	{
		// Despawn manager.
		if (MANAGER_NPC_INSTANCE != null)
		{
			MANAGER_NPC_INSTANCE.deleteMe();
			MANAGER_NPC_INSTANCE = null;
		}
		
		// Cancel all timers.
		for (List<QuestTimer> timers : getQuestTimers().values())
		{
			for (QuestTimer timer : timers)
			{
				timer.cancel();
			}
		}
		
		// Clean up registered players.
		for (Player p : REGISTERED_PLAYERS)
		{
			if (p != null)
			{
				p.setRegisteredOnEvent(false);
				p.setOnEvent(false);
				removeListeners(p);
			}
		}
		REGISTERED_PLAYERS.clear();
		for (List<QueueEntry> queue : QUEUES.values())
		{
			queue.clear();
		}
		
		// End all active fights.
		for (Fight fight : new ArrayList<>(ACTIVE_FIGHTS.values()))
		{
			fight.state = FightState.ENDED;
			for (Player p : fight.getAllPlayers())
			{
				if (p != null)
				{
					removeListeners(p);
					p.setTeam(Team.NONE);
					p.setOnEvent(false);
					p.setInvul(false);
					p.setImmobilized(false);
					p.enableAllSkills();
					p.leaveParty();
					if (p.isDead())
					{
						p.doRevive();
					}
					final Summon summon = p.getSummon();
					if (summon != null)
					{
						summon.setInvul(false);
						summon.setImmobilized(false);
						summon.enableAllSkills();
					}
					p.sendPacket(new ExSendUIEvent(p, false, false, 0, 0, NpcStringId.TIME_REMAINING));
				}
			}
			if (fight.world != null)
			{
				fight.world.destroy();
			}
		}
		ACTIVE_FIGHTS.clear();
		
		Broadcast.toAllOnlinePlayers("Tournament Event: Event was canceled.");
		setState(EventState.INACTIVE);
		
		return true;
	}
	
	// =========================================================================
	// Ranking
	// =========================================================================
	
	private void updateRanking(List<Player> winners, List<Player> losers, boolean tie, Fight fight)
	{
		try (Connection con = DatabaseFactory.getConnection())
		{
			if (tie)
			{
				for (Player p : fight.getAllPlayers())
				{
					if (p != null)
					{
						try (PreparedStatement ps = con.prepareStatement(INSERT_OR_UPDATE_RANKING))
						{
							ps.setInt(1, p.getObjectId());
							ps.setString(2, p.getName());
							ps.setInt(3, fight.fightType);
							ps.setInt(4, 0); // victories
							ps.setInt(5, 0); // defeats
							ps.setInt(6, 1); // ties
							ps.setInt(7, fight.kills.getOrDefault(p, 0));
							ps.setInt(8, 0); // deaths
							ps.execute();
						}
					}
				}
			}
			else
			{
				for (Player p : winners)
				{
					if (p != null)
					{
						try (PreparedStatement ps = con.prepareStatement(INSERT_OR_UPDATE_RANKING))
						{
							ps.setInt(1, p.getObjectId());
							ps.setString(2, p.getName());
							ps.setInt(3, fight.fightType);
							ps.setInt(4, 1); // victories
							ps.setInt(5, 0); // defeats
							ps.setInt(6, 0); // ties
							ps.setInt(7, fight.kills.getOrDefault(p, 0));
							ps.setInt(8, 0); // deaths
							ps.execute();
						}
					}
				}
				for (Player p : losers)
				{
					if (p != null)
					{
						try (PreparedStatement ps = con.prepareStatement(INSERT_OR_UPDATE_RANKING))
						{
							ps.setInt(1, p.getObjectId());
							ps.setString(2, p.getName());
							ps.setInt(3, fight.fightType);
							ps.setInt(4, 0); // victories
							ps.setInt(5, 1); // defeats
							ps.setInt(6, 0); // ties
							ps.setInt(7, fight.kills.getOrDefault(p, 0));
							ps.setInt(8, 1); // deaths
							ps.execute();
						}
					}
				}
			}
		}
		catch (Exception e)
		{
			LOGGER.warning("Tournament: Error updating ranking: " + e.getMessage());
		}
	}
	
	private void showRanking(Player player, Npc npc, int fightType)
	{
		final StringBuilder sb = new StringBuilder();
		sb.append("<html><title>Tournament</title><body><center>");
		sb.append("<table border=0 cellpadding=0 cellspacing=0 width=290 background=L2UI_CH3.refinewnd_back_Pattern>");
		sb.append("<tr><td valign=top align=center>");
		sb.append("<table border=0 cellpadding=0 cellspacing=0>");
		sb.append("<tr><td width=256 height=60 background=\"L2UI_CT1.OlympiadWnd_DF_GrandTexture\"></td></tr>");
		sb.append("</table>");
		sb.append("<table border=0 cellpadding=0 cellspacing=0>");
		sb.append("<tr><td align=center fixwidth=290>");
		sb.append("<font name=\"hs15\" color=\"CDB67F\">Ranking ").append(fightType).append("x").append(fightType).append("</font><br1>");
		sb.append("<img src=\"L2UI_CH3.herotower_deco\" width=256 height=32>");
		sb.append("</td></tr></table><br>");
		sb.append("<table width=270 border=0>");
		sb.append("<tr>");
		sb.append("<td width=25 align=center><font color=\"LEVEL\">#</font></td>");
		sb.append("<td width=95 align=center><font color=\"LEVEL\">Name</font></td>");
		sb.append("<td width=30 align=center><font color=\"LEVEL\">W</font></td>");
		sb.append("<td width=30 align=center><font color=\"LEVEL\">L</font></td>");
		sb.append("<td width=30 align=center><font color=\"LEVEL\">D</font></td>");
		sb.append("<td width=30 align=center><font color=\"LEVEL\">K</font></td>");
		sb.append("</tr>");
		
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement ps = con.prepareStatement(SELECT_RANKING))
		{
			ps.setInt(1, fightType);
			try (ResultSet rs = ps.executeQuery())
			{
				int rank = 1;
				while (rs.next())
				{
					final String name = rs.getString("char_name");
					final int victories = rs.getInt("victories");
					final int defeats = rs.getInt("defeats");
					final int ties = rs.getInt("ties");
					final int kills = rs.getInt("kills");
					
					sb.append("<tr>");
					sb.append("<td width=25 align=center>").append(rank).append("</td>");
					sb.append("<td width=95 align=center>").append(name).append("</td>");
					sb.append("<td width=30 align=center><font color=\"00FF00\">").append(victories).append("</font></td>");
					sb.append("<td width=30 align=center><font color=\"FF0000\">").append(defeats).append("</font></td>");
					sb.append("<td width=30 align=center>").append(ties).append("</td>");
					sb.append("<td width=30 align=center>").append(kills).append("</td>");
					sb.append("</tr>");
					rank++;
				}
				
				if (rank == 1)
				{
					sb.append("<tr><td colspan=6 align=center><font color=\"999999\">No data available.</font></td></tr>");
				}
			}
		}
		catch (Exception e)
		{
			sb.append("<tr><td colspan=6 align=center><font color=\"FF0000\">Error loading ranking.</font></td></tr>");
			LOGGER.warning("Tournament: Error loading ranking: " + e.getMessage());
		}
		
		sb.append("</table><br>");
		sb.append("<button value=\"Back\" action=\"bypass -h Quest Tournament Ranking\" width=100 height=27 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"/>");
		sb.append("<br><br></td></tr></table></center></body></html>");
		
		final NpcHtmlMessage html = new NpcHtmlMessage(npc != null ? npc.getObjectId() : 0);
		html.setHtml(sb.toString());
		player.sendPacket(html);
	}
	
	// =========================================================================
	// Utility Methods
	// =========================================================================
	
	private void createParty(List<Player> team)
	{
		if (team.size() <= 1)
		{
			return;
		}
		final Player first = team.get(0);
		final Party party = new Party(first, PartyDistributionType.FINDERS_KEEPERS);
		first.joinParty(party);
		for (int i = 1; i < team.size(); i++)
		{
			team.get(i).joinParty(party);
		}
	}
	
	private Fight findFightByPlayer(Player player)
	{
		for (Fight fight : ACTIVE_FIGHTS.values())
		{
			if (fight.teamBlue.contains(player) || fight.teamRed.contains(player))
			{
				return fight;
			}
		}
		return null;
	}
	
	private boolean isPlayerInFight(Player player)
	{
		return findFightByPlayer(player) != null;
	}
	
	private String findPlayerFightType(Player player)
	{
		for (Map.Entry<Integer, List<QueueEntry>> entry : QUEUES.entrySet())
		{
			for (QueueEntry qe : entry.getValue())
			{
				if (qe.members.contains(player))
				{
					return entry.getKey() + "x" + entry.getKey();
				}
			}
		}
		return "?";
	}
	
	private void cancelFightTimers(Fight fight)
	{
		final int id = fight.id;
		cancelQuestTimer("fight-start-" + id, null, null);
		cancelQuestTimer("fight-end-" + id, null, null);
		cancelQuestTimer("fight-cleanup-" + id, null, null);
		cancelQuestTimer("fight-heal-" + id, null, null);
		for (int i = 10; i >= 1; i--)
		{
			cancelQuestTimer("fight-cd-" + id + "-" + i, null, null);
			cancelQuestTimer("fight-endcd-" + id + "-" + i, null, null);
		}
	}
	
	private void broadcastToFight(Fight fight, String message, int duration)
	{
		if (fight.world != null)
		{
			fight.world.broadcastPacket(new ExShowScreenMessage(message, ExShowScreenMessage.TOP_CENTER, duration * 1000, 0, true, false));
		}
	}
	
	private void broadcastToFightWithEffect(Fight fight, String message, int duration)
	{
		if (fight.world != null)
		{
			fight.world.broadcastPacket(new ExShowScreenMessage(message, ExShowScreenMessage.TOP_CENTER, duration * 1000, 0, true, true));
		}
	}
	
	private void addLogoutListener(Player player)
	{
		player.addListener(new ConsumerEventListener(player, EventType.ON_PLAYER_LOGOUT, (OnPlayerLogout event) -> onPlayerLogout(event), this));
	}
	
	private void addDeathListener(Player player)
	{
		player.addListener(new ConsumerEventListener(player, EventType.ON_CREATURE_DEATH, (OnCreatureDeath event) -> onPlayerDeath(event), this));
	}
	
	private void removeListeners(Player player)
	{
		for (AbstractEventListener listener : player.getListeners(EventType.ON_PLAYER_LOGOUT))
		{
			if (listener.getOwner() == this)
			{
				listener.unregisterMe();
			}
		}
		for (AbstractEventListener listener : player.getListeners(EventType.ON_CREATURE_DEATH))
		{
			if (listener.getOwner() == this)
			{
				listener.unregisterMe();
			}
		}
	}
	
	// =========================================================================
	// State Management
	// =========================================================================
	
	public static void setState(EventState state)
	{
		synchronized (Tournament.class)
		{
			_state = state;
		}
	}
	
	public static boolean IS_INACTIVE()
	{
		synchronized (Tournament.class)
		{
			return _state == EventState.INACTIVE;
		}
	}
	
	public static boolean IS_REGISTERING()
	{
		synchronized (Tournament.class)
		{
			return _state == EventState.REGISTERING;
		}
	}
	
	public static boolean IS_FIGHTING()
	{
		synchronized (Tournament.class)
		{
			return _state == EventState.FIGHTING;
		}
	}
	
	@Override
	public boolean eventBypass(Player player, String bypass)
	{
		return false;
	}
	
	public static void main(String[] args)
	{
		new Tournament();
	}
}
