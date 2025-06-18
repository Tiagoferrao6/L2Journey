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
package handlers.bypasshandlers;

import java.util.logging.Level;

import com.l2journey.gameserver.handler.IBypassHandler;
import com.l2journey.gameserver.model.actor.Creature;
import com.l2journey.gameserver.model.actor.Npc;
import com.l2journey.gameserver.model.actor.Player;
import com.l2journey.gameserver.model.actor.instance.OlympiadManager;
import com.l2journey.gameserver.model.olympiad.Olympiad;
import com.l2journey.gameserver.model.olympiad.OlympiadGameManager;
import com.l2journey.gameserver.model.olympiad.OlympiadGameTask;
import com.l2journey.gameserver.network.SystemMessageId;
import com.l2journey.gameserver.network.serverpackets.ExOlympiadMatchList;

/**
 * @author DS
 */
public class OlympiadObservation implements IBypassHandler
{
	private static final String[] COMMANDS =
	{
		"watchmatch",
		"arenachange"
	};
	
	@Override
	public boolean useBypass(String command, Player player, Creature target)
	{
		try
		{
			final Npc olymanager = player.getLastFolkNPC();
			if (command.startsWith(COMMANDS[0])) // list
			{
				if (!Olympiad.getInstance().inCompPeriod())
				{
					player.sendPacket(SystemMessageId.THE_GRAND_OLYMPIAD_GAMES_ARE_NOT_CURRENTLY_IN_PROGRESS);
					return false;
				}
				
				player.sendPacket(new ExOlympiadMatchList());
			}
			else
			{
				if ((olymanager == null) || !(olymanager instanceof OlympiadManager))
				{
					return false;
				}
				
				if (!player.inObserverMode() && !player.isInsideRadius2D(olymanager, 300))
				{
					return false;
				}
				
				if (com.l2journey.gameserver.model.olympiad.OlympiadManager.getInstance().isRegisteredInComp(player))
				{
					player.sendPacket(SystemMessageId.YOU_MAY_NOT_OBSERVE_A_GRAND_OLYMPIAD_GAMES_MATCH_WHILE_YOU_ARE_ON_THE_WAITING_LIST);
					return false;
				}
				
				if (!Olympiad.getInstance().inCompPeriod())
				{
					player.sendPacket(SystemMessageId.THE_GRAND_OLYMPIAD_GAMES_ARE_NOT_CURRENTLY_IN_PROGRESS);
					return false;
				}
				
				if (player.isRegisteredOnEvent())
				{
					player.sendMessage("You can not observe games while registered on an event.");
					return false;
				}
				
				final int arenaId = Integer.parseInt(command.substring(12).trim());
				final OlympiadGameTask nextArena = OlympiadGameManager.getInstance().getOlympiadTask(arenaId);
				if (nextArena != null)
				{
					final int instanceId = OlympiadGameManager.getInstance().getOlympiadTask(arenaId).getZone().getInstanceId();
					player.enterOlympiadObserverMode(nextArena.getZone().getSpectatorSpawns().get(0), arenaId, instanceId);
				}
			}
			
			return true;
		}
		catch (Exception e)
		{
			LOGGER.log(Level.WARNING, "Exception in " + getClass().getSimpleName(), e);
		}
		
		return false;
	}
	
	@Override
	public String[] getBypassList()
	{
		return COMMANDS;
	}
}
