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

/**
 * Single Player Mercenary Instance (Healer / Support).
 * Spawns on-demand, matches owner's level, joins owner's party, and performs AI healing/support.
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

		// Start AI Healing Tick every 2 seconds
		ThreadPool.scheduleAtFixedRate(new HealerAITick(), 2000, 2000);
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
	 * AI Tick for Healer Mercenary (Emergency Healing, Buffing, Cleanse).
	 */
	private class HealerAITick implements Runnable
	{
		@Override
		public void run()
		{
			Player owner = getOwner();
			if (owner == null || !owner.isOnline() || isDead() || !isOnline())
			{
				return;
			}

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

			// Healing Priority: Check Owner HP
			double ownerHpPercent = (owner.getCurrentHp() / owner.getMaxHp()) * 100.0;
			if (ownerHpPercent < 70.0)
			{
				castHealSkill(owner, ownerHpPercent < 35.0);
				return;
			}

			// Check Self HP
			double selfHpPercent = (getCurrentHp() / getMaxHp()) * 100.0;
			if (selfHpPercent < 60.0)
			{
				castHealSkill(MercenaryInstance.this, selfHpPercent < 35.0);
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
							castHealSkill(member, memberHpPercent < 35.0);
							return;
						}
					}
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
