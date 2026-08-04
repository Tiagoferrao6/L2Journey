package com.l2journey.gameserver.managers;

import java.util.logging.Logger;

import com.l2journey.gameserver.model.actor.Player;

/**
 * Handover Dual-Control Manager (`HumanHandoverManager`).
 * Manages concurrency and clean state transition between AI OOG Agent execution
 * and physical human player login/logout through the L2 client.
 */
public class HumanHandoverManager
{
	private static final Logger LOGGER = Logger.getLogger(HumanHandoverManager.class.getName());

	protected HumanHandoverManager()
	{
		LOGGER.info("HumanHandoverManager: Initialized Dual-Control Handover Manager.");
	}

	public static HumanHandoverManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}

	private static class SingletonHolder
	{
		protected static final HumanHandoverManager INSTANCE = new HumanHandoverManager();
	}

	/**
	 * Triggered when a physical human player connects to an account.
	 * Disconnects any active OOG AI session on the account gracefully.
	 */
	public void onHumanLogin(String accountName, Player player)
	{
		OOGClientSession.SessionEntry session = OOGClientSession.getInstance().getSession(accountName);
		if (session != null)
		{
			session.setHumanConnected(true);
			if (session.getBotInstance() != null && session.getBotInstance().isOnline())
			{
				LOGGER.info("HumanHandoverManager: Human player connected to account '" + accountName + "'. Yielding control and disconnecting OOG bot.");
				session.getBotInstance().deleteMe();
				session.setBotInstance(null);
			}
		}
	}

	/**
	 * Triggered when a physical human player disconnects from an account.
	 * Signals OOG Client Session engine to resume AI Agent automation.
	 */
	public void onHumanLogout(String accountName)
	{
		OOGClientSession.SessionEntry session = OOGClientSession.getInstance().getSession(accountName);
		if (session != null)
		{
			session.setHumanConnected(false);
			LOGGER.info("HumanHandoverManager: Human player disconnected from account '" + accountName + "'. Signalling OOG Engine to resume AI automation.");
		}
	}
}
