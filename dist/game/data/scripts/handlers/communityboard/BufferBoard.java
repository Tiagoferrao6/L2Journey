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
 */
package handlers.communityboard;

import static com.l2journey.gameserver.util.FormatUtil.formatAdena;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import com.l2journey.Config;
import com.l2journey.commons.database.DatabaseFactory;
import com.l2journey.gameserver.data.xml.SkillData;
import com.l2journey.gameserver.handler.CommunityBoardHandler;
import com.l2journey.gameserver.handler.IParseBoardHandler;
import com.l2journey.gameserver.model.actor.Creature;
import com.l2journey.gameserver.model.actor.Player;
import com.l2journey.gameserver.model.actor.Summon;
import com.l2journey.gameserver.model.actor.instance.Cubic;
import com.l2journey.gameserver.model.actor.instance.Servitor;
import com.l2journey.gameserver.model.actor.stat.PlayerStat;
import com.l2journey.gameserver.model.actor.status.PlayerStatus;
import com.l2journey.gameserver.model.effects.EffectType;
import com.l2journey.gameserver.model.item.enums.ItemProcessType;
import com.l2journey.gameserver.model.skill.Skill;
import com.l2journey.gameserver.model.skill.skillVariation.ServitorShareConditions;
import com.l2journey.gameserver.network.SystemMessageId;
import com.l2journey.gameserver.network.serverpackets.MagicSkillUse;
import com.l2journey.gameserver.network.serverpackets.SetSummonRemainTime;

/**
 * Complete Community Board Buffer with full scheme support. Uses the same database tables as NpcBuffer (npcbuffer_buff_list, npcbuffer_scheme_list, npcbuffer_scheme_contents). All configurations are read from npcbuffer.ini via {@link Config}.
 * @author KingHanker
 */
public class BufferBoard implements IParseBoardHandler
{
	private static final Logger LOG = Logger.getLogger(BufferBoard.class.getName());
	
	private static final String TITLE = "Community Buffer";
	private static final String[] COMMANDS =
	{
		"_bbsbuffer"
	};
	
	private static final int MAX_SCHEME_BUFFS = Config.BUFFS_MAX_AMOUNT;
	private static final int MAX_SCHEME_DANCES = Config.DANCES_MAX_AMOUNT;
	private static final int BUFFS_PER_PAGE = 20;
	
	// Buff Set class labels (GM management)
	private static final String SET_FIGHTER = "Fighter";
	private static final String SET_MAGE = "Mage";
	private static final String SET_ALL = "All";
	private static final String SET_NONE = "None";
	
	// Visual skill IDs for animations
	private static final int SKILL_HEAL = 6696;
	private static final int SKILL_BUFF_1 = 1411;
	private static final int SKILL_BUFF_2 = 6662;
	
	// Table row helpers for 2-column grid layout
	private static final String[] TRS =
	{
		"<tr><td height=25>",
		"</td>",
		"<td height=25>",
		"</td></tr>"
	};
	
	// Per-player pet buff mode
	private static final Map<Integer, Boolean> PET_MODE = new ConcurrentHashMap<>();
	
	// Skill cache for performance
	private static final Map<Integer, Skill> SKILL_CACHE = new ConcurrentHashMap<>();
	
	@Override
	public String[] getCommunityBoardCommands()
	{
		return COMMANDS;
	}
	
	// =========================================================
	// COMMAND ROUTING
	// =========================================================
	
	@Override
	public boolean parseCommunityBoardCommand(String command, Player player)
	{
		if (!Config.COMMUNITYBOARD_ENABLED)
		{
			player.sendPacket(SystemMessageId.THE_COMMUNITY_SERVER_IS_CURRENTLY_OFFLINE);
			return false;
		}
		
		// Access validations
		if (!Config.BUFF_WITH_KARMA && (player.getKarma() > 0))
		{
			sendHtml(player, showInfo("Info", "You have too much <font color=FF0000>karma!</font><br>Come back when you don't have any karma!"));
			return false;
		}
		if (player.isInOlympiadMode())
		{
			sendHtml(player, showInfo("Info", "You can't use the buffer while in the <font color=FF0000>Olympiad!</font>"));
			return false;
		}
		if (player.isOnEvent())
		{
			sendHtml(player, showInfo("Info", "You can't use the buffer while in an <font color=FF0000>Event!</font>"));
			return false;
		}
		if (player.getLevel() < Config.MIN_LEVEL)
		{
			sendHtml(player, showInfo("Info", "Your level is too low!<br>You need at least level <font color=LEVEL>" + Config.MIN_LEVEL + "</font>."));
			return false;
		}
		if (!Config.BUFF_WITH_FLAG && (player.getPvpFlag() > 0))
		{
			sendHtml(player, showInfo("Info", "You can't use the buffer while <font color=800080>flagged!</font>"));
			return false;
		}
		if (player.isInCombat())
		{
			sendHtml(player, showInfo("Info", "You can't use the buffer while in <font color=FF0000>combat!</font>"));
			return false;
		}
		
		final String params = command.startsWith("_bbsbuffer;") ? command.substring(11) : "";
		String html = null;
		
		try
		{
			if (params.isEmpty())
			{
				html = buildMainPage(player);
			}
			else if (params.equals("togglePet"))
			{
				PET_MODE.put(player.getObjectId(), !isPetMode(player));
				html = buildMainPage(player);
			}
			else if (params.startsWith("view;"))
			{
				html = buildCategoryPage(params.substring(5));
			}
			else if (params.startsWith("give;"))
			{
				html = handleGiveBuff(player, params.substring(5));
			}
			else if (params.equals("heal"))
			{
				html = handleHeal(player);
			}
			else if (params.equals("removeBuffs"))
			{
				html = handleRemoveBuffs(player);
			}
			else if (params.equals("castSet"))
			{
				html = handleCastBuffSet(player);
			}
			else if (params.startsWith("cast;"))
			{
				html = handleCastScheme(player, params.substring(5));
			}
			else if (params.equals("create_1"))
			{
				html = createSchemeForm();
			}
			else if (params.startsWith("create;"))
			{
				html = handleCreateScheme(player, params.substring(7));
			}
			else if (params.equals("edit_1"))
			{
				html = editSchemeList(player);
			}
			else if (params.equals("delete_1"))
			{
				html = deleteSchemeList(player);
			}
			else if (params.startsWith("delete_c;"))
			{
				final String[] dp = params.substring(9).split(";", 2);
				html = confirmDeleteScheme(dp[0], dp.length > 1 ? dp[1] : "?");
			}
			else if (params.startsWith("delete;"))
			{
				html = handleDeleteScheme(player, params.substring(7));
			}
			else if (params.startsWith("manage;"))
			{
				html = getSchemeOptions(params.substring(7));
			}
			else if (params.startsWith("addView;"))
			{
				final String[] ap = params.substring(8).split(";", 2);
				html = viewSchemeBuffs(ap[0], ap.length > 1 ? ap[1] : "1", "add");
			}
			else if (params.startsWith("removeView;"))
			{
				final String[] rp = params.substring(11).split(";", 2);
				html = viewSchemeBuffs(rp[0], rp.length > 1 ? rp[1] : "1", "remove");
			}
			else if (params.startsWith("addBuff;"))
			{
				html = handleAddBuffToScheme(params.substring(8));
			}
			else if (params.startsWith("removeBuff;"))
			{
				html = handleRemoveBuffFromScheme(params.substring(11));
			}
			// GM Management routes
			else if (params.equals("gmManage") && player.isGM())
			{
				html = gmViewAllBuffTypes();
			}
			else if (params.startsWith("gmEditList;") && player.isGM())
			{
				final String[] gp = params.substring(11).split(";", 3);
				html = gmViewAllBuffs(gp[0], gp.length > 1 ? gp[1] : gp[0], gp.length > 2 ? gp[2] : "1");
			}
			else if (params.startsWith("gmEditBuff;") && player.isGM())
			{
				final String[] gp = params.substring(11).split(";", 3);
				final String actionPage = gp.length > 1 ? gp[1] : "1-1";
				final String[] ap = actionPage.split("-", 2);
				gmManageSelectedBuff(gp[0], ap[0]);
				final String page = ap.length > 1 ? ap[1] : "1";
				final String type = gp.length > 2 ? gp[2] : "buff";
				html = gmViewAllBuffs(type, type, page);
			}
			else if (params.startsWith("gmChangeSet;") && player.isGM())
			{
				final String[] gp = params.substring(12).split(";", 3);
				html = gmManageSelectedSet(gp[0], gp.length > 1 ? gp[1] : "3", gp.length > 2 ? gp[2] : "1");
			}
			else
			{
				html = buildMainPage(player);
			}
		}
		catch (Exception e)
		{
			LOG.warning("BufferBoard error for " + player.getName() + ": " + e.getMessage());
			html = buildMainPage(player);
		}
		
		if (html != null)
		{
			sendHtml(player, html);
		}
		
		return false;
	}
	
	// =========================================================
	// MAIN PAGE
	// =========================================================
	
	private String buildMainPage(Player player)
	{
		final StringBuilder html = new StringBuilder();
		html.append("<html noscrollbar><title>").append(TITLE).append("</title><body><center>");
		html.append("<img src=\"L2UI_CH3.herotower_deco\" width=256 height=32><br>");
		
		final boolean petMode = isPetMode(player);
		
		// Pet toggle
		html.append(button(petMode ? "Player Options" : "Pet Options", "_bbsbuffer;togglePet", 130));
		
		// ---- Buff Categories Section ----
		if (Config.ENABLE_BUFF_SECTION)
		{
			final StringBuilder cats = new StringBuilder();
			int td = 0;
			
			if (Config.ENABLE_BUFFS)
			{
				cats.append(gridCell(td, button("Buffs", "_bbsbuffer;view;buff", 120)));
				td += 2;
				if (td > 2)
				{
					td = 0;
				}
			}
			if (Config.ENABLE_RESIST)
			{
				cats.append(gridCell(td, button("Resist", "_bbsbuffer;view;resist", 120)));
				td += 2;
				if (td > 2)
				{
					td = 0;
				}
			}
			if (Config.ENABLE_SONGS)
			{
				cats.append(gridCell(td, button("Songs", "_bbsbuffer;view;song", 120)));
				td += 2;
				if (td > 2)
				{
					td = 0;
				}
			}
			if (Config.ENABLE_DANCES)
			{
				cats.append(gridCell(td, button("Dances", "_bbsbuffer;view;dance", 120)));
				td += 2;
				if (td > 2)
				{
					td = 0;
				}
			}
			if (Config.ENABLE_CHANTS)
			{
				cats.append(gridCell(td, button("Chants", "_bbsbuffer;view;chant", 120)));
				td += 2;
				if (td > 2)
				{
					td = 0;
				}
			}
			if (Config.ENABLE_SPECIAL)
			{
				cats.append(gridCell(td, button("Special", "_bbsbuffer;view;special", 120)));
				td += 2;
				if (td > 2)
				{
					td = 0;
				}
			}
			if (Config.ENABLE_OTHERS)
			{
				cats.append(gridCell(td, button("Others", "_bbsbuffer;view;others", 120)));
				td += 2;
				if (td > 2)
				{
					td = 0;
				}
			}
			if (Config.ENABLE_CUBIC)
			{
				cats.append(gridCell(td, button("Cubics", "_bbsbuffer;view;cubic", 120)));
				td += 2;
				if (td > 2)
				{
					td = 0;
				}
			}
			
			if (cats.length() > 0)
			{
				html.append("<BR1><table width=100% border=0 cellspacing=0 cellpadding=1 bgcolor=444444><tr><td><font color=00FFFF>Buffs:</font></td>");
				if (!Config.FREE_BUFFS)
				{
					html.append("<td align=right><font color=LEVEL>").append(formatAdena(Config.BUFF_PRICE)).append("</font> adena</td>");
				}
				html.append("</tr></table><BR1>");
				html.append("<table cellspacing=0 cellpadding=0>").append(cats).append("</table>");
			}
		}
		
		// ---- Preset Section ----
		{
			final StringBuilder presets = new StringBuilder();
			int td = 0;
			
			if (Config.ENABLE_BUFF_SET)
			{
				presets.append(gridCell(td, button(petMode ? "Auto Buff Pet" : "Auto Buff", "_bbsbuffer;castSet", 120)));
				td += 2;
				if (td > 2)
				{
					td = 0;
				}
			}
			if (Config.ENABLE_HEAL)
			{
				presets.append(gridCell(td, button(petMode ? "Heal My Pet" : "Heal", "_bbsbuffer;heal", 120)));
				td += 2;
				if (td > 2)
				{
					td = 0;
				}
			}
			if (Config.ENABLE_BUFF_REMOVE)
			{
				presets.append(gridCell(td, button(petMode ? "Remove Pet Buffs" : "Remove Buffs", "_bbsbuffer;removeBuffs", 120)));
				td += 2;
				if (td > 2)
				{
					td = 0;
				}
			}
			
			if (presets.length() > 0)
			{
				html.append("<BR1><table width=100% border=0 cellspacing=0 cellpadding=1 bgcolor=444444><tr><td><font color=00FFFF>Preset:</font></td>");
				if (!Config.FREE_BUFFS)
				{
					html.append("<td align=right><font color=LEVEL>").append(formatAdena(Config.BUFF_SET_PRICE)).append("</font> adena</td>");
				}
				html.append("</tr></table><BR1>");
				html.append("<table cellspacing=0 cellpadding=0>").append(presets).append("</table>");
			}
		}
		
		// ---- Scheme Section ----
		if (Config.ENABLE_SCHEME_SYSTEM)
		{
			html.append(generateSchemeSection(player));
		}
		
		if (Config.FREE_BUFFS)
		{
			html.append("<BR1><font color=LEVEL>All buffs are for free!</font>");
		}
		
		// ---- GM Management Button (admin only) ----
		if (player.isGM())
		{
			html.append("<BR1>").append(button("GM Manage Buffs", "_bbsbuffer;gmManage", 130));
		}
		
		html.append("<br1><br><font color=303030>").append(TITLE).append("</font></center></body></html>");
		return html.toString();
	}
	
	// =========================================================
	// BUFF CATEGORY PAGE
	// =========================================================
	
	private String buildCategoryPage(String buffType)
	{
		final StringBuilder html = new StringBuilder();
		html.append("<html noscrollbar><title>").append(TITLE).append("</title><body><center><br>");
		
		final List<String> availableBuffs = new ArrayList<>();
		try (Connection con = DatabaseFactory.getConnection())
		{
			final PreparedStatement ps = con.prepareStatement("SELECT buffId, buffLevel FROM npcbuffer_buff_list WHERE buffType=? AND canUse=1 ORDER BY Buff_Class ASC, id");
			ps.setString(1, buffType);
			final ResultSet rs = ps.executeQuery();
			while (rs.next())
			{
				final int buffId = rs.getInt("buffId");
				final int buffLevel = rs.getInt("buffLevel");
				String buffName = SkillData.getInstance().getSkill(buffId, buffLevel).getName();
				buffName = buffName.replace(" ", "+");
				availableBuffs.add(buffName + "_" + buffId + "_" + buffLevel);
			}
			rs.close();
			ps.close();
		}
		catch (SQLException e)
		{
			LOG.warning("BufferBoard buildCategoryPage error: " + e.getMessage());
		}
		
		if (availableBuffs.isEmpty())
		{
			html.append("No buffs are available at this moment!");
		}
		else
		{
			if (Config.FREE_BUFFS)
			{
				html.append("All buffs are for <font color=LEVEL>free</font>!");
			}
			else
			{
				html.append("Each buff costs <font color=LEVEL>").append(formatAdena(getCategoryPrice(buffType))).append("</font> adena!");
			}
			
			html.append("<BR1><table>");
			for (String buff : availableBuffs)
			{
				buff = buff.replace("_", " ");
				final String[] parts = buff.split(" ");
				final String name = parts[0].replace("+", " ");
				final int id = Integer.parseInt(parts[1]);
				final int level = Integer.parseInt(parts[2]);
				html.append("<tr><td>").append(getSkillIconHtml(id, level)).append("</td>");
				html.append("<td>").append(button(name, "_bbsbuffer;give;" + id + ";" + level + ";" + buffType, 190)).append("</td></tr>");
			}
			html.append("</table>");
		}
		
		html.append("<br>").append(button("Back", "_bbsbuffer", 100));
		html.append("<br><font color=303030>").append(TITLE).append("</font></center></body></html>");
		return html.toString();
	}
	
	// =========================================================
	// ACTION HANDLERS
	// =========================================================
	
	private String handleGiveBuff(Player player, String data)
	{
		final String[] parts = data.split(";");
		if (parts.length < 3)
		{
			return buildMainPage(player);
		}
		
		final int skillId = Integer.parseInt(parts[0]);
		final int skillLevel = Integer.parseInt(parts[1]);
		final String buffType = parts[2];
		
		if (!Config.FREE_BUFFS)
		{
			final int cost = getCategoryPrice(buffType);
			if (player.getInventory().getInventoryItemCount(Config.CONSUMABLE_ID, -1) < cost)
			{
				return showInfo("Sorry", "You don't have enough items:<br>You need: <font color=LEVEL>" + formatAdena(cost) + " " + getItemNameHtml(Config.CONSUMABLE_ID) + "!");
			}
			player.destroyItemByItemId(ItemProcessType.FEE, Config.CONSUMABLE_ID, cost, player, true);
		}
		
		final Skill skill = getSkillCached(skillId, skillLevel);
		if (skill == null)
		{
			return buildCategoryPage(buffType);
		}
		
		final boolean petMode = isPetMode(player);
		
		// Cubic special handling
		if ("cubic".equals(buffType))
		{
			if (skill.hasEffectType(EffectType.SUMMON) && (player.getInventory().getInventoryItemCount(skill.getItemConsumeId(), -1) < skill.getItemConsumeCount()))
			{
				return showInfo("Sorry", "You don't have enough items for this cubic!");
			}
			
			final Map<Integer, Cubic> playerCubics = player.getCubics();
			if (playerCubics != null)
			{
				playerCubics.forEach((index, cubic) -> cubic.stopAction());
				playerCubics.clear();
			}
			player.broadcastPacket(new MagicSkillUse(player, skillId, skillLevel, 1000, 0));
			player.useMagic(skill, false, false);
		}
		else if (petMode)
		{
			final Summon summon = player.getSummon();
			if (summon == null)
			{
				return showInfo("Info", "You can't use the Pet's options.<br>Summon your pet first!");
			}
			skill.applyEffects(summon, summon);
		}
		else
		{
			skill.applyEffects(player, player);
		}
		
		return buildCategoryPage(buffType);
	}
	
	private String handleHeal(Player player)
	{
		if (!Config.FREE_BUFFS)
		{
			if (player.getInventory().getInventoryItemCount(Config.CONSUMABLE_ID, -1) < Config.HEAL_PRICE)
			{
				return showInfo("Sorry", "You don't have enough items:<br>You need: <font color=LEVEL>" + formatAdena(Config.HEAL_PRICE) + " " + getItemNameHtml(Config.CONSUMABLE_ID) + "!");
			}
			player.destroyItemByItemId(ItemProcessType.FEE, Config.CONSUMABLE_ID, Config.HEAL_PRICE, player, true);
		}
		
		final boolean petMode = isPetMode(player);
		if (petMode)
		{
			final Summon target = player.getSummon();
			if (target == null)
			{
				return showInfo("Info", "You can't use the Pet's options.<br>Summon your pet first!");
			}
			
			final double maxHp = ServitorShareConditions.getMaxServitorRecoverableHp(target);
			final double maxMp = ServitorShareConditions.getMaxServitorRecoverableMp(target);
			target.setCurrentHp(maxHp);
			target.setCurrentMp(maxMp);
			
			if (target instanceof Servitor)
			{
				final Servitor servitor = (Servitor) target;
				servitor.setLifeTimeRemaining(servitor.getLifeTimeRemaining() + servitor.getLifeTime());
				player.sendPacket(new SetSummonRemainTime(servitor.getLifeTime(), servitor.getLifeTimeRemaining()));
			}
			
			target.setTarget(target);
			target.broadcastPacket(new MagicSkillUse(target, SKILL_HEAL, 1, 1000, 0));
		}
		else
		{
			final PlayerStatus pcStatus = player.getStatus();
			final PlayerStat pcStat = player.getStat();
			pcStatus.setCurrentHp(pcStat.getMaxHp());
			pcStatus.setCurrentMp(pcStat.getMaxMp());
			pcStatus.setCurrentCp(pcStat.getMaxCp());
			player.setTarget(player);
			player.broadcastPacket(new MagicSkillUse(player, SKILL_HEAL, 1, 1000, 0));
		}
		
		return buildMainPage(player);
	}
	
	private String handleRemoveBuffs(Player player)
	{
		if (!Config.FREE_BUFFS)
		{
			if (player.getInventory().getInventoryItemCount(Config.CONSUMABLE_ID, -1) < Config.BUFF_REMOVE_PRICE)
			{
				return showInfo("Sorry", "You don't have enough items:<br>You need: <font color=LEVEL>" + formatAdena(Config.BUFF_REMOVE_PRICE) + " " + getItemNameHtml(Config.CONSUMABLE_ID) + "!");
			}
			player.destroyItemByItemId(ItemProcessType.FEE, Config.CONSUMABLE_ID, Config.BUFF_REMOVE_PRICE, player, true);
		}
		
		final boolean petMode = isPetMode(player);
		if (petMode)
		{
			if (player.getSummon() == null)
			{
				return showInfo("Info", "You can't use the Pet's options.<br>Summon your pet first!");
			}
			player.getSummon().stopAllEffects();
		}
		else
		{
			player.stopAllEffects();
		}
		
		return buildMainPage(player);
	}
	
	private String handleCastBuffSet(Player player)
	{
		if (!Config.FREE_BUFFS)
		{
			if (player.getInventory().getInventoryItemCount(Config.CONSUMABLE_ID, -1) < Config.BUFF_SET_PRICE)
			{
				return showInfo("Sorry", "You don't have enough items:<br>You need: <font color=LEVEL>" + formatAdena(Config.BUFF_SET_PRICE) + " " + getItemNameHtml(Config.CONSUMABLE_ID) + "!");
			}
			player.destroyItemByItemId(ItemProcessType.FEE, Config.CONSUMABLE_ID, Config.BUFF_SET_PRICE, player, true);
		}
		
		final boolean petMode = isPetMode(player);
		final int playerClass = player.isMageClass() ? 1 : 0;
		final List<int[]> buffSets = new ArrayList<>();
		
		try (Connection con = DatabaseFactory.getConnection())
		{
			final PreparedStatement ps = con.prepareStatement("SELECT buffId, buffLevel FROM npcbuffer_buff_list WHERE forClass IN (?, ?) ORDER BY id ASC");
			ps.setInt(1, petMode ? 0 : playerClass);
			ps.setString(2, "2");
			final ResultSet rs = ps.executeQuery();
			while (rs.next())
			{
				buffSets.add(new int[]
				{
					rs.getInt("buffId"),
					rs.getInt("buffLevel")
				});
			}
			rs.close();
			ps.close();
		}
		catch (SQLException e)
		{
			LOG.warning("BufferBoard castBuffSet error: " + e.getMessage());
		}
		
		if (petMode)
		{
			final Summon summon = player.getSummon();
			if (summon == null)
			{
				return showInfo("Info", "You can't use the Pet's options.<br>Summon your pet first!");
			}
			summon.setTarget(summon);
			summon.broadcastPacket(new MagicSkillUse(summon, SKILL_BUFF_1, 1, 1000, 0));
			summon.broadcastPacket(new MagicSkillUse(summon, SKILL_BUFF_2, 1, 1000, 0));
			applyBuffsDirect(summon, buffSets);
		}
		else
		{
			player.setTarget(player);
			player.broadcastPacket(new MagicSkillUse(player, SKILL_BUFF_1, 1, 1000, 0));
			player.broadcastPacket(new MagicSkillUse(player, SKILL_BUFF_2, 1, 1000, 0));
			applyBuffsDirect(player, buffSets);
		}
		
		return buildMainPage(player);
	}
	
	private String handleCastScheme(Player player, String schemeId)
	{
		final List<Integer> buffs = new ArrayList<>();
		final List<Integer> levels = new ArrayList<>();
		
		try (Connection con = DatabaseFactory.getConnection())
		{
			final PreparedStatement ps = con.prepareStatement("SELECT skill_id, skill_level FROM npcbuffer_scheme_contents WHERE scheme_id=? ORDER BY id");
			ps.setString(1, schemeId);
			final ResultSet rs = ps.executeQuery();
			while (rs.next())
			{
				final int id = rs.getInt("skill_id");
				final int level = rs.getInt("skill_level");
				final String type = getBuffType(id);
				
				if (isBuffTypeEnabled(type) && isEnabled(id, level))
				{
					buffs.add(id);
					levels.add(level);
				}
			}
			rs.close();
			ps.close();
		}
		catch (SQLException e)
		{
			LOG.warning("BufferBoard castScheme error: " + e.getMessage());
		}
		
		if (buffs.isEmpty())
		{
			return viewSchemeBuffs(schemeId, "1", "add");
		}
		
		if (!Config.FREE_BUFFS)
		{
			if (player.getInventory().getInventoryItemCount(Config.CONSUMABLE_ID, -1) < Config.SCHEME_BUFF_PRICE)
			{
				return showInfo("Sorry", "You don't have enough items:<br>You need: <font color=LEVEL>" + formatAdena(Config.SCHEME_BUFF_PRICE) + " " + getItemNameHtml(Config.CONSUMABLE_ID) + "!");
			}
			player.destroyItemByItemId(ItemProcessType.FEE, Config.CONSUMABLE_ID, Config.SCHEME_BUFF_PRICE, player, true);
		}
		
		final List<int[]> buffList = new ArrayList<>(buffs.size());
		for (int i = 0; i < buffs.size(); i++)
		{
			buffList.add(new int[]
			{
				buffs.get(i),
				levels.get(i)
			});
		}
		
		final boolean petMode = isPetMode(player);
		if (petMode)
		{
			final Summon summon = player.getSummon();
			if (summon == null)
			{
				return showInfo("Info", "You can't use the Pet's options.<br>Summon your pet first!");
			}
			summon.setTarget(summon);
			summon.broadcastPacket(new MagicSkillUse(summon, SKILL_BUFF_1, 1, 1000, 0));
			summon.broadcastPacket(new MagicSkillUse(summon, SKILL_BUFF_2, 1, 1000, 0));
			applyBuffsDirect(summon, buffList);
		}
		else
		{
			player.setTarget(player);
			player.broadcastPacket(new MagicSkillUse(player, SKILL_BUFF_1, 1, 1000, 0));
			player.broadcastPacket(new MagicSkillUse(player, SKILL_BUFF_2, 1, 1000, 0));
			applyBuffsDirect(player, buffList);
		}
		
		return buildMainPage(player);
	}
	
	// =========================================================
	// SCHEME MANAGEMENT
	// =========================================================
	
	private String generateSchemeSection(Player player)
	{
		final List<String> schemeNames = new ArrayList<>();
		final List<String> schemeIds = new ArrayList<>();
		
		try (Connection con = DatabaseFactory.getConnection())
		{
			final PreparedStatement ps = con.prepareStatement("SELECT id, scheme_name FROM npcbuffer_scheme_list WHERE player_id=?");
			ps.setInt(1, player.getObjectId());
			final ResultSet rs = ps.executeQuery();
			while (rs.next())
			{
				schemeIds.add(rs.getString("id"));
				schemeNames.add(rs.getString("scheme_name"));
			}
			rs.close();
			ps.close();
		}
		catch (SQLException e)
		{
			LOG.warning("BufferBoard generateSchemeSection error: " + e.getMessage());
		}
		
		final StringBuilder html = new StringBuilder();
		html.append("<BR1><table width=100% border=0 cellspacing=0 cellpadding=1 bgcolor=444444><tr><td><font color=00FFFF>Scheme:</font></td>");
		if (!Config.FREE_BUFFS)
		{
			html.append("<td align=right><font color=LEVEL>").append(formatAdena(Config.SCHEME_BUFF_PRICE)).append("</font> adena</td>");
		}
		html.append("</tr></table><BR1>");
		
		if (!schemeNames.isEmpty())
		{
			html.append("<table cellspacing=0 cellpadding=0>");
			int td = 0;
			for (int i = 0; i < schemeNames.size(); i++)
			{
				if (td > 2)
				{
					td = 0;
				}
				html.append(gridCell(td, button(schemeNames.get(i), "_bbsbuffer;cast;" + schemeIds.get(i), 120)));
				td += 2;
			}
			html.append("</table>");
		}
		
		html.append("<BR1><table><tr>");
		if (schemeNames.size() < Config.SCHEMES_PER_PLAYER)
		{
			html.append("<td>").append(button("Create", "_bbsbuffer;create_1", 85)).append("</td>");
		}
		if (!schemeNames.isEmpty())
		{
			html.append("<td>").append(button("Edit", "_bbsbuffer;edit_1", 85)).append("</td>");
			html.append("<td>").append(button("Delete", "_bbsbuffer;delete_1", 85)).append("</td>");
		}
		html.append("</tr></table>");
		
		return html.toString();
	}
	
	private String createSchemeForm()
	{
		return "<html noscrollbar><title>" + TITLE + "</title><body><center>" + "<img src=\"L2UI_CH3.herotower_deco\" width=256 height=32><br><br>" + "You MUST separate new words with a dot (.)<br><br>" + "Scheme name: <edit var=\"sname\" width=120><br><br>" + button("Create Scheme", "_bbsbuffer;create;$sname", 130) + "<br>" + button("Back", "_bbsbuffer", 100) + "<br><font color=303030>" + TITLE + "</font></center></body></html>";
	}
	
	private String handleCreateScheme(Player player, String rawName)
	{
		final String name = rawName.replaceAll("[ !\"#$%&'()*+,/:;<=>?@\\[\\\\\\]\\^`{|}~]", "");
		if (name.isEmpty() || (name.length() > 36))
		{
			player.sendPacket(SystemMessageId.INCORRECT_NAME_PLEASE_TRY_AGAIN);
			return showInfo("Info", "Please enter a valid scheme name!<br>Max 36 characters, no special chars.");
		}
		
		try (Connection con = DatabaseFactory.getConnection())
		{
			final PreparedStatement ps = con.prepareStatement("INSERT INTO npcbuffer_scheme_list (player_id, scheme_name) VALUES (?, ?)");
			ps.setInt(1, player.getObjectId());
			ps.setString(2, name);
			ps.executeUpdate();
			ps.close();
		}
		catch (SQLException e)
		{
			LOG.warning("BufferBoard createScheme error: " + e.getMessage());
		}
		
		return buildMainPage(player);
	}
	
	private String editSchemeList(Player player)
	{
		final StringBuilder html = new StringBuilder();
		html.append("<html noscrollbar><title>").append(TITLE).append("</title><body><center>");
		html.append("<img src=\"L2UI_CH3.herotower_deco\" width=256 height=32><br>");
		html.append("Select a scheme to manage:<br><br>");
		
		try (Connection con = DatabaseFactory.getConnection())
		{
			final PreparedStatement ps = con.prepareStatement("SELECT id, scheme_name FROM npcbuffer_scheme_list WHERE player_id=?");
			ps.setInt(1, player.getObjectId());
			final ResultSet rs = ps.executeQuery();
			while (rs.next())
			{
				html.append(button(rs.getString("scheme_name"), "_bbsbuffer;manage;" + rs.getString("id"), 130));
			}
			rs.close();
			ps.close();
		}
		catch (SQLException e)
		{
			LOG.warning("BufferBoard editSchemeList error: " + e.getMessage());
		}
		
		html.append("<br>").append(button("Back", "_bbsbuffer", 100));
		html.append("<br><font color=303030>").append(TITLE).append("</font></center></body></html>");
		return html.toString();
	}
	
	private String deleteSchemeList(Player player)
	{
		final StringBuilder html = new StringBuilder();
		html.append("<html noscrollbar><title>").append(TITLE).append("</title><body><center>");
		html.append("<img src=\"L2UI_CH3.herotower_deco\" width=256 height=32><br>");
		html.append("Select a scheme to delete:<br><br>");
		
		try (Connection con = DatabaseFactory.getConnection())
		{
			final PreparedStatement ps = con.prepareStatement("SELECT id, scheme_name FROM npcbuffer_scheme_list WHERE player_id=?");
			ps.setInt(1, player.getObjectId());
			final ResultSet rs = ps.executeQuery();
			while (rs.next())
			{
				final String id = rs.getString("id");
				final String name = rs.getString("scheme_name");
				html.append(button(name, "_bbsbuffer;delete_c;" + id + ";" + name, 130));
			}
			rs.close();
			ps.close();
		}
		catch (SQLException e)
		{
			LOG.warning("BufferBoard deleteSchemeList error: " + e.getMessage());
		}
		
		html.append("<br>").append(button("Back", "_bbsbuffer", 100));
		html.append("<br><font color=303030>").append(TITLE).append("</font></center></body></html>");
		return html.toString();
	}
	
	private String confirmDeleteScheme(String id, String name)
	{
		return "<html noscrollbar><title>" + TITLE + "</title><body><center>" + "<img src=\"L2UI_CH3.herotower_deco\" width=256 height=32><br>" + "Do you really want to delete '<font color=LEVEL>" + name + "</font>'?<br><br>" + button("Yes", "_bbsbuffer;delete;" + id, 50) + button("No", "_bbsbuffer;delete_1", 50) + "<br><font color=303030>" + TITLE + "</font></center></body></html>";
	}
	
	private String handleDeleteScheme(Player player, String schemeId)
	{
		try (Connection con = DatabaseFactory.getConnection())
		{
			PreparedStatement ps = con.prepareStatement("DELETE FROM npcbuffer_scheme_list WHERE id=? LIMIT 1");
			ps.setString(1, schemeId);
			ps.executeUpdate();
			ps.close();
			
			ps = con.prepareStatement("DELETE FROM npcbuffer_scheme_contents WHERE scheme_id=?");
			ps.setString(1, schemeId);
			ps.executeUpdate();
			ps.close();
		}
		catch (SQLException e)
		{
			LOG.warning("BufferBoard deleteScheme error: " + e.getMessage());
		}
		
		return buildMainPage(player);
	}
	
	private String getSchemeOptions(String schemeId)
	{
		final int buffCount = getBuffCount(schemeId);
		final StringBuilder html = new StringBuilder();
		html.append("<html noscrollbar><title>").append(TITLE).append("</title><body><center>");
		html.append("<img src=\"L2UI_CH3.herotower_deco\" width=256 height=32><br>");
		html.append("There are <font color=LEVEL>").append(buffCount).append("</font> buffs in this scheme!<br><br>");
		
		if (buffCount < (MAX_SCHEME_BUFFS + MAX_SCHEME_DANCES))
		{
			html.append(button("Add buffs", "_bbsbuffer;addView;" + schemeId + ";1", 130));
		}
		if (buffCount > 0)
		{
			html.append(button("Remove buffs", "_bbsbuffer;removeView;" + schemeId + ";1", 130));
		}
		
		html.append("<br>").append(button("Back", "_bbsbuffer;edit_1", 100));
		html.append(button("Home", "_bbsbuffer", 100));
		html.append("<br><font color=303030>").append(TITLE).append("</font></center></body></html>");
		return html.toString();
	}
	
	// =========================================================
	// SCHEME BUFF ADD/REMOVE VIEW (PAGINATED)
	// =========================================================
	
	private String viewSchemeBuffs(String scheme, String page, String mode)
	{
		final List<String> buffList = new ArrayList<>();
		final StringBuilder html = new StringBuilder();
		html.append("<html noscrollbar><title>").append(TITLE).append("</title><body><center><br>");
		
		final String[] counts = getSchemeBuffCounts(scheme).split(" ");
		final int totalBuffs = Integer.parseInt(counts[0]);
		final int buffCount = Integer.parseInt(counts[1]);
		final int danceSongCount = Integer.parseInt(counts[2]);
		
		try (Connection con = DatabaseFactory.getConnection())
		{
			if ("add".equals(mode))
			{
				html.append("You can add <font color=LEVEL>").append(MAX_SCHEME_BUFFS - buffCount).append("</font> Buffs and <font color=LEVEL>").append(MAX_SCHEME_DANCES - danceSongCount).append("</font> Dances more!");
				
				final String typeQuery = generateQuery(buffCount, danceSongCount);
				if (typeQuery.isEmpty())
				{
					html.append("<br>No more buff slots available!");
					html.append("<br>").append(button("Back", "_bbsbuffer;manage;" + scheme, 100));
					html.append(button("Home", "_bbsbuffer", 100));
					html.append("<br><font color=303030>").append(TITLE).append("</font></center></body></html>");
					return html.toString();
				}
				
				final PreparedStatement ps = con.prepareStatement("SELECT * FROM npcbuffer_buff_list WHERE buffType IN (" + typeQuery + ") AND canUse=1 ORDER BY Buff_Class ASC, id");
				final ResultSet rs = ps.executeQuery();
				while (rs.next())
				{
					String name = SkillData.getInstance().getSkill(rs.getInt("buffId"), rs.getInt("buffLevel")).getName();
					name = name.replace(" ", "+");
					buffList.add(name + "_" + rs.getInt("buffId") + "_" + rs.getInt("buffLevel"));
				}
				rs.close();
				ps.close();
			}
			else
			{
				html.append("You have <font color=LEVEL>").append(buffCount).append("</font> Buffs and <font color=LEVEL>").append(danceSongCount).append("</font> Dances");
				
				final PreparedStatement ps = con.prepareStatement("SELECT skill_id, skill_level FROM npcbuffer_scheme_contents WHERE scheme_id=? ORDER BY Buff_Class ASC, id");
				ps.setString(1, scheme);
				final ResultSet rs = ps.executeQuery();
				while (rs.next())
				{
					String name = SkillData.getInstance().getSkill(rs.getInt("skill_id"), rs.getInt("skill_level")).getName();
					name = name.replace(" ", "+");
					buffList.add(name + "_" + rs.getInt("skill_id") + "_" + rs.getInt("skill_level"));
				}
				rs.close();
				ps.close();
			}
		}
		catch (SQLException e)
		{
			LOG.warning("BufferBoard viewSchemeBuffs error: " + e.getMessage());
		}
		
		// Pagination
		final int pageCount = Math.max(1, ((buffList.size() - 1) / BUFFS_PER_PAGE) + 1);
		final int currentPage = Integer.parseInt(page);
		final String pageName = pageCount > 5 ? "P" : "Page ";
		final String width = pageCount > 5 ? "25" : "50";
		
		html.append("<BR1><table border=0><tr>");
		for (int i = 1; i <= pageCount; i++)
		{
			if (i == currentPage)
			{
				html.append("<td width=").append(width).append(" align=center><font color=LEVEL>").append(pageName).append(i).append("</font></td>");
			}
			else
			{
				final String viewCmd = "add".equals(mode) ? "addView" : "removeView";
				html.append("<td width=").append(width).append(">").append(button(pageName + i, "_bbsbuffer;" + viewCmd + ";" + scheme + ";" + i, Integer.parseInt(width))).append("</td>");
			}
		}
		html.append("</tr></table>");
		
		// Buff list
		final int start = Math.max(0, (BUFFS_PER_PAGE * currentPage) - BUFFS_PER_PAGE);
		final int end = Math.min(BUFFS_PER_PAGE * currentPage, buffList.size());
		int k = 0;
		
		for (int i = start; i < end; i++)
		{
			final String original = buffList.get(i);
			final String cleaned = original.replace("_", " ");
			final String[] parts = cleaned.split(" ");
			final String name = parts[0].replace("+", " ");
			final int id = Integer.parseInt(parts[1]);
			final int level = Integer.parseInt(parts[2]);
			
			if ("add".equals(mode) && isUsed(scheme, id, level))
			{
				continue;
			}
			
			final String bgColor = ((k % 2) != 0) ? "333333" : "292929";
			html.append("<BR1><table border=0 bgcolor=").append(bgColor).append(">");
			html.append("<tr><td width=35>").append(getSkillIconHtml(id, level)).append("</td><td fixwidth=170>").append(name).append("</td><td>");
			
			if ("add".equals(mode))
			{
				html.append(button("Add", "_bbsbuffer;addBuff;" + scheme + "_" + id + "_" + level + ";" + page + ";" + totalBuffs, 65));
			}
			else
			{
				html.append(button("Remove", "_bbsbuffer;removeBuff;" + scheme + "_" + id + "_" + level + ";" + page + ";" + totalBuffs, 65));
			}
			
			html.append("</td></tr></table>");
			k++;
		}
		
		html.append("<br><br>").append(button("Back", "_bbsbuffer;manage;" + scheme, 100));
		html.append(button("Home", "_bbsbuffer", 100));
		html.append("<br><font color=303030>").append(TITLE).append("</font></center></body></html>");
		return html.toString();
	}
	
	private String handleAddBuffToScheme(String data)
	{
		final String[] parts = data.split(";");
		final String[] buffParts = parts[0].split("_");
		final String scheme = buffParts[0];
		final String skill = buffParts[1];
		final String level = buffParts[2];
		final String page = parts.length > 1 ? parts[1] : "1";
		final int total = parts.length > 2 ? Integer.parseInt(parts[2]) : 0;
		
		final int buffClass = getClassBuff(skill);
		try (Connection con = DatabaseFactory.getConnection())
		{
			final PreparedStatement ps = con.prepareStatement("INSERT INTO npcbuffer_scheme_contents (scheme_id, skill_id, skill_level, buff_class) VALUES (?, ?, ?, ?)");
			ps.setString(1, scheme);
			ps.setString(2, skill);
			ps.setString(3, level);
			ps.setInt(4, buffClass);
			ps.executeUpdate();
			ps.close();
		}
		catch (SQLException e)
		{
			LOG.warning("BufferBoard addBuff error: " + e.getMessage());
		}
		
		if ((total + 1) >= (MAX_SCHEME_BUFFS + MAX_SCHEME_DANCES))
		{
			return getSchemeOptions(scheme);
		}
		return viewSchemeBuffs(scheme, page, "add");
	}
	
	private String handleRemoveBuffFromScheme(String data)
	{
		final String[] parts = data.split(";");
		final String[] buffParts = parts[0].split("_");
		final String scheme = buffParts[0];
		final String skill = buffParts[1];
		final String level = buffParts[2];
		final String page = parts.length > 1 ? parts[1] : "1";
		final int total = parts.length > 2 ? Integer.parseInt(parts[2]) : 0;
		
		try (Connection con = DatabaseFactory.getConnection())
		{
			final PreparedStatement ps = con.prepareStatement("DELETE FROM npcbuffer_scheme_contents WHERE scheme_id=? AND skill_id=? AND skill_level=? LIMIT 1");
			ps.setString(1, scheme);
			ps.setString(2, skill);
			ps.setString(3, level);
			ps.executeUpdate();
			ps.close();
		}
		catch (SQLException e)
		{
			LOG.warning("BufferBoard removeBuff error: " + e.getMessage());
		}
		
		if ((total - 1) <= 0)
		{
			return getSchemeOptions(scheme);
		}
		return viewSchemeBuffs(scheme, page, "remove");
	}
	
	// =========================================================
	// DATABASE HELPERS
	// =========================================================
	
	private int getBuffCount(String scheme)
	{
		int count = 0;
		try (Connection con = DatabaseFactory.getConnection())
		{
			final PreparedStatement ps = con.prepareStatement("SELECT COUNT(*) AS cnt FROM npcbuffer_scheme_contents WHERE scheme_id=?");
			ps.setString(1, scheme);
			final ResultSet rs = ps.executeQuery();
			if (rs.next())
			{
				count = rs.getInt("cnt");
			}
			rs.close();
			ps.close();
		}
		catch (SQLException e)
		{
			LOG.warning("BufferBoard getBuffCount error: " + e.getMessage());
		}
		return count;
	}
	
	/**
	 * Returns "total buffCount danceSongCount" for a scheme.
	 * @param scheme
	 * @return
	 */
	private String getSchemeBuffCounts(String scheme)
	{
		int total = 0;
		int buffCount = 0;
		int danceSongCount = 0;
		try (Connection con = DatabaseFactory.getConnection())
		{
			final PreparedStatement ps = con.prepareStatement("SELECT buff_class FROM npcbuffer_scheme_contents WHERE scheme_id=?");
			ps.setString(1, scheme);
			final ResultSet rs = ps.executeQuery();
			while (rs.next())
			{
				total++;
				final int val = rs.getInt("buff_class");
				if ((val == 1) || (val == 2))
				{
					danceSongCount++;
				}
				else
				{
					buffCount++;
				}
			}
			rs.close();
			ps.close();
		}
		catch (SQLException e)
		{
			LOG.warning("BufferBoard getSchemeBuffCounts error: " + e.getMessage());
		}
		return total + " " + buffCount + " " + danceSongCount;
	}
	
	private String getBuffType(int id)
	{
		String val = "none";
		try (Connection con = DatabaseFactory.getConnection())
		{
			final PreparedStatement ps = con.prepareStatement("SELECT buffType FROM npcbuffer_buff_list WHERE buffId=? LIMIT 1");
			ps.setInt(1, id);
			final ResultSet rs = ps.executeQuery();
			if (rs.next())
			{
				val = rs.getString("buffType");
			}
			rs.close();
			ps.close();
		}
		catch (SQLException e)
		{
			LOG.warning("BufferBoard getBuffType error: " + e.getMessage());
		}
		return val;
	}
	
	private boolean isEnabled(int id, int level)
	{
		boolean val = false;
		try (Connection con = DatabaseFactory.getConnection())
		{
			final PreparedStatement ps = con.prepareStatement("SELECT canUse FROM npcbuffer_buff_list WHERE buffId=? AND buffLevel=? LIMIT 1");
			ps.setInt(1, id);
			ps.setInt(2, level);
			final ResultSet rs = ps.executeQuery();
			if (rs.next())
			{
				val = "1".equals(rs.getString("canUse"));
			}
			rs.close();
			ps.close();
		}
		catch (SQLException e)
		{
			LOG.warning("BufferBoard isEnabled error: " + e.getMessage());
		}
		return val;
	}
	
	private boolean isUsed(String scheme, int id, int level)
	{
		boolean used = false;
		try (Connection con = DatabaseFactory.getConnection())
		{
			final PreparedStatement ps = con.prepareStatement("SELECT id FROM npcbuffer_scheme_contents WHERE scheme_id=? AND skill_id=? AND skill_level=? LIMIT 1");
			ps.setString(1, scheme);
			ps.setInt(2, id);
			ps.setInt(3, level);
			final ResultSet rs = ps.executeQuery();
			if (rs.next())
			{
				used = true;
			}
			rs.close();
			ps.close();
		}
		catch (SQLException e)
		{
			LOG.warning("BufferBoard isUsed error: " + e.getMessage());
		}
		return used;
	}
	
	private int getClassBuff(String id)
	{
		int val = 0;
		try (Connection con = DatabaseFactory.getConnection())
		{
			final PreparedStatement ps = con.prepareStatement("SELECT buff_class FROM npcbuffer_buff_list WHERE buffId=?");
			ps.setString(1, id);
			final ResultSet rs = ps.executeQuery();
			if (rs.next())
			{
				val = rs.getInt("buff_class");
			}
			rs.close();
			ps.close();
		}
		catch (SQLException e)
		{
			LOG.warning("BufferBoard getClassBuff error: " + e.getMessage());
		}
		return val;
	}
	
	private String generateQuery(int buffsCount, int dancesCount)
	{
		final StringBuilder query = new StringBuilder();
		if (Config.ENABLE_BUFFS && (buffsCount < MAX_SCHEME_BUFFS))
		{
			query.append(",\"buff\"");
		}
		if (Config.ENABLE_RESIST && (buffsCount < MAX_SCHEME_BUFFS))
		{
			query.append(",\"resist\"");
		}
		if (Config.ENABLE_SONGS && (dancesCount < MAX_SCHEME_DANCES))
		{
			query.append(",\"song\"");
		}
		if (Config.ENABLE_DANCES && (dancesCount < MAX_SCHEME_DANCES))
		{
			query.append(",\"dance\"");
		}
		if (Config.ENABLE_CHANTS && (buffsCount < MAX_SCHEME_BUFFS))
		{
			query.append(",\"chant\"");
		}
		if (Config.ENABLE_OTHERS && (buffsCount < MAX_SCHEME_BUFFS))
		{
			query.append(",\"others\"");
		}
		if (Config.ENABLE_SPECIAL && (buffsCount < MAX_SCHEME_BUFFS))
		{
			query.append(",\"special\"");
		}
		if (query.length() > 0)
		{
			query.deleteCharAt(0);
		}
		return query.toString();
	}
	
	// =========================================================
	// GM MANAGEMENT
	// =========================================================
	
	private String gmViewAllBuffTypes()
	{
		final StringBuilder html = new StringBuilder();
		html.append("<html noscrollbar><title>").append(TITLE).append("</title><body><center>");
		html.append("<img src=\"L2UI_CH3.herotower_deco\" width=256 height=32><br>");
		html.append("<font color=LEVEL>[GM Buff Management]</font><br>");
		
		if (Config.ENABLE_BUFFS)
		{
			html.append(button("Buffs", "_bbsbuffer;gmEditList;buff;Buffs;1", 118));
		}
		if (Config.ENABLE_RESIST)
		{
			html.append(button("Resist Buffs", "_bbsbuffer;gmEditList;resist;Resists;1", 118));
		}
		if (Config.ENABLE_SONGS)
		{
			html.append(button("Songs", "_bbsbuffer;gmEditList;song;Songs;1", 118));
		}
		if (Config.ENABLE_DANCES)
		{
			html.append(button("Dances", "_bbsbuffer;gmEditList;dance;Dances;1", 118));
		}
		if (Config.ENABLE_CHANTS)
		{
			html.append(button("Chants", "_bbsbuffer;gmEditList;chant;Chants;1", 118));
		}
		if (Config.ENABLE_SPECIAL)
		{
			html.append(button("Special Buffs", "_bbsbuffer;gmEditList;special;Special_Buffs;1", 118));
		}
		if (Config.ENABLE_OTHERS)
		{
			html.append(button("Others Buffs", "_bbsbuffer;gmEditList;others;Others_Buffs;1", 118));
		}
		if (Config.ENABLE_CUBIC)
		{
			html.append(button("Cubics", "_bbsbuffer;gmEditList;cubic;Cubics;1", 118));
		}
		if (Config.ENABLE_BUFF_SET)
		{
			html.append("<br1>").append(button("Buff Sets", "_bbsbuffer;gmEditList;set;Buff_Sets;1", 118));
		}
		
		html.append("<br>").append(button("Back", "_bbsbuffer", 100));
		html.append("<br><font color=303030>").append(TITLE).append("</font></center></body></html>");
		return html.toString();
	}
	
	private String gmViewAllBuffs(String type, String typeName, String page)
	{
		final List<String> buffList = new ArrayList<>();
		final StringBuilder html = new StringBuilder();
		html.append("<html noscrollbar><title>").append(TITLE).append("</title><body><center>");
		html.append("<img src=\"L2UI_CH3.herotower_deco\" width=256 height=32><br>");
		
		try (Connection con = DatabaseFactory.getConnection())
		{
			final PreparedStatement ps;
			if ("set".equals(type))
			{
				ps = con.prepareStatement("SELECT * FROM npcbuffer_buff_list WHERE buffType IN (" + generateQuery(0, 0) + ") AND canUse=1");
			}
			else
			{
				ps = con.prepareStatement("SELECT * FROM npcbuffer_buff_list WHERE buffType=?");
				ps.setString(1, type);
			}
			final ResultSet rs = ps.executeQuery();
			while (rs.next())
			{
				String name = SkillData.getInstance().getSkill(rs.getInt("buffId"), rs.getInt("buffLevel")).getName();
				name = name.replace(" ", "+");
				buffList.add(name + "_" + rs.getString("forClass") + "_" + page + "_" + rs.getString("canUse") + "_" + rs.getString("buffId") + "_" + rs.getString("buffLevel"));
			}
			rs.close();
			ps.close();
		}
		catch (SQLException e)
		{
			LOG.warning("BufferBoard gmViewAllBuffs error: " + e.getMessage());
		}
		Collections.sort(buffList);
		
		final int buffsPerPage = "set".equals(type) ? 12 : BUFFS_PER_PAGE;
		final int pageCount = Math.max(1, ((buffList.size() - 1) / buffsPerPage) + 1);
		final int currentPage = Integer.parseInt(page);
		final String pName = pageCount > 5 ? "P" : "Page ";
		final String pWidth = pageCount > 5 ? "25" : "50";
		
		html.append("<font color=LEVEL>[GM Management - ").append(typeName.replace("_", " ")).append(" - Page ").append(page).append("]</font><br><table border=0><tr>");
		for (int i = 1; i <= pageCount; i++)
		{
			if (i == currentPage)
			{
				html.append("<td width=").append(pWidth).append(" align=center><font color=LEVEL>").append(pName).append(i).append("</font></td>");
			}
			else
			{
				html.append("<td width=").append(pWidth).append(">").append(button(pName + i, "_bbsbuffer;gmEditList;" + type + ";" + typeName + ";" + i, Integer.parseInt(pWidth))).append("</td>");
			}
		}
		html.append("</tr></table><br>");
		
		final int start = Math.max(0, (buffsPerPage * currentPage) - buffsPerPage);
		final int end = Math.min(buffsPerPage * currentPage, buffList.size());
		
		for (int i = start; i < end; i++)
		{
			String value = buffList.get(i).replace("_", " ");
			final String[] extr = value.split(" ");
			final String name = extr[0].replace("+", " ");
			final int forClass = Integer.parseInt(extr[1]);
			final int usable = Integer.parseInt(extr[3]);
			final String skillPos = extr[4] + "_" + extr[5];
			
			final String bgColor = ((i % 2) != 0) ? "333333" : "292929";
			html.append("<BR1><table border=0 bgcolor=").append(bgColor).append(">");
			
			if ("set".equals(type))
			{
				final String listOrder;
				if (forClass == 0)
				{
					listOrder = "List=\"" + SET_FIGHTER + ";" + SET_MAGE + ";" + SET_ALL + ";" + SET_NONE + ";\"";
				}
				else if (forClass == 1)
				{
					listOrder = "List=\"" + SET_MAGE + ";" + SET_FIGHTER + ";" + SET_ALL + ";" + SET_NONE + ";\"";
				}
				else if (forClass == 2)
				{
					listOrder = "List=\"" + SET_ALL + ";" + SET_FIGHTER + ";" + SET_MAGE + ";" + SET_NONE + ";\"";
				}
				else
				{
					listOrder = "List=\"" + SET_NONE + ";" + SET_FIGHTER + ";" + SET_MAGE + ";" + SET_ALL + ";\"";
				}
				html.append("<tr><td fixwidth=145>").append(name).append("</td><td width=70><combobox var=\"newSet").append(i).append("\" width=70 ").append(listOrder).append("></td><td width=50>");
				html.append(button("Update", "_bbsbuffer;gmChangeSet;" + skillPos + ";$newSet" + i + ";" + page, 50));
				html.append("</td></tr>");
			}
			else
			{
				html.append("<tr><td fixwidth=170>").append(name).append("</td><td width=80>");
				if (usable == 1)
				{
					html.append(button("Disable", "_bbsbuffer;gmEditBuff;" + skillPos + ";0-" + page + ";" + type, 80));
				}
				else
				{
					html.append(button("Enable", "_bbsbuffer;gmEditBuff;" + skillPos + ";1-" + page + ";" + type, 80));
				}
				html.append("</td></tr>");
			}
			html.append("</table>");
		}
		
		html.append("<br><br>").append(button("Back", "_bbsbuffer;gmManage", 100));
		html.append(button("Home", "_bbsbuffer", 100));
		html.append("<br><font color=303030>").append(TITLE).append("</font></center></body></html>");
		return html.toString();
	}
	
	private void gmManageSelectedBuff(String buffPosId, String canUseBuff)
	{
		final String[] bpid = buffPosId.split("_");
		final String buffId = bpid[0];
		final String buffLevel = bpid[1];
		try (Connection con = DatabaseFactory.getConnection())
		{
			final PreparedStatement ps = con.prepareStatement("UPDATE npcbuffer_buff_list SET canUse=? WHERE buffId=? AND buffLevel=? LIMIT 1");
			ps.setString(1, canUseBuff);
			ps.setString(2, buffId);
			ps.setString(3, buffLevel);
			ps.executeUpdate();
			ps.close();
		}
		catch (SQLException e)
		{
			LOG.warning("BufferBoard gmManageSelectedBuff error: " + e.getMessage());
		}
	}
	
	private String gmManageSelectedSet(String id, String newVal, String page)
	{
		// Convert label to DB value
		String dbVal;
		if (SET_FIGHTER.equals(newVal))
		{
			dbVal = "0";
		}
		else if (SET_MAGE.equals(newVal))
		{
			dbVal = "1";
		}
		else if (SET_ALL.equals(newVal))
		{
			dbVal = "2";
		}
		else
		{
			dbVal = "3";
		}
		
		final String[] bpid = id.split("_");
		final String buffId = bpid[0];
		final String buffLevel = bpid[1];
		try (Connection con = DatabaseFactory.getConnection())
		{
			final PreparedStatement ps = con.prepareStatement("UPDATE npcbuffer_buff_list SET forClass=? WHERE buffId=? AND buffLevel=?");
			ps.setString(1, dbVal);
			ps.setString(2, buffId);
			ps.setString(3, buffLevel);
			ps.executeUpdate();
			ps.close();
		}
		catch (SQLException e)
		{
			LOG.warning("BufferBoard gmManageSelectedSet error: " + e.getMessage());
		}
		return gmViewAllBuffs("set", "Buff_Sets", page);
	}
	
	// =========================================================
	// BUFF APPLICATION
	// =========================================================
	
	private Skill getSkillCached(int skillId, int skillLevel)
	{
		final int cacheKey = (skillId * 10000) + skillLevel;
		return SKILL_CACHE.computeIfAbsent(cacheKey, k -> SkillData.getInstance().getSkill(skillId, skillLevel));
	}
	
	private void applyBuffsDirect(Creature target, List<int[]> buffs)
	{
		if ((buffs == null) || buffs.isEmpty() || (target == null))
		{
			return;
		}
		
		for (int[] buff : buffs)
		{
			final Skill skill = getSkillCached(buff[0], buff[1]);
			if (skill != null)
			{
				skill.applyEffects(target, target);
			}
		}
	}
	
	// =========================================================
	// CONFIG HELPERS
	// =========================================================
	
	private boolean isBuffTypeEnabled(String type)
	{
		switch (type)
		{
			case "buff":
				return Config.ENABLE_BUFFS;
			case "resist":
				return Config.ENABLE_RESIST;
			case "song":
				return Config.ENABLE_SONGS;
			case "dance":
				return Config.ENABLE_DANCES;
			case "chant":
				return Config.ENABLE_CHANTS;
			case "others":
				return Config.ENABLE_OTHERS;
			case "special":
				return Config.ENABLE_SPECIAL;
			case "cubic":
				return Config.ENABLE_CUBIC;
			default:
				return false;
		}
	}
	
	private int getCategoryPrice(String buffType)
	{
		switch (buffType)
		{
			case "buff":
				return Config.BUFF_PRICE;
			case "resist":
				return Config.RESIST_PRICE;
			case "song":
				return Config.SONG_PRICE;
			case "dance":
				return Config.DANCE_PRICE;
			case "chant":
				return Config.CHANT_PRICE;
			case "others":
				return Config.OTHERS_PRICE;
			case "special":
				return Config.SPECIAL_PRICE;
			case "cubic":
				return Config.CUBIC_PRICE;
			default:
				return 0;
		}
	}
	
	// =========================================================
	// HTML UTILITY
	// =========================================================
	
	private boolean isPetMode(Player player)
	{
		return PET_MODE.getOrDefault(player.getObjectId(), false);
	}
	
	private void sendHtml(Player player, String html)
	{
		CommunityBoardHandler.separateAndSend(html, player);
	}
	
	private String button(String label, String bypass, int width)
	{
		return "<button value=\"" + label + "\" action=\"bypass " + bypass + "\" width=" + width + " height=25 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">";
	}
	
	private String gridCell(int td, String content)
	{
		return TRS[td] + content + TRS[td + 1];
	}
	
	private String showInfo(String title, String message)
	{
		return "<html noscrollbar><title>" + TITLE + "</title><body><center>" + "<img src=\"L2UI_CH3.herotower_deco\" width=256 height=32><br>" + "<font color=LEVEL>" + title + "</font><br>" + message + "<br><br>" + button("Back", "_bbsbuffer", 100) + "<br><font color=303030>" + TITLE + "</font></center></body></html>";
	}
	
	private String getItemNameHtml(int itemId)
	{
		return "&#" + itemId + ";";
	}
	
	private String getSkillIconHtml(int id, int level)
	{
		return "<img src=\"Icon.skill" + getSkillIconNumber(id, level) + "\" width=32 height=32>";
	}
	
	private String getSkillIconNumber(int id, int level)
	{
		if (id == 4)
		{
			return "0004";
		}
		if ((id > 9) && (id < 100))
		{
			return "00" + id;
		}
		if ((id > 99) && (id < 1000))
		{
			return "0" + id;
		}
		if (id == 1517)
		{
			return "1536";
		}
		if (id == 1518)
		{
			return "1537";
		}
		if (id == 1547)
		{
			return "0065";
		}
		if (id == 2076)
		{
			return "0195";
		}
		if ((id > 4550) && (id < 4555))
		{
			return "5739";
		}
		if ((id > 4698) && (id < 4701))
		{
			return "1331";
		}
		if ((id > 4701) && (id < 4704))
		{
			return "1332";
		}
		if (id == 6049)
		{
			return "0094";
		}
		return String.valueOf(id);
	}
}
