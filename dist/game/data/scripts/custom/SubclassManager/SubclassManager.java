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
package custom.SubclassManager;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.l2journey.gameserver.data.xml.CumulativeSubclassData;
import com.l2journey.gameserver.data.xml.ExperienceData;
import com.l2journey.gameserver.model.actor.Npc;
import com.l2journey.gameserver.model.actor.Player;
import com.l2journey.gameserver.model.actor.enums.player.PlayerClass;
import com.l2journey.gameserver.model.itemcontainer.Inventory;
import com.l2journey.gameserver.network.serverpackets.ItemList;
import com.l2journey.gameserver.network.serverpackets.NpcHtmlMessage;
import com.l2journey.gameserver.network.serverpackets.SkillList;
import com.l2journey.gameserver.network.serverpackets.UserInfo;
import com.l2journey.gameserver.util.Broadcast;

import ai.AbstractNpcAI;

/**
 * Custom Subclass Manager NPC script for Cumulative Subclass (Dual Class) system.
 */
public class SubclassManager extends AbstractNpcAI
{
	private static final int NPC_ID = 39900;
	
	public SubclassManager()
	{
		addStartNpc(NPC_ID);
		addTalkId(NPC_ID);
		addFirstTalkId(NPC_ID);
	}
	
	@Override
	public String onFirstTalk(Npc npc, Player player)
	{
		return showMainHtml(npc, player);
	}
	
	private String showMainHtml(Npc npc, Player player)
	{
		final CumulativeSubclassData config = CumulativeSubclassData.getInstance();
		if (!config.isEnabled())
		{
			final NpcHtmlMessage html = new NpcHtmlMessage(npc.getObjectId());
			html.setHtml("<html><body>Subclass Manager:<br><br><font color=\"LEVEL\">O sistema de Subclasse Acumulativa está temporariamente desativado.</font></body></html>");
			player.sendPacket(html);
			return null;
		}
		
		final StringBuilder sb = new StringBuilder();
		sb.append("<html><body>Subclass Manager:<br><br>");
		sb.append("Bem-vindo! Aqui você pode fundir sua classe ativa com uma segunda classe da mesma raça.<br><br>");
		
		if (player.getLevel() < config.getRequiredLevel())
		{
			sb.append("<font color=\"FF0000\">Você precisa ser pelo menos Nível ").append(config.getRequiredLevel()).append(" para fundir uma classe.</font>");
		}
		else if (getQuestItemsCount(player, config.getRequiredItemId()) < config.getRequiredItemCount())
		{
			sb.append("<font color=\"FF0000\">Você precisa possuir ").append(config.getRequiredItemCount()).append(" Golkonda's Horn (ID: ").append(config.getRequiredItemId()).append(") para fundir uma classe.</font>");
		}
		else
		{
			sb.append("Selecione a classe da raça <font color=\"LEVEL\">").append(player.getRace()).append("</font> para fusão:<br><br>");
			
			final PlayerClass activeClass = player.getPlayerClass();
			final List<PlayerClass> availableClasses = Arrays.stream(PlayerClass.values())
				.filter(c -> c.level() == 3) // Apenas 3ªs classes
				.filter(c -> !config.isSameRaceOnly() || c.getRace() == player.getRace())
				.filter(c -> c != activeClass)
				.filter(c -> c.getId() != player.getDualClassId())
				.collect(Collectors.toList());
			
			for (PlayerClass pClass : availableClasses)
			{
				sb.append("<a action=\"bypass -h Quest SubclassManager merge_class ").append(pClass.getId()).append("\">").append(pClass.name()).append("</a><br>");
			}
		}
		
		sb.append("</body></html>");
		
		final NpcHtmlMessage html = new NpcHtmlMessage(npc.getObjectId());
		html.setHtml(sb.toString());
		player.sendPacket(html);
		return null;
	}
	
	@Override
	public String onEvent(String event, Npc npc, Player player)
	{
		final CumulativeSubclassData config = CumulativeSubclassData.getInstance();
		if (!config.isEnabled())
		{
			player.sendMessage("O sistema de Subclasse Acumulativa está desativado.");
			return null;
		}
		
		if (event.startsWith("merge_class "))
		{
			final int selectedDualClassId = Integer.parseInt(event.replace("merge_class ", "").trim());
			final PlayerClass activeClass = player.getPlayerClass();
			final PlayerClass selectedDualClass = PlayerClass.getPlayerClass(selectedDualClassId);
			
			// Condição 1: Nível mínimo
			if (player.getLevel() < config.getRequiredLevel())
			{
				player.sendMessage("Você precisa ser Nível " + config.getRequiredLevel() + " para fundir a classe.");
				return showMainHtml(npc, player);
			}
			
			// Condição 2: Possuir item exigido
			if (getQuestItemsCount(player, config.getRequiredItemId()) < config.getRequiredItemCount())
			{
				player.sendMessage("Você não possui o item necessário (" + config.getRequiredItemCount() + " Golkonda's Horn).");
				return showMainHtml(npc, player);
			}
			
			// Condição 3: Validação de Mesma Raça
			if (selectedDualClass == null || (config.isSameRaceOnly() && activeClass.getRace() != selectedDualClass.getRace()))
			{
				player.sendMessage("A subclasse acumulativa deve pertencer à mesma raça (" + activeClass.getRace() + ") da sua classe ativa.");
				return showMainHtml(npc, player);
			}
			
			// Condição 4: Não permitir fundir com a mesma classe
			if (activeClass == selectedDualClass)
			{
				player.sendMessage("Você não pode fundir sua classe com ela mesma!");
				return showMainHtml(npc, player);
			}
			
			// Ação 1: Consumir item
			takeItems(player, config.getRequiredItemId(), config.getRequiredItemCount());
			
			// Ação 2: Gravar dual_class_id
			if (!player.setDualClassId(selectedDualClassId))
			{
				player.sendMessage("Erro ao registrar Subclasse Acumulativa.");
				return showMainHtml(npc, player);
			}
			
			// Ação 3 (Delevel): Ajustar nível e EXP para o valor base do nível de destino (ex: 40)
			final int targetLevel = config.getDelevelTargetLevel();
			final long targetExpLevel = ExperienceData.getInstance().getExpForLevel(targetLevel);
			player.getStat().setLevel((byte) targetLevel);
			player.getStat().setExp(targetExpLevel);
			
			// Ação 4: Desequipar todos os itens
			for (int slot = 0; slot < Inventory.PAPERDOLL_TOTALSLOTS; slot++)
			{
				player.getInventory().unEquipItemInSlot(slot);
			}
			
			// Ação 5: Atualizar cliente
			player.rewardSkills();
			player.sendPacket(new UserInfo(player));
			player.sendPacket(new SkillList());
			player.sendPacket(new ItemList(player, true));
			
			// Anúncio Global
			Broadcast.toAllOnlinePlayers("Parabéns ao jogador " + player.getName() + " por fundir sua classe e adquirir a Subclasse Acumulativa " + selectedDualClass.name() + "!");
			
			final NpcHtmlMessage html = new NpcHtmlMessage(npc.getObjectId());
			html.setHtml("<html><body>Subclass Manager:<br><br><font color=\"LEVEL\">Parabéns! Sua classe foi fundida com sucesso para Nível " + targetLevel + "!</font></body></html>");
			player.sendPacket(html);
			return null;
		}
		
		return super.onEvent(event, npc, player);
	}
	
	public static void main(String[] args)
	{
		new SubclassManager();
	}
}
