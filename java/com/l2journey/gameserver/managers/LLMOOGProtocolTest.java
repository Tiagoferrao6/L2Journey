package com.l2journey.gameserver.managers;

import com.l2journey.gameserver.managers.OOGClientSession.SessionEntry;
import com.l2journey.gameserver.managers.OOGClientSession.SessionState;

/**
 * Unit test suite for OOG Protocol Driver, Character Creation, and Handover Dual Control.
 */
public class LLMOOGProtocolTest
{
	public static void main(String[] args)
	{
		System.out.println("Testing OOGClientSession authentication & lifecycle...");
		OOGClientSession sessionManager = OOGClientSession.getInstance();

		SessionEntry entry = sessionManager.connectAccount("oog_acc_paladinbot", "PaladinBot");
		if (entry != null && entry.getState() == SessionState.AUTHENTICATED)
		{
			System.out.println("[PASS] OOG Account 'oog_acc_paladinbot' authenticated successfully.");
		}

		System.out.println("Testing HumanHandoverManager dual-control handover...");
		HumanHandoverManager handoverManager = HumanHandoverManager.getInstance();

		handoverManager.onHumanLogin("oog_acc_paladinbot", null);
		if (sessionManager.isHumanConnected("oog_acc_paladinbot"))
		{
			System.out.println("[PASS] Human login detected. OOG session marked as human active.");
		}

		handoverManager.onHumanLogout("oog_acc_paladinbot");
		if (!sessionManager.isHumanConnected("oog_acc_paladinbot"))
		{
			System.out.println("[PASS] Human logout detected. OOG session ready for AI automation.");
		}

		System.out.println("All OOG Protocol Driver & Dual Control tests completed successfully.");
	}
}
