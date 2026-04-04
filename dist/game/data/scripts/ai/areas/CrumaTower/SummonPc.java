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
package ai.areas.CrumaTower;

import com.l2journey.gameserver.model.actor.Attackable;
import com.l2journey.gameserver.model.actor.Creature;
import com.l2journey.gameserver.model.actor.Npc;
import com.l2journey.gameserver.model.actor.Player;
import com.l2journey.gameserver.network.enums.ChatType;
import com.l2journey.gameserver.network.serverpackets.MagicSkillUse;
import com.l2journey.gameserver.network.serverpackets.NpcSay;

import ai.AbstractNpcAI;

/**
 * IA de Summon Pc.<br>
 * Puxa o jogador para o NPC ao atacar.
 * @author Zoey76, Mafias, KingHanker
 */
public class SummonPc extends AbstractNpcAI
{
	// NPCs
	private static final int PORTA = 20213;
	private static final int PERUM = 20221;
	// Timers
	private static final String CHECK_TIMER = "CHECK_TARGET";
	private static final String PULL_TIMER = "PULL_TARGET";
	// Visual skill
	private static final int VISUAL_SKILL = 1086;
	private static final int VISUAL_LEVEL = 2;
	private static final int CAST_TIME = 3000;
	// Config
	private static final int CHECK_INTERVAL = 1000;
	private static final int SUMMON_DISTANCE = 300;
	private static final int MELEE_DISTANCE = 200;
	private static final int SUMMON_COOLDOWN_MS = 10000;
	
	private static final String[] SUMMON_PHRASES =
	{
		"You cannot escape!",
		"Get over here!"
	};
	
	private SummonPc()
	{
		addSpawnId(PORTA, PERUM);
		addAttackId(PORTA, PERUM);
		addKillId(PORTA, PERUM);
	}
	
	@Override
	public void onSpawn(Npc npc)
	{
		npc.setScriptValue(0);
		startQuestTimer(CHECK_TIMER, CHECK_INTERVAL, npc, null, true);
	}
	
	@Override
	public void onAttack(Npc npc, Player attacker, int damage, boolean isSummon)
	{
		npc.getVariables().set("lastTarget", attacker);
		
		final Attackable monster = npc.asAttackable();
		if (monster != null)
		{
			monster.addDamageHate(attacker, Math.max(1, damage), 5000);
		}
	}
	
	@Override
	public void onKill(Npc npc, Player killer, boolean isSummon)
	{
		cancelQuestTimer(CHECK_TIMER, npc, null);
		cancelQuestTimer(PULL_TIMER, npc, null);
		npc.setScriptValue(0);
	}
	
	@Override
	public String onEvent(String event, Npc npc, Player player)
	{
		if ((npc == null) || npc.isDead())
		{
			return null;
		}
		
		switch (event)
		{
			case CHECK_TIMER:
			{
				handleCheckTarget(npc);
				break;
			}
			case PULL_TIMER:
			{
				handlePullTarget(npc);
				break;
			}
		}
		return super.onEvent(event, npc, player);
	}
	
	private void handleCheckTarget(Npc npc)
	{
		final Attackable monster = npc.asAttackable();
		if (monster == null)
		{
			return;
		}
		
		final Player target = resolveTarget(npc, monster);
		if ((target == null) || target.isDead() || !target.isOnline())
		{
			return;
		}
		
		npc.setTarget(target);
		
		// Se está castando summon, não faz mais nada.
		if (npc.isScriptValue(1))
		{
			return;
		}
		
		final double distance = npc.calculateDistance3D(target);
		
		// Perto o suficiente: ataque corpo a corpo.
		if (distance <= MELEE_DISTANCE)
		{
			npc.doAttack(target);
			return;
		}
		
		// Longe e fora de cooldown: inicia cast de summon.
		if (distance > SUMMON_DISTANCE)
		{
			final long now = System.currentTimeMillis();
			final long lastSummon = npc.getVariables().getLong("lastSummonTime", 0);
			if ((now - lastSummon) >= SUMMON_COOLDOWN_MS)
			{
				castSummonAndPull(npc, monster, target);
			}
		}
	}
	
	private void handlePullTarget(Npc npc)
	{
		// Fim do cast: reseta flag.
		npc.setScriptValue(0);
		
		final Player target = npc.getVariables().getObject("lastTarget", Player.class);
		if ((target == null) || target.isDead() || !target.isOnline())
		{
			return;
		}
		
		final Attackable monster = npc.asAttackable();
		if (monster == null)
		{
			return;
		}
		
		// Efeito visual de teleporte no jogador (antes e depois).
		target.broadcastPacket(new MagicSkillUse(target, target, 2036, 1, 0, 0));
		
		// Teleporta o jogador para perto do NPC.
		target.teleToLocation(npc.getX() + getRandom(-30, 31), npc.getY() + getRandom(-30, 31), npc.getZ());
		
		// Efeito visual de chegada.
		target.broadcastPacket(new MagicSkillUse(target, target, 2036, 1, 0, 0));
		
		npc.setTarget(target);
		monster.addDamageHate(target, 1, 10000);
		npc.doAttack(target);
		npc.getVariables().set("lastSummonTime", System.currentTimeMillis());
	}
	
	private Player resolveTarget(Npc npc, Attackable monster)
	{
		final Creature hated = monster.getMostHated();
		if (hated instanceof Player)
		{
			final Player player = (Player) hated;
			npc.getVariables().set("lastTarget", player);
			return player;
		}
		
		return npc.getVariables().getObject("lastTarget", Player.class);
	}
	
	private void castSummonAndPull(Npc npc, Attackable monster, Player target)
	{
		// Marca como castando.
		npc.setScriptValue(1);
		npc.getVariables().set("lastTarget", target);
		
		// Frase aleatória.
		final String phrase = SUMMON_PHRASES[getRandom(SUMMON_PHRASES.length)];
		npc.broadcastPacket(new NpcSay(npc.getObjectId(), ChatType.NPC_SHOUT, npc.getId(), phrase));
		
		// Animação visual do cast.
		npc.broadcastPacket(new MagicSkillUse(npc, target, VISUAL_SKILL, VISUAL_LEVEL, CAST_TIME, 0));
		
		// Agenda o pull após o tempo de cast.
		startQuestTimer(PULL_TIMER, CAST_TIME, npc, null);
	}
	
	public static void main(String[] args)
	{
		new SummonPc();
	}
}
