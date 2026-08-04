package com.l2journey.gameserver.managers;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.l2journey.commons.util.Rnd;
import com.l2journey.gameserver.data.xml.PlayerTemplateData;
import com.l2journey.gameserver.managers.IdManager;
import com.l2journey.gameserver.model.actor.appearance.PlayerAppearance;
import com.l2journey.gameserver.model.actor.instance.FakePlayer;
import com.l2journey.gameserver.model.actor.templates.PlayerTemplate;

/**
 * Autonomous Character Creation Agent (`OOGCharacterCreator`).
 * Handles automatic character creation protocol execution for new AI companion accounts.
 */
public class OOGCharacterCreator
{
	private static final Logger LOGGER = Logger.getLogger(OOGCharacterCreator.class.getName());

	protected OOGCharacterCreator()
	{
		LOGGER.info("OOGCharacterCreator: Initialized Autonomous Character Creation Agent.");
	}

	public static OOGCharacterCreator getInstance()
	{
		return SingletonHolder.INSTANCE;
	}

	private static class SingletonHolder
	{
		protected static final OOGCharacterCreator INSTANCE = new OOGCharacterCreator();
	}

	/**
	 * Creates a new character instance autonomously for a given account.
	 * @param accountName Account name
	 * @param charName Character name
	 * @param classId Base class ID (0 = Human Fighter, 10 = Human Mystic, etc.)
	 * @param role Companion role title
	 * @return Created FakePlayer instance ready for world spawn.
	 */
	public FakePlayer createCharacter(String accountName, String charName, int classId, String role)
	{
		try
		{
			PlayerTemplate template = PlayerTemplateData.getInstance().getTemplate(classId);
			if (template == null)
			{
				LOGGER.warning("OOGCharacterCreator: Invalid classId " + classId + " for character creation!");
				return null;
			}

			// Generate randomized visual appearance (sex, hairStyle, hairColor, face)
			byte sex = (byte) (classId == 10 ? Rnd.get(2) : 0);
			byte hairStyle = (byte) Rnd.get(5);
			byte hairColor = (byte) Rnd.get(4);
			byte face = (byte) Rnd.get(3);
			PlayerAppearance app = new PlayerAppearance(sex, hairStyle, hairColor, false);

			int objectId = IdManager.getInstance().getNextId();
			FakePlayer bot = new FakePlayer(objectId, template, accountName, app);
			bot.setName(charName);
			bot.setTitle(role);
			bot.setHeading(Rnd.get(65536));

			LOGGER.info("OOGCharacterCreator: Successfully created autonomous character '" + charName + "' (ClassId: " + classId + ") for account '" + accountName + "'.");
			return bot;
		}
		catch (Exception e)
		{
			LOGGER.log(Level.SEVERE, "OOGCharacterCreator: Error during character creation for account " + accountName, e);
			return null;
		}
	}
}
