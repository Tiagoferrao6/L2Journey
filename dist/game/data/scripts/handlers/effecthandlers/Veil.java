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
package handlers.effecthandlers;

import java.util.concurrent.ScheduledFuture;

import com.l2journey.commons.threads.ThreadPool;
import com.l2journey.gameserver.ai.Intention;
import com.l2journey.gameserver.model.StatSet;
import com.l2journey.gameserver.model.actor.Creature;
import com.l2journey.gameserver.model.actor.enums.creature.Race;
import com.l2journey.gameserver.model.actor.instance.Defender;
import com.l2journey.gameserver.model.actor.instance.FortCommander;
import com.l2journey.gameserver.model.actor.instance.SiegeFlag;
import com.l2journey.gameserver.model.conditions.Condition;
import com.l2journey.gameserver.model.effects.AbstractEffect;
import com.l2journey.gameserver.model.effects.EffectFlag;
import com.l2journey.gameserver.model.skill.AbnormalVisualEffect;
import com.l2journey.gameserver.model.skill.Skill;
import com.l2journey.gameserver.model.stats.Formulas;

/**
 * @author KingHanker
 */
public class Veil extends AbstractEffect
{
	private static final int VEIL_SKILL_ID = 106;
	private static final int FALLBACK_CHANCE = 100;
	private static final int TWENTY_SECONDS = 20000;
	
	private final int _chance;
	private ScheduledFuture<?> _task;
	
	public Veil(Condition attachCond, Condition applyCond, StatSet set, StatSet params)
	{
		super(attachCond, applyCond, set, params);
		
		_chance = params.getInt("chance", FALLBACK_CHANCE);
	}
	
	@Override
	public boolean calcSuccess(Creature effector, Creature effected, Skill skill)
	{
		return Formulas.calcProbability(_chance, effector, effected, skill);
	}
	
	@Override
	public boolean canStart(Creature effector, Creature effected, Skill skill)
	{
		return (effected != effector) && (effected.isPlayer() || effected.isSummon() || (effected.isAttackable() && !((effected instanceof Defender) || (effected instanceof FortCommander) || (effected instanceof SiegeFlag) || (effected.getTemplate().getRace() == Race.SIEGE_WEAPON))));
	}
	
	@Override
	public int getEffectFlags()
	{
		return EffectFlag.PASSIVE.getMask();
	}
	
	@Override
	public void onStart(Creature effector, Creature effected, Skill skill)
	{
		effected.startAbnormalVisualEffect(true, AbnormalVisualEffect.TIME_BOMB);
		applyEffect(effected);
		
		_task = ThreadPool.scheduleAtFixedRate(() ->
		{
			if (effected.isAffectedBySkill(VEIL_SKILL_ID))
			{
				applyEffect(effected);
			}
			else
			{
				_task.cancel(false);
			}
		}, TWENTY_SECONDS, TWENTY_SECONDS);
	}
	
	private void applyEffect(Creature effected)
	{
		effected.setTarget(effected);
		effected.abortAttack();
		effected.abortCast();
		effected.getAI().setIntention(Intention.IDLE);
	}
	
	@Override
	public void onExit(Creature effector, Creature effected, Skill skill)
	{
		effected.stopAbnormalVisualEffect(true, AbnormalVisualEffect.TIME_BOMB);
		
		if (_task != null)
		{
			_task.cancel(false);
			_task = null;
		}
	}
}
