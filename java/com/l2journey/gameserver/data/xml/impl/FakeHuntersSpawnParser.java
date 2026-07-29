package com.l2journey.gameserver.data.xml.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import com.l2journey.commons.util.IXmlReader;
import com.l2journey.commons.util.Rnd;
import com.l2journey.gameserver.data.xml.PlayerTemplateData;
import com.l2journey.gameserver.managers.FakeHunterManager;
import com.l2journey.gameserver.managers.IdManager;
import com.l2journey.gameserver.model.actor.appearance.PlayerAppearance;
import com.l2journey.gameserver.model.actor.dna.HunterDNA;
import com.l2journey.gameserver.model.actor.instance.FakePlayer;
import com.l2journey.gameserver.model.actor.templates.PlayerTemplate;

public class FakeHuntersSpawnParser implements IXmlReader
{
	private static final Logger LOGGER = Logger.getLogger(FakeHuntersSpawnParser.class.getName());

	protected FakeHuntersSpawnParser()
	{
		load();
	}

	@Override
	public synchronized void load()
	{
		parseDatapackFile("config/npcs/fake_hunters_spawns.xml");
		LOGGER.info(getClass().getSimpleName() + ": Loaded fake hunter spawns.");
	}

	@Override
	public void parseDocument(Document document, File file)
	{
		forEach(document, n ->
		{
			if ("hunters".equalsIgnoreCase(n.getNodeName()))
			{
				forEach(n, d ->
				{
					if ("spawn".equalsIgnoreCase(d.getNodeName()))
					{
						final List<String> names = new ArrayList<>();
						String profileId = "cautious_hunter";
						int classId = 88;
						boolean isFemale = false;
						int x = 0, y = 0, z = 0, radius = 0;
						int amount = 1;

						for (Node child = d.getFirstChild(); child != null; child = child.getNextSibling())
						{
							if ("names".equalsIgnoreCase(child.getNodeName()))
							{
								forEach(child, nameNode ->
								{
									if ("name".equalsIgnoreCase(nameNode.getNodeName()))
									{
										names.add(nameNode.getTextContent().trim());
									}
								});
							}
							else if ("profileId".equalsIgnoreCase(child.getNodeName()))
							{
								profileId = child.getTextContent().trim();
							}
							else if ("appearance".equalsIgnoreCase(child.getNodeName()))
							{
								classId = parseInteger(child.getAttributes(), "classId", 88);
								final String sexStr = parseString(child.getAttributes(), "sex", "MALE");
								isFemale = "FEMALE".equalsIgnoreCase(sexStr);
							}
							else if ("location".equalsIgnoreCase(child.getNodeName()))
							{
								x = parseInteger(child.getAttributes(), "x");
								y = parseInteger(child.getAttributes(), "y");
								z = parseInteger(child.getAttributes(), "z");
								radius = parseInteger(child.getAttributes(), "radius");
							}
							else if ("amount".equalsIgnoreCase(child.getNodeName()))
							{
								amount = Integer.parseInt(child.getTextContent().trim());
							}
						}

						// Load associated Hunter DNA
						final HunterDNA dna = FakeHuntersDNAParser.getInstance().getDNA(profileId);

						for (int i = 0; i < amount; i++)
						{
							if (names.isEmpty())
							{
								continue;
							}
							final String name = names.get(Rnd.get(names.size()));
							FakeHunterManager.getInstance().addReservedName(name);

							final PlayerTemplate template = PlayerTemplateData.getInstance().getTemplate(classId);
							if (template == null)
							{
								LOGGER.warning(getClass().getSimpleName() + ": Invalid classId " + classId + " for hunter " + name);
								continue;
							}

							final PlayerAppearance app = new PlayerAppearance((byte) 0, (byte) 0, (byte) 0, isFemale);
							final FakePlayer bot = new FakePlayer(IdManager.getInstance().getNextId(), template, "FakeHunters", app);

							bot.setName(name);
							bot.setTitle("Hunter");
							bot.setAccessLevel(0);
							bot.setSpawnTime(System.currentTimeMillis());
							bot.setHunterDNA(dna);

							int finalX = x + Rnd.get(-radius, radius);
							int finalY = y + Rnd.get(-radius, radius);

							bot.spawnMe(finalX, finalY, z);
							FakeHunterManager.getInstance().addHunter(bot);
						}
					}
				});
			}
		});
	}

	public static FakeHuntersSpawnParser getInstance()
	{
		return SingletonHolder.INSTANCE;
	}

	private static class SingletonHolder
	{
		protected static final FakeHuntersSpawnParser INSTANCE = new FakeHuntersSpawnParser();
	}
}
