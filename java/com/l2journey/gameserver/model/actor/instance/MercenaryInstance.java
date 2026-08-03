package com.l2journey.gameserver.model.actor.instance;

import java.util.logging.Logger;

import com.l2journey.commons.threads.ThreadPool;
import com.l2journey.gameserver.model.groups.Party;
import com.l2journey.gameserver.model.skill.Skill;
import com.l2journey.gameserver.data.xml.SkillData;
import com.l2journey.gameserver.model.World;
import com.l2journey.gameserver.model.actor.Player;
import com.l2journey.gameserver.model.actor.appearance.PlayerAppearance;
import com.l2journey.gameserver.model.actor.templates.PlayerTemplate;
import com.l2journey.gameserver.model.skill.BuffInfo;

/**
 * Single Player Mercenary Instance (Healer / Support).
 * Spawns on-demand, matches owner's level, joins owner's party, and performs BabyPet-style AI healing/support/buffing.
 */
public class MercenaryInstance extends FakePlayer
{
	private static final Logger LOGGER = Logger.getLogger(MercenaryInstance.class.getName());

	private final int _ownerCharId;
	private final String _mercenaryId;
	private boolean _following = true;

	public MercenaryInstance(int objectId, PlayerTemplate template, String accountName, PlayerAppearance app, int ownerCharId, String mercenaryId)
	{
		super(objectId, template, accountName, app);
		_ownerCharId = ownerCharId;
		_mercenaryId = mercenaryId;

		setRunning();

		// Start AI Support Tick every 2 seconds
		ThreadPool.scheduleAtFixedRate(new HealerAITick(), 2000, 2000);
	}

	@Override
	public boolean isMercenary()
	{
		return true;
	}

	public int getOwnerCharId()
	{
		return _ownerCharId;
	}

	public String getMercenaryId()
	{
		return _mercenaryId;
	}

	public Player getOwner()
	{
		return World.getInstance().getPlayer(_ownerCharId);
	}

	public boolean isFollowing()
	{
		return _following;
	}

	public void setFollowing(boolean following)
	{
		_following = following;
		if (!_following)
		{
			getAI().stopFollow();
		}
	}

	/**
	 * AI Tick for Healer Mercenary (Emergency Healing, Buffing, Recharge, Cleanse).
	 * Mirrors BabyPet (Kookaburra) continuous support engine.
	 */
	private class HealerAITick implements Runnable
	{
		private final int[] SUPPORT_BUFF_IDS = { 1204, 1085, 1086, 1068, 1040, 1059, 1062 }; // WindWalk, Acumen, Haste, Might, Shield, Empower, BerserkerSpirit

		@Override
		public void run()
		{
			Player owner = getOwner();
			if (owner == null || !owner.isOnline() || isDead() || !isOnline())
			{
				return;
			}

			setRunning();

			// Follow Owner
			if (_following && (getTarget() == null || !isCastingNow()))
			{
				double dist = calculateDistance2D(owner);
				if (dist > 150 && dist < 2000)
				{
					getAI().startFollow(owner);
				}
				else if (dist >= 2000)
				{
					teleToLocation(owner.getX(), owner.getY(), owner.getZ());
				}
			}

			if (isCastingNow())
			{
				return;
			}

			// 1. Emergency Healing: Check Owner HP
			double ownerHpPercent = (owner.getCurrentHp() / owner.getMaxHp()) * 100.0;
			if (ownerHpPercent < 70.0)
			{
				castHealSkill(owner, ownerHpPercent < 30.0);
				return;
			}

			// Check Self HP
			double selfHpPercent = (getCurrentHp() / getMaxHp()) * 100.0;
			if (selfHpPercent < 60.0)
			{
				castHealSkill(MercenaryInstance.this, selfHpPercent < 30.0);
				return;
			}

			// Check Party Members HP
			Party party = getParty();
			if (party != null)
			{
				for (Player member : party.getMembers())
				{
					if (member != null && !member.isDead())
					{
						double memberHpPercent = (member.getCurrentHp() / member.getMaxHp()) * 100.0;
						if (memberHpPercent < 60.0)
						{
							castHealSkill(member, memberHpPercent < 30.0);
							return;
						}
					}
				}
			}

			// 2. Recharge MP (If owner is in combat stance and MP < 60%)
			if (owner.isInCombat())
			{
				double ownerMpPercent = (owner.getCurrentMp() / owner.getMaxMp()) * 100.0;
				if (ownerMpPercent < 60.0)
				{
					Skill rechargeSkill = SkillData.getInstance().getSkill(1013, 9); // Recharge (1013)
					if (rechargeSkill != null && getCurrentMp() >= rechargeSkill.getMpConsume())
					{
						setTarget(owner);
						doCast(rechargeSkill);
						return;
					}
				}
			}

			// 3. Automatic Continuous Buff Maintenance
			for (int buffId : SUPPORT_BUFF_IDS)
			{
				Skill buffSkill = SkillData.getInstance().getSkill(buffId, getSkillLevel(buffId) > 0 ? getSkillLevel(buffId) : 1);
				if (buffSkill == null || getCurrentMp() < buffSkill.getMpConsume())
				{
					continue;
				}

				// Check Owner
				BuffInfo ownerBuff = owner.getEffectList().getBuffInfoByAbnormalType(buffSkill.getAbnormalType());
				if (ownerBuff == null)
				{
					setTarget(owner);
					doCast(buffSkill);
					return;
				}

				// Check Self
				BuffInfo selfBuff = getEffectList().getBuffInfoByAbnormalType(buffSkill.getAbnormalType());
				if (selfBuff == null)
				{
					setTarget(MercenaryInstance.this);
					doCast(buffSkill);
					return;
				}
			}
		}

		private void castHealSkill(Player target, boolean emergency)
		{
			int healSkillId = emergency ? 1015 : 1217; // Battle Heal vs Greater Heal
			Skill skill = SkillData.getInstance().getSkill(healSkillId, getSkillLevel(healSkillId) > 0 ? getSkillLevel(healSkillId) : 1);
			if (skill != null && getCurrentMp() >= skill.getMpConsume())
			{
				setTarget(target);
				doCast(skill);
			}
		}
	}
}
