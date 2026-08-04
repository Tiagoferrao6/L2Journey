package com.l2journey.gameserver.managers;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import com.l2journey.gameserver.model.actor.instance.FakePlayer;
import com.l2journey.gameserver.model.actor.Player;

/**
 * Manager for Out-Of-Game (OOG) Virtual Client Sessions.
 * Emulates protocol-driven account lifecycle (Auth -> Char Select -> In-Game)
 * and coordinates Handover Dual Control between AI Agents and Human Players.
 */
public class OOGClientSession
{
	private static final Logger LOGGER = Logger.getLogger(OOGClientSession.class.getName());

	public enum SessionState
	{
		DISCONNECTED,
		AUTHENTICATED,
		CHARACTER_SELECT,
		IN_GAME
	}

	public static class SessionEntry
	{
		private final String _accountName;
		private final String _charName;
		private SessionState _state;
		private FakePlayer _botInstance;
		private boolean _isHumanConnected;

		public SessionEntry(String accountName, String charName)
		{
			_accountName = accountName;
			_charName = charName;
			_state = SessionState.DISCONNECTED;
			_isHumanConnected = false;
		}

		public String getAccountName()
		{
			return _accountName;
		}

		public String getCharName()
		{
			return _charName;
		}

		public SessionState getState()
		{
			return _state;
		}

		public void setState(SessionState state)
		{
			_state = state;
		}

		public FakePlayer getBotInstance()
		{
			return _botInstance;
		}

		public void setBotInstance(FakePlayer botInstance)
		{
			_botInstance = botInstance;
		}

		public boolean isHumanConnected()
		{
			return _isHumanConnected;
		}

		public void setHumanConnected(boolean humanConnected)
		{
			_isHumanConnected = humanConnected;
		}
	}

	private final Map<String, SessionEntry> _activeSessions = new ConcurrentHashMap<>();

	protected OOGClientSession()
	{
		LOGGER.info("OOGClientSession: Initialized Out-Of-Game Protocol Emulation Engine.");
	}

	public static OOGClientSession getInstance()
	{
		return SingletonHolder.INSTANCE;
	}

	private static class SingletonHolder
	{
		protected static final OOGClientSession INSTANCE = new OOGClientSession();
	}

	/**
	 * Registers and authenticates an OOG account session.
	 */
	public SessionEntry connectAccount(String accountName, String charName)
	{
		SessionEntry session = _activeSessions.computeIfAbsent(accountName, acc -> new SessionEntry(accountName, charName));
		session.setState(SessionState.AUTHENTICATED);
		LOGGER.info("OOGClientSession: Authenticated OOG Account '" + accountName + "' (Target Char: " + charName + ").");
		return session;
	}

	/**
	 * Updates the session state to IN_GAME and attaches the bot player instance.
	 */
	public void enterWorld(String accountName, FakePlayer bot)
	{
		SessionEntry session = _activeSessions.get(accountName);
		if (session != null)
		{
			session.setBotInstance(bot);
			session.setState(SessionState.IN_GAME);
			LOGGER.info("OOGClientSession: Account '" + accountName + "' entered world with character '" + bot.getName() + "'.");
		}
	}

	/**
	 * Disconnects the OOG session gracioulsy.
	 */
	public void disconnectSession(String accountName)
	{
		SessionEntry session = _activeSessions.remove(accountName);
		if (session != null)
		{
			session.setState(SessionState.DISCONNECTED);
			if (session.getBotInstance() != null && session.getBotInstance().isOnline())
			{
				session.getBotInstance().deleteMe();
			}
			LOGGER.info("OOGClientSession: Disconnected OOG Session for account '" + accountName + "'.");
		}
	}

	public SessionEntry getSession(String accountName)
	{
		return _activeSessions.get(accountName);
	}

	public boolean isHumanConnected(String accountName)
	{
		SessionEntry session = _activeSessions.get(accountName);
		return session != null && session.isHumanConnected();
	}
}
