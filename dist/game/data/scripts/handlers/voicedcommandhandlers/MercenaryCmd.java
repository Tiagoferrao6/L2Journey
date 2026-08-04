package handlers.voicedcommandhandlers;

import com.l2journey.gameserver.handler.IVoicedCommandHandler;
import com.l2journey.gameserver.managers.MercenaryManager;
import com.l2journey.gameserver.model.actor.Player;
import com.l2journey.gameserver.model.actor.instance.MercenaryInstance;

public class MercenaryCmd implements IVoicedCommandHandler
{
	private static final String[] VOICED_COMMANDS =
	{
		"merc"
	};

	@Override
	public boolean useVoicedCommand(String command, Player activeChar, String target)
	{
		if (activeChar == null)
		{
			return false;
		}

		MercenaryInstance merc = MercenaryManager.getInstance().getActiveMercenary(activeChar.getObjectId());
		if (merc == null)
		{
			activeChar.sendMessage("Voce nao possui um mercenario ativo.");
			return false;
		}

		String subCmd = (target != null && !target.isEmpty()) ? target.trim().toLowerCase() : "panel";

		switch (subCmd)
		{
			case "panel":
			case "control":
				merc.sendControlPanel(activeChar);
				break;
			case "attack":
				merc.forceAttackTarget();
				activeChar.sendMessage("Mercenario orientando ataque ao alvo.");
				break;
			case "follow":
				merc.setFollowing(true);
				activeChar.sendMessage("Mercenario agora esta te seguindo.");
				break;
			case "stay":
			case "stop":
				merc.setFollowing(false);
				activeChar.sendMessage("Mercenario parado no local.");
				break;
			case "heal":
				merc.forceHealOwner();
				activeChar.sendMessage("Mercenario executando cura emergencial.");
				break;
			case "buff":
				merc.forceBuffOwner();
				activeChar.sendMessage("Mercenario renovando suporte de buffs.");
				break;
			case "dismiss":
				MercenaryManager.getInstance().dismissMercenary(activeChar, true);
				activeChar.sendMessage("Mercenario dispensado com sucesso.");
				break;
			default:
				merc.sendControlPanel(activeChar);
				break;
		}
		return true;
	}

	@Override
	public String[] getVoicedCommandList()
	{
		return VOICED_COMMANDS;
	}
}
