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
package handlers.voicedcommandhandlers;

import com.l2journey.Config;
import com.l2journey.gameserver.handler.IVoicedCommandHandler;
import com.l2journey.gameserver.model.actor.Player;
import com.l2journey.gameserver.model.variables.Statistics;
import com.l2journey.gameserver.network.serverpackets.NpcHtmlMessage;

/**
 * @author KingHanker
 */
public class Panel implements IVoicedCommandHandler
{
	private static final String[] VOICED_COMMANDS =
	{
		"panel",
		"changeexp",
		"enchantanime",
		"tradeprot"
	};
	
	@Override
	public boolean useVoicedCommand(String command, Player activeChar, String target)
	{
		switch (command)
		{
			case "panel":
				break;
			case "tradeprot":
				if (activeChar.getVarB("noTrade"))
				{
					activeChar.setVar("noTrade", "false");
					activeChar.sendMessage("Block Trade: Disabled.");
				}
				else
				{
					activeChar.setVar("noTrade", "true");
					activeChar.sendMessage("Block Trade: Enabled.");
				}
				break;
			case "changeexp":
				if (!activeChar.getVarB("noExp"))
				{
					activeChar.setVar("noExp", "true");
					activeChar.sendMessage("Block Experience: Enable.");
				}
				else
				{
					activeChar.setVar("noExp", "false");
					activeChar.sendMessage("Block Experience: Disabled.");
				}
				break;
			case "enchantanime":
				if (activeChar.getVarB("showEnchantAnime"))
				{
					activeChar.setVar("showEnchantAnime", "false");
					activeChar.sendMessage("Enchant Animation: Disabled.");
				}
				else
				{
					activeChar.setVar("showEnchantAnime", "true");
					activeChar.sendMessage("Enchant Animation: Enabled.");
				}
				break;
		}
		sendHtml(activeChar);
		
		return true;
	}
	
	public static void sendHtml(Player player)
	{
		StringBuilder builder = new StringBuilder();
		NpcHtmlMessage html = new NpcHtmlMessage();
		
		builder.append("<html noscrollbar><title>User Panel</title><body>");
		builder.append("<table width=245 height=135>");
		builder.append("<tr><td valign=\"top\" align=\"center\">");
		builder.append("<table>");
		builder.append("<tr><td><center>");
		builder.append("<table width=245 height=135>");
		builder.append("<tr><td width=250 height=3 align=center><img src=\"L2UI.SquareBlank\"></td></tr>");
		builder.append("<tr><td><table width=265 background=\"l2ui_ct1.ComboBox_DF_Dropmenu_Bg\"><tr>");
		builder.append("<td width=110 align=\"center\"><font name=hs8 name=CreditTextNormal color=ae9977>Options</font></td>");
		builder.append("<td width=60 align=\"center\"><font name=hs8 name=CreditTextNormal color=ae9977>Action</font></td>");
		builder.append("<td width=60 align=\"center\"><font name=hs8 name=CreditTextSmall color=ae9977>Status</font></td></tr>");
		builder.append("</table></td></tr>");
		builder.append("<tr><td>");
		builder.append("<table border=0 width=270>");
		builder.append("<tr>");
		builder.append("<td width=110><font color=00FA9A>Block Trade</font></td>");
		builder.append("<td width=60><button value=\"Change\" action=\"bypass -h voice .tradeprot\" width=60 height=21 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_ct1.button_df\"></td>");
		if (player.getVarB("noTrade"))
		{
			builder.append("<td width=60 align=center valign=center>");
			builder.append("<table cellpadding=0 cellspacing=0>");
			builder.append("<tr><td height=10></td></tr>");
			builder.append("<tr>");
			builder.append("<td width=4 valign=top><img src=\"L2UI_CT1.Gauge_DF_Large_MP_Left\" width=4 height=14></td>");
			builder.append("<td width=52><img src=\"L2UI_CT1.Gauge_DF_Large_MP_Center\" width=52 height=14></td>");
			builder.append("<td width=4 valign=top><img src=\"L2UI_CT1.Gauge_DF_Large_MP_Right\" width=4 height=14></td>");
			builder.append("</tr>");
			builder.append("</table>");
			builder.append("</td></tr>");
		}
		else
		{
			builder.append("<td width=70 align=center valign=center>");
			builder.append("<table cellpadding=0 cellspacing=0>");
			builder.append("<tr><td height=10></td></tr>");
			builder.append("<tr>");
			builder.append("<td width=4 valign=top><img src=\"L2UI_CT1.Gauge_DF_Large_HP_Left\" width=4 height=14></td>");
			builder.append("<td width=52><img src=\"L2UI_CT1.Gauge_DF_Large_HP_Center\" width=52 height=14></td>");
			builder.append("<td width=4 valign=top><img src=\"L2UI_CT1.Gauge_DF_Large_HP_Right\" width=4 height=14></td>");
			builder.append("</tr>");
			builder.append("</table>");
			builder.append("</td></tr>");
		}
		
		builder.append("<tr>");
		builder.append("<td width=110><font color=00FA9A>Block Experience</font></td>");
		builder.append("<td width=60><button value=\"Change\" action=\"bypass -h voice .changeexp\" width=60 height=21 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_ct1.button_df\"></td>");
		if (player.getVarB("noExp"))
		{
			builder.append("<td width=70 align=center valign=center>");
			builder.append("<table cellpadding=0 cellspacing=0>");
			builder.append("<tr><td height=10></td></tr>");
			builder.append("<tr>");
			builder.append("<td width=4 valign=top><img src=\"L2UI_CT1.Gauge_DF_Large_MP_Left\" width=4 height=14></td>");
			builder.append("<td width=52><img src=\"L2UI_CT1.Gauge_DF_Large_MP_Center\" width=52 height=14></td>");
			builder.append("<td width=4 valign=top><img src=\"L2UI_CT1.Gauge_DF_Large_MP_Right\" width=4 height=14></td>");
			builder.append("</tr>");
			builder.append("</table>");
			builder.append("</td></tr>");
		}
		else
		{
			builder.append("<td width=70 align=center valign=center>");
			builder.append("<table cellpadding=0 cellspacing=0>");
			builder.append("<tr><td height=10></td></tr>");
			builder.append("<tr>");
			builder.append("<td width=4 valign=top><img src=\"L2UI_CT1.Gauge_DF_Large_HP_Left\" width=4 height=14></td>");
			builder.append("<td width=52><img src=\"L2UI_CT1.Gauge_DF_Large_HP_Center\" width=52 height=14></td>");
			builder.append("<td width=4 valign=top><img src=\"L2UI_CT1.Gauge_DF_Large_HP_Right\" width=4 height=14></td>");
			builder.append("</tr>");
			builder.append("</table>");
			builder.append("</td></tr>");
		}
		
		builder.append("<tr>");
		builder.append("<td width=110><font color=00FA9A>Enchant Animation</font></td>");
		builder.append("<td width=60><button value=\"Change\" action=\"bypass -h voice .enchantanime\" width=60 height=21 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_ct1.button_df\"></td>");
		if (player.getVarB("showEnchantAnime"))
		{
			builder.append("<td width=70 align=center valign=center>");
			builder.append("<table cellpadding=0 cellspacing=0>");
			builder.append("<tr><td height=10></td></tr>");
			builder.append("<tr>");
			builder.append("<td width=4 valign=top><img src=\"L2UI_CT1.Gauge_DF_Large_MP_Left\" width=4 height=14></td>");
			builder.append("<td width=52><img src=\"L2UI_CT1.Gauge_DF_Large_MP_Center\" width=52 height=14></td>");
			builder.append("<td width=4 valign=top><img src=\"L2UI_CT1.Gauge_DF_Large_MP_Right\" width=4 height=14></td>");
			builder.append("</tr>");
			builder.append("</table>");
			builder.append("</td></tr>");
		}
		else
		{
			builder.append("<td width=70 align=center valign=center>");
			builder.append("<table cellpadding=0 cellspacing=0>");
			builder.append("<tr><td height=10></td></tr>");
			builder.append("<tr>");
			builder.append("<td width=4 valign=top><img src=\"L2UI_CT1.Gauge_DF_Large_HP_Left\" width=4 height=14></td>");
			builder.append("<td width=52><img src=\"L2UI_CT1.Gauge_DF_Large_HP_Center\" width=52 height=14></td>");
			builder.append("<td width=4 valign=top><img src=\"L2UI_CT1.Gauge_DF_Large_HP_Right\" width=4 height=14></td>");
			builder.append("</tr>");
			builder.append("</table>");
			builder.append("</td></tr>");
		}
		
		builder.append("</table>"); // Fechamento da tabela com opcoes ativaveis
		
		builder.append("<br1>");
		
		if (Config.EPIC_COMMAND) // comando .epic
		{
			builder.append("<center>");
			builder.append("<table width=260>");
			builder.append("<tr>");
			builder.append("<td width=195 ><font color=00FA9A>Boss Status</font></td>");
			builder.append("<td width=30 align=\"center\"><button value=\"View\" action=\"bypass -h voice .epic\" width=70 height=21 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_ct1.button_df\"></td>");
			builder.append("<td></td>");
			builder.append("</tr>");
			builder.append("</table>");
			builder.append("<br1>");
		}
		
		if (Config.ENABLE_AUTO_PLAY) // comando .play
		{
			builder.append("<center>");
			builder.append("<table width=260>");
			builder.append("<tr>");
			builder.append("<td width=195 ><font color=00FA9A>Auto Play</font></td>");
			builder.append("<td width=30 align=\"center\"><button value=\"Start\" action=\"bypass -h voice .play\" width=70 height=21 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_ct1.button_df\"></td>");
			builder.append("<td></td>");
			builder.append("</tr>");
			builder.append("</table>");
			builder.append("<br1>");
		}
		
		if (Config.COMBINETALISMANS_COMMAND) // comando .combinetalismans
		{
			builder.append("<center>");
			builder.append("<table width=260>");
			builder.append("<tr>");
			builder.append("<td width=195 ><font color=00FA9A>Combine Talismans</font></td>");
			builder.append("<td width=30 align=\"center\"><button value=\"Combine\" action=\"bypass -h voice .combinetalismans\" width=70 height=21 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_ct1.button_df\"></td>");
			builder.append("<td></td>");
			builder.append("</tr>");
			builder.append("</table>");
			builder.append("<br1>");
		}
		
		if (Config.DRESSME_ENABLE) // comando .dressme
		{
			builder.append("<center>");
			builder.append("<table width=260>");
			builder.append("<tr>");
			builder.append("<td width=195 ><font color=00FA9A>Visual system</font></td>");
			builder.append("<td width=30 align=\"center\"><button value=\"DressMe\" action=\"bypass -h voice .dressme\" width=70 height=21 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_ct1.button_df\"></td>");
			builder.append("<td></td>");
			builder.append("</tr>");
			builder.append("</table>");
			builder.append("<br1>");
		}
		
		if (Config.ALLOW_CHANGE_PASSWORD) // comando .changepassword
		{
			builder.append("<center>");
			builder.append("<table width=260>");
			builder.append("<tr>");
			builder.append("<td width=195 ><font color=00FA9A>Change Password</font></td>");
			builder.append("<td width=30 align=\"center\"><button value=\"Change\" action=\"bypass -h voice .changepassword\" width=70 height=21 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_ct1.button_df\"></td>");
			builder.append("<td></td>");
			builder.append("</tr>");
			builder.append("</table>");
			builder.append("<br1>");
		}
		
		if (Config.MULTILANG_ENABLE) // Multilingual
		{
			builder.append("<center>");
			builder.append("<table width=275 height=23>");
			builder.append("<tr>");
			builder.append("<td background=\"l2ui_ct1.ComboBox_DF_Dropmenu_Bg\" align=\"center\" ><font name=hs11 name=CreditTextNormal color=B59A75>Multilingual Support</font></td>");
			builder.append("</tr>");
			builder.append("</table>");
			
			builder.append("<table width=260>");
			builder.append("<tr>");
			builder.append("<td width=70 ><font color=00FA9A>Language</font></td>");
			builder.append("<td width=122 align=\"center\"><font color=FFE4E1>" + player.getLang() + "</font></td>");
			builder.append("<td width=1 align=\"center\"><button value=\"Change\" action=\"bypass -h voice .lang\" width=70 height=21 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_ct1.button_df\"></td>");
			builder.append("<td></td>");
			builder.append("</tr>");
			builder.append("</table>");
		}
		
		builder.append("<br1>");
		
		if (Config.STATISTIC_PANEL) // Estatisticas do Servidor
		{
			builder.append("<table width=280 height=23>");
			builder.append("<tr>");
			builder.append("<td background=\"l2ui_ct1.ComboBox_DF_Dropmenu_Bg\" align=center><font name=hs11 name=CreditTextNormal color=B59A75>Server Statistics</font></td>");
			builder.append("</tr>");
			builder.append("</table>");
			
			builder.append("<table width=280>");
			builder.append("<tr>");
			builder.append("<td width=15></td>");
			builder.append("<td></td>");
			builder.append("<td></td>");
			builder.append("</tr>");
			builder.append("<tr>");
			builder.append("<td width=11></td>");
			builder.append("<td width=274><font color=00FA9A>Players Online</font></td>");
			builder.append("<td width=470 align=\"center\"><font color=FFE4E1>" + Statistics.getTotalPlayersOn() + "</font></td>");
			builder.append("</tr>");
			builder.append("<tr>");
			builder.append("<td width=11></td>");
			builder.append("<td width=274><font color=00FA9A>Active Players</font></td>");
			builder.append("<td width=470 align=\"center\"><font color=FFE4E1>" + Statistics.getRealOnline() + "</font></td>");
			builder.append("</tr>");
			builder.append("<tr>");
			builder.append("<td>");
			builder.append("<td width=11></td>");
			builder.append("<td width=274><font color=00FA9A>Offline Shop</font></td>");
			builder.append("<td width=274 align=\"center\"><font color=FFE4E1>" + Statistics.getOffShops() + "</font></td>");
			builder.append("</tr><tr><td height=5></td></tr></table>");
		}
		
		builder.append("</td></tr>");
		builder.append("</table>"); // Fechamento final da tabela
		builder.append("</body></html>");
		html.setHtml(builder.toString());
		player.sendPacket(html);
	}
	
	@Override
	public String[] getVoicedCommandList()
	{
		return VOICED_COMMANDS;
	}
}
