package com.l2journey.gameserver.data.xml.impl;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.l2journey.commons.util.IXmlReader;
import com.l2journey.gameserver.model.actor.dna.HunterProfile;

public class FakeHunterProfilesParser implements IXmlReader
{
	private static final Logger LOGGER = Logger.getLogger(FakeHunterProfilesParser.class.getName());
	private final Map<String, HunterProfile> _profiles = new ConcurrentHashMap<>();

	protected FakeHunterProfilesParser()
	{
		load();
	}

	@Override
	public synchronized void load()
	{
		_profiles.clear();
		parseDatapackFile("config/npcs/fake_hunter_profiles.xml");
		LOGGER.info(getClass().getSimpleName() + ": Loaded " + _profiles.size() + " fake hunter profiles.");
	}

	@Override
	public void parseDocument(Document document, File file)
	{
		forEach(document, n ->
		{
			if ("profiles".equalsIgnoreCase(n.getNodeName()))
			{
				forEach(n, profileNode ->
				{
					if ("profile".equalsIgnoreCase(profileNode.getNodeName()))
					{
						String id = parseString(profileNode.getAttributes(), "id", "default");
						int townReturnDelay = 20;
						boolean allowKS = false;
						boolean pickupItems = true;
						boolean groupAssist = true;
						int kitingDistance = 300;
						HunterProfile profile = null;

						for (Node child = profileNode.getFirstChild(); child != null; child = child.getNextSibling())
						{
							if ("townReturnDelay".equalsIgnoreCase(child.getNodeName()))
							{
								townReturnDelay = Integer.parseInt(child.getTextContent().trim());
							}
							else if ("allowKS".equalsIgnoreCase(child.getNodeName()))
							{
								allowKS = Boolean.parseBoolean(child.getTextContent().trim());
							}
							else if ("pickupItems".equalsIgnoreCase(child.getNodeName()))
							{
								pickupItems = Boolean.parseBoolean(child.getTextContent().trim());
							}
							else if ("groupAssist".equalsIgnoreCase(child.getNodeName()))
							{
								groupAssist = Boolean.parseBoolean(child.getTextContent().trim());
							}
							else if ("kitingDistance".equalsIgnoreCase(child.getNodeName()))
							{
								kitingDistance = Integer.parseInt(child.getTextContent().trim());
							}
						}

						final HunterProfile finalProfile = new HunterProfile(id, townReturnDelay, allowKS, pickupItems, groupAssist, kitingDistance);

						for (Node child = profileNode.getFirstChild(); child != null; child = child.getNextSibling())
						{
							if ("assignedRoutes".equalsIgnoreCase(child.getNodeName()))
							{
								forEach(child, routeNode ->
								{
									if ("route".equalsIgnoreCase(routeNode.getNodeName()))
									{
										finalProfile.addRoute(routeNode.getTextContent().trim());
									}
								});
							}
						}

						_profiles.put(id, finalProfile);
					}
				});
			}
		});
	}

	public HunterProfile getProfile(String id)
	{
		return _profiles.getOrDefault(id, _profiles.get("cautious_hunter"));
	}

	public static FakeHunterProfilesParser getInstance()
	{
		return SingletonHolder.INSTANCE;
	}

	private static class SingletonHolder
	{
		protected static final FakeHunterProfilesParser INSTANCE = new FakeHunterProfilesParser();
	}
}
