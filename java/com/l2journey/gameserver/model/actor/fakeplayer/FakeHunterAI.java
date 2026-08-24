package com.l2journey.gameserver.model.actor.fakeplayer;

import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.logging.Logger;

import com.l2journey.commons.threads.ThreadPool;
import com.l2journey.commons.util.Rnd;
import com.l2journey.gameserver.ai.PlayerAI;
import com.l2journey.gameserver.data.xml.PlayerTemplateData;
import com.l2journey.gameserver.model.World;
import com.l2journey.gameserver.model.WorldObject;
import com.l2journey.gameserver.model.actor.Creature;
import com.l2journey.gameserver.model.actor.Player;
import com.l2journey.gameserver.model.actor.appearance.PlayerAppearance;
import com.l2journey.gameserver.model.actor.instance.Monster;
import com.l2journey.gameserver.model.actor.templates.PlayerTemplate;
import com.l2journey.gameserver.model.groups.Party;
import com.l2journey.gameserver.model.groups.PartyDistributionType;
import com.l2journey.gameserver.dao.FakePlayerDAO;

/**
 * AI & Controller for Fake Hunter players with DNA traits, party logic, and combat reactivity.
 */
public class FakeHunterAI extends PlayerAI
{
	private static final Logger LOGGER = Logger.getLogger(FakeHunterAI.class.getName());

	private final FakePlayerProfile _profile;
	private Player _player;
	private ScheduledFuture<?> _aiTask;
	private boolean _isEscaping;

	public FakeHunterAI(FakePlayerProfile profile)
	{
		super(null);
		_profile = profile;
	}

	public FakePlayerProfile getProfile()
	{
		return _profile;
	}

	public Player getPlayer()
	{
		return _player;
	}

	public synchronized boolean spawn()
	{
		if ((_player != null) && _player.isOnline())
		{
			return true;
		}

		int classId = _profile.getClassId() > 0 ? _profile.getClassId() : 1; // Default Warrior
		PlayerTemplate template = PlayerTemplateData.getInstance().getTemplate(classId);
		if (template == null)
		{
			template = PlayerTemplateData.getInstance().getTemplate(1);
		}

		final String name = "Hunter_" + _profile.getFakeId();
		final PlayerAppearance appearance = new PlayerAppearance((byte) Rnd.get(3), (byte) Rnd.get(3), (byte) Rnd.get(3), false);
		_player = Player.create(template, name.toLowerCase(), name, appearance);

		if (_player == null)
		{
			LOGGER.warning("FakeHunterAI: Failed to create Player for profile ID " + _profile.getFakeId());
			return false;
		}

		_player.setFakePlayer(true);
		_player.setAI(this);
		if (_profile.getDualClassId() != -1)
		{
			_player.setDualClassId(_profile.getDualClassId());
			_player.rewardSkills();
		}
		_player.getStat().setLevel((byte) 35);

		int x = _profile.getX() != 0 ? _profile.getX() : -14000 + Rnd.get(-500, 500);
		int y = _profile.getY() != 0 ? _profile.getY() : 123000 + Rnd.get(-500, 500);
		int z = _profile.getZ() != 0 ? _profile.getZ() : -3115;
		int heading = _profile.getHeading() != 0 ? _profile.getHeading() : Rnd.get(65535);

		_player.setXYZ(x, y, z);
		_player.setHeading(heading);
		_player.spawnMe(x, y, z);

		// Start AI thinking loop every 2 seconds
		_aiTask = ThreadPool.scheduleAtFixedRate(this::thinkCombat, 2000, 2000);

		_profile.setActive(true);
		_profile.setLastActiveTime(System.currentTimeMillis());
		FakePlayerDAO.getInstance().saveProfile(_profile);

		LOGGER.info("FakeHunterAI: Spawned Hunter " + name + " (Aggro: " + _profile.getAggressiveness() + ", Courage: " + _profile.getCourage() + ", Party: " + _profile.getPartyTendency() + ")");
		return true;
	}

	public synchronized void despawn()
	{
		if (_aiTask != null)
		{
			_aiTask.cancel(false);
			_aiTask = null;
		}

		if (_player != null)
		{
			_profile.setX(_player.getX());
			_profile.setY(_player.getY());
			_profile.setZ(_player.getZ());
			_profile.setHeading(_player.getHeading());
			_profile.setActive(false);
			FakePlayerDAO.getInstance().saveProfile(_profile);

			_player.deleteMe();
			_player = null;
		}
	}

	private void thinkCombat()
	{
		if ((_player == null) || !_player.isOnline() || _player.isDead())
		{
			return;
		}

		// 1. Low HP Courage Check
		double hpRatio = _player.getCurrentHp() / _player.getMaxHp();
		int courage = _profile.getCourage(); // 1 to 10
		double escapeThreshold = 0.5 - (courage * 0.04); // e.g. courage 1 -> 46% HP, courage 10 -> 10% HP

		if (hpRatio < escapeThreshold && !_isEscaping)
		{
			triggerFlee();
			return;
		}

		if (_isEscaping)
		{
			return;
		}

		// 2. Party Logic Check
		if (_profile.getPartyTendency() >= 6 && !_player.isInParty())
		{
			checkAndJoinParty();
		}

		// 3. Combat reactivity
		if (_player.getTarget() == null || !(_player.getTarget() instanceof Monster))
		{
			findMonsterTarget();
		}

		WorldObject target = _player.getTarget();
		if (target instanceof Monster)
		{
			Monster mob = (Monster) target;
			if (mob.isDead())
			{
				_player.setTarget(null);
			}
			else
			{
				// Attack monster target
				_player.getAI().setIntention(com.l2journey.gameserver.ai.Intention.ATTACK, mob);
			}
		}
	}

	private void triggerFlee()
	{
		_isEscaping = true;
		LOGGER.info("FakeHunterAI: Hunter #" + _profile.getFakeId() + " (Courage: " + _profile.getCourage() + ") fleeing due to low HP!");
		// Move towards town square
		_player.getAI().setIntention(com.l2journey.gameserver.ai.Intention.MOVE_TO, new com.l2journey.gameserver.model.Location(-14228, 123445, -3115));

		// Reset escape flag after 10 seconds
		ThreadPool.schedule(() ->
		{
			_isEscaping = false;
		}, 10000);
	}

	private void checkAndJoinParty()
	{
		List<Player> nearbyPlayers = World.getInstance().getVisibleObjectsInRange(_player, Player.class, 1000);
		for (Player targetPlayer : nearbyPlayers)
		{
			if (targetPlayer.isFakePlayer() && targetPlayer != _player)
			{
				if (!targetPlayer.isInParty())
				{
					Party party = new Party(_player, PartyDistributionType.FINDERS_KEEPERS);
					party.addPartyMember(targetPlayer);
					LOGGER.info("FakeHunterAI: Formed bot party between Hunter #" + _profile.getFakeId() + " and " + targetPlayer.getName());
					break;
				}
			}
		}
	}

	private void findMonsterTarget()
	{
		int radius = 500 + (_profile.getAggressiveness() * 100); // 600 to 1500 range based on aggressiveness
		List<Monster> nearbyMonsters = World.getInstance().getVisibleObjectsInRange(_player, Monster.class, radius);
		for (Monster mob : nearbyMonsters)
		{
			if (!mob.isDead())
			{
				_player.setTarget(mob);
				break;
			}
		}
	}
}
