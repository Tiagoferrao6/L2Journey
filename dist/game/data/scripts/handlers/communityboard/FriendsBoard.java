/*
 * This file is part of the L2J Mobius project.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package handlers.communityboard;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.l2journey.commons.database.DatabaseFactory;
import com.l2journey.gameserver.cache.HtmCache;
import com.l2journey.gameserver.data.sql.CharInfoTable;
import com.l2journey.gameserver.handler.CommunityBoardHandler;
import com.l2journey.gameserver.handler.IWriteBoardHandler;
import com.l2journey.gameserver.model.BlockList;
import com.l2journey.gameserver.model.World;
import com.l2journey.gameserver.model.actor.Player;
import com.l2journey.gameserver.network.serverpackets.FriendPacket;

/**
 * Friends board.
 * @author Zoey76, L2Journey
 */
public class FriendsBoard implements IWriteBoardHandler
{
	private static final Logger LOGGER = Logger.getLogger(FriendsBoard.class.getName());
	
	private static final String[] COMMANDS =
	{
		"_bbsfriends",
		"_friendlist",
		"_friendblocklist",
		"_frienddelete",
		"_frienddeleteall",
		"_friendblockdelete",
		"_friendblockdeleteall",
		"_friendblockadd"
	};
	
	@Override
	public String[] getCommunityBoardCommands()
	{
		return COMMANDS;
	}
	
	@Override
	public boolean parseCommunityBoardCommand(String command, Player player)
	{
		if (command.equals("_bbsfriends") || command.equals("_friendlist"))
		{
			showFriendList(player, "");
		}
		else if (command.equals("_friendblocklist"))
		{
			showBlockList(player, "");
		}
		else if (command.equals("_frienddeleteall"))
		{
			final int count = deleteAllFriends(player);
			showFriendList(player, count + " friend(s) removed from your list.");
		}
		else if (command.startsWith("_frienddelete_"))
		{
			final String name = command.substring("_frienddelete_".length());
			deleteFriend(player, name);
			showFriendList(player, name + " has been removed from your friends list.");
		}
		else if (command.equals("_friendblockdeleteall"))
		{
			deleteAllBlocked(player);
			showBlockList(player, "All players removed from your ignore list.");
		}
		else if (command.startsWith("_friendblockdelete_"))
		{
			final String name = command.substring("_friendblockdelete_".length());
			final int targetId = CharInfoTable.getInstance().getIdByName(name);
			if (targetId > 0)
			{
				BlockList.removeFromBlockList(player, targetId);
			}
			showBlockList(player, name + " has been removed from your ignore list.");
		}
		return true;
	}
	
	@Override
	public boolean writeCommunityBoardCommand(Player player, String arg1, String arg2, String arg3, String arg4, String arg5)
	{
		// arg1 = value of <edit var="block"> — the name to block
		final String name = (arg1 == null) ? "" : arg1.trim();
		if (name.isEmpty())
		{
			showBlockList(player, "Please enter a character name.");
			return false;
		}
		
		final int targetId = CharInfoTable.getInstance().getIdByName(name);
		if (targetId <= 0)
		{
			showBlockList(player, "Player '" + name + "' was not found.");
			return false;
		}
		
		if (targetId == player.getObjectId())
		{
			showBlockList(player, "You cannot add yourself to the ignore list.");
			return false;
		}
		
		BlockList.addToBlockList(player, targetId);
		showBlockList(player, name + " has been added to your ignore list.");
		return true;
	}
	
	// -------------------------------------------------------------------------
	// Friend list page
	// -------------------------------------------------------------------------
	
	private void showFriendList(Player player, String message)
	{
		CommunityBoardHandler.getInstance().addBypass(player, "Friends List", "_friendlist");
		
		String html = HtmCache.getInstance().getHtm(player, "data/html/CommunityBoard/friends_list.html");
		
		final StringBuilder rows = new StringBuilder();
		for (int id : player.getFriendList())
		{
			final String name = CharInfoTable.getInstance().getNameById(id);
			if (name == null)
			{
				continue;
			}
			
			final Player online = World.getInstance().getPlayer(id);
			final boolean isOnline = (online != null) && online.isOnline();
			
			rows.append("<table border=\"0\" cellspacing=\"0\" cellpadding=\"3\" width=\"755\">");
			rows.append("<tr>");
			rows.append("<td width=\"500\"><font color=\"");
			rows.append(isOnline ? "00AA00" : "999999");
			rows.append("\">");
			rows.append(name);
			rows.append("</font>&nbsp;");
			rows.append(isOnline ? "(Online)" : "(Offline)");
			rows.append("</td>");
			rows.append("<td width=\"100\" align=\"right\">");
			rows.append("<a action=\"bypass _frienddelete_").append(name).append("\">[Remove]</a>");
			rows.append("</td>");
			rows.append("</tr></table>");
		}
		
		if (rows.length() == 0)
		{
			rows.append("<font color=\"999999\">Your friends list is empty.</font>");
		}
		
		html = html.replace("%friend_list%", rows.toString());
		html = html.replace("%delete_all_msg%", message.isEmpty() ? "" : "<font color=\"LEVEL\">" + message + "</font>");
		
		CommunityBoardHandler.separateAndSend(html, player);
	}
	
	private void deleteFriend(Player player, String friendName)
	{
		final int id = CharInfoTable.getInstance().getIdByName(friendName);
		if ((id <= 0) || !player.getFriendList().contains(id))
		{
			return;
		}
		
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement st = con.prepareStatement("DELETE FROM character_friends WHERE (charId=? AND friendId=?) OR (charId=? AND friendId=?)"))
		{
			st.setInt(1, player.getObjectId());
			st.setInt(2, id);
			st.setInt(3, id);
			st.setInt(4, player.getObjectId());
			st.execute();
		}
		catch (Exception e)
		{
			LOGGER.log(Level.WARNING, "FriendsBoard: error deleting friend " + friendName, e);
			return;
		}
		
		player.getFriendList().remove(Integer.valueOf(id));
		player.sendPacket(new FriendPacket(false, id));
		
		final Player target = World.getInstance().getPlayer(id);
		if (target != null)
		{
			target.getFriendList().remove(Integer.valueOf(player.getObjectId()));
			target.sendPacket(new FriendPacket(false, player.getObjectId()));
		}
	}
	
	private int deleteAllFriends(Player player)
	{
		final Collection<Integer> copy = new ArrayList<>(player.getFriendList());
		int count = 0;
		for (int id : copy)
		{
			final String name = CharInfoTable.getInstance().getNameById(id);
			if (name != null)
			{
				deleteFriend(player, name);
				count++;
			}
		}
		return count;
	}
	
	// -------------------------------------------------------------------------
	// Block (ignore) list page
	// -------------------------------------------------------------------------
	
	private void showBlockList(Player player, String message)
	{
		CommunityBoardHandler.getInstance().addBypass(player, "Ignore List", "_friendblocklist");
		
		String html = HtmCache.getInstance().getHtm(player, "data/html/CommunityBoard/friends_block_list.html");
		
		final StringBuilder rows = new StringBuilder();
		final Set<Integer> blockedIds = loadBlockedIds(player);
		for (int id : blockedIds)
		{
			final String name = CharInfoTable.getInstance().getNameById(id);
			if (name == null)
			{
				continue;
			}
			
			rows.append("<table border=\"0\" cellspacing=\"0\" cellpadding=\"3\" width=\"755\">");
			rows.append("<tr>");
			rows.append("<td width=\"500\"><font color=\"999999\">").append(name).append("</font></td>");
			rows.append("<td width=\"100\" align=\"right\">");
			rows.append("<a action=\"bypass _friendblockdelete_").append(name).append("\">[Remove]</a>");
			rows.append("</td>");
			rows.append("</tr></table>");
		}
		
		if (rows.length() == 0)
		{
			rows.append("<font color=\"999999\">Your ignore list is empty.</font>");
		}
		
		html = html.replace("%block_list%", rows.toString());
		html = html.replace("%delete_all_msg%", message.isEmpty() ? "" : "<font color=\"LEVEL\">" + message + "</font>");
		
		CommunityBoardHandler.separateAndSend(html, player);
	}
	
	private void deleteAllBlocked(Player player)
	{
		final Set<Integer> copy = loadBlockedIds(player);
		for (int id : copy)
		{
			BlockList.removeFromBlockList(player, id);
		}
	}
	
	/**
	 * Loads the block (ignore) list IDs directly from the database. This avoids any dependency on the compiled BlockList class methods, allowing the script to compile without a full server rebuild.
	 * @param player
	 * @return
	 */
	private Set<Integer> loadBlockedIds(Player player)
	{
		final Set<Integer> ids = new HashSet<>();
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement st = con.prepareStatement("SELECT friendId FROM character_friends WHERE charId=? AND relation=1"))
		{
			st.setInt(1, player.getObjectId());
			try (ResultSet rs = st.executeQuery())
			{
				while (rs.next())
				{
					ids.add(rs.getInt("friendId"));
				}
			}
		}
		catch (Exception e)
		{
			LOGGER.log(Level.WARNING, "FriendsBoard: error loading block list for " + player.getName(), e);
		}
		return ids;
	}
}
