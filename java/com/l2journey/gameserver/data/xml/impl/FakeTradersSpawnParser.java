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
import com.l2journey.gameserver.managers.IdManager;
import com.l2journey.gameserver.managers.FakeTraderManager;
import com.l2journey.gameserver.model.actor.appearance.PlayerAppearance;
import com.l2journey.gameserver.model.actor.instance.FakePlayer;
import com.l2journey.gameserver.model.actor.templates.PlayerTemplate;

public class FakeTradersSpawnParser implements IXmlReader
{
	private static final Logger LOGGER = Logger.getLogger(FakeTradersSpawnParser.class.getName());

	protected FakeTradersSpawnParser()
	{
		load();
	}

	@Override
	public synchronized void load()
	{
		parseDatapackFile("config/npcs/fake_traders_spawns.xml");
		LOGGER.info(getClass().getSimpleName() + ": Loaded fake trader spawns.");
	}

	@Override
	public void parseDocument(Document document, File file)
	{
		forEach(document, n ->
		{
			if ("traders".equalsIgnoreCase(n.getNodeName()))
			{
				forEach(n, d ->
				{
					if ("spawn".equalsIgnoreCase(d.getNodeName()))
					{
						final NamedNodeMap attrs = d.getAttributes();
						final String type = parseString(attrs, "type");
						
						final List<String> names = new ArrayList<>();
						String titlePrefix = "";
						String profileId = "";
						int classId = 0;
						boolean isFemale = false;
						int x = 0, y = 0, z = 0, radius = 0;
						int amount = 1;
						long renewTime = 14400000;

						for (Node child = d.getFirstChild(); child != null; child = child.getNextSibling())
						{
							if ("names".equalsIgnoreCase(child.getNodeName()))
							{
								forEach(child, nameNode ->
								{
									if ("name".equalsIgnoreCase(nameNode.getNodeName()))
									{
										names.add(nameNode.getTextContent());
									}
								});
							}
							else if ("titlePrefix".equalsIgnoreCase(child.getNodeName()))
							{
								titlePrefix = child.getTextContent();
							}
							else if ("profileId".equalsIgnoreCase(child.getNodeName()))
							{
								profileId = child.getTextContent();
							}
							else if ("appearance".equalsIgnoreCase(child.getNodeName()))
							{
								classId = parseInteger(child.getAttributes(), "classId");
								final String sex = parseString(child.getAttributes(), "sex");
								isFemale = "FEMALE".equalsIgnoreCase(sex);
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
								amount = Integer.parseInt(child.getTextContent());
							}
							else if ("renewTime".equalsIgnoreCase(child.getNodeName()))
							{
								renewTime = Long.parseLong(child.getTextContent());
							}
						}

						// Process Spawns
						for (int i = 0; i < amount; i++)
						{
							if (names.isEmpty())
							{
								continue;
							}
							final String name = names.get(Rnd.get(names.size()));
							
							FakeTraderManager.getInstance().addReservedName(name);

							final PlayerTemplate template = com.l2journey.gameserver.data.xml.PlayerTemplateData.getInstance().getTemplate(classId);
							if (template == null)
							{
								LOGGER.warning(getClass().getSimpleName() + ": Invalid classId " + classId + " for bot " + name);
								continue;
							}

							final PlayerAppearance app = new PlayerAppearance((byte) 0, (byte) 0, (byte) 0, isFemale);
							final FakePlayer bot = new FakePlayer(IdManager.getInstance().getNextId(), template, "FakeTraders", app);
							
							bot.setName(name);
							bot.setTitle(titlePrefix);
							bot.setAccessLevel(0);
							bot.setSpawnTime(System.currentTimeMillis());
							bot.setRenewTime(renewTime);
							
							// Randomize location inside radius
							int finalX = x + Rnd.get(-radius, radius);
							int finalY = y + Rnd.get(-radius, radius);
							
							bot.spawnMe(finalX, finalY, z);
							FakeTraderManager.getInstance().addTrader(bot);

							// Call specific setup depending on type
							if ("SELL".equalsIgnoreCase(type))
							{
								bot.setupSellStore(profileId);
							}
							else if ("BUY".equalsIgnoreCase(type))
							{
								bot.setupBuyStore(profileId);
							}
							else if ("CRAFT".equalsIgnoreCase(type))
							{
								bot.setupCraftStore(profileId);
							}
						}
					}
				});
			}
		});
	}

	public static FakeTradersSpawnParser getInstance()
	{
		return SingletonHolder.INSTANCE;
	}

	private static class SingletonHolder
	{
		protected static final FakeTradersSpawnParser INSTANCE = new FakeTradersSpawnParser();
	}
}
