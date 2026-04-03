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
import com.l2journey.gameserver.model.actor.Npc;
import com.l2journey.gameserver.model.actor.Player;
import com.l2journey.gameserver.model.skill.Skill;
import com.l2journey.gameserver.model.skill.holders.SkillHolder;

import ai.AbstractNpcAI;

/**
 * IA de Summon Pc.<br>
 * Puxa o jogador para o NPC ao atacar.
 * @author Zoey76, Mafias
 */
public class SummonPc extends AbstractNpcAI
{
	// NPCs
    private static final int PORTA = 20213;
    private static final int PERUM = 20221;
	// Skill
    private static final SkillHolder SUMMON_PC = new SkillHolder(4161, 1);

    private SummonPc()
    {
        addAttackId(PORTA, PERUM);
        addSpellFinishedId(PORTA, PERUM);
    }

    @Override
    public void onAttack(Npc npc, Player attacker, int damage, boolean isSummon)
    {
        final Skill skill = SUMMON_PC.getSkill();
        final boolean attacked = npc.getVariables().getBoolean("attacked", false);

        if (attacked || npc.isDead())
        {
            return;
        }

        if ((skill.getMpConsume() >= npc.getCurrentMp()) ||
            (skill.getHpConsume() >= npc.getCurrentHp()) ||
            npc.isSkillDisabled(skill))
        {
            return;
        }

        final int chance = getRandom(100);
        final double distance = npc.calculateDistance3D(attacker);

        if ((distance > 300) && (chance < 50))
        {
            npc.setTarget(attacker);
            npc.doCast(skill);
            npc.getVariables().set("attacked", true);
            return;
        }

        if (distance > 100)
        {
            final Attackable monster = npc.asAttackable();
            if (monster.getMostHated() != null)
            {
                if (((monster.getMostHated() == attacker) && (chance < 50)) || (chance < 10))
                {
                    npc.setTarget(attacker);
                    npc.doCast(skill);
                    npc.getVariables().set("attacked", true);
                }
            }
        }
    }

    @Override
    public void onSpellFinished(Npc npc, Player player, Skill skill)
    {
        if ((skill.getId() == SUMMON_PC.getSkillId()) &&
            !npc.isDead() &&
            npc.getVariables().getBoolean("attacked", false))
        {
            player.teleToLocation(npc);
            npc.getVariables().set("attacked", false);
        }
    }

    public static void main(String[] args)
    {
        new SummonPc();
    }
}
