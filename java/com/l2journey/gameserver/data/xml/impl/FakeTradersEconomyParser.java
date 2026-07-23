package com.l2journey.gameserver.data.xml.impl;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import com.l2journey.commons.util.IXmlReader;
import com.l2journey.gameserver.model.fake.EconomyItem;
import com.l2journey.gameserver.model.fake.EconomyProfile;
import com.l2journey.gameserver.model.fake.EconomyRecipe;

public class FakeTradersEconomyParser implements IXmlReader
{
	private static final Logger LOGGER = Logger.getLogger(FakeTradersEconomyParser.class.getName());

	private final Map<String, EconomyProfile> _profiles = new ConcurrentHashMap<>();

	protected FakeTradersEconomyParser()
	{
		load();
	}

	@Override
	public synchronized void load()
	{
		_profiles.clear();
		parseDatapackFile("config/npcs/fake_traders_economy.xml");
		LOGGER.info(getClass().getSimpleName() + ": Loaded " + _profiles.size() + " economy profiles.");
	}

	@Override
	public void parseDocument(Document document, File file)
	{
		forEach(document, n ->
		{
			if ("economy".equalsIgnoreCase(n.getNodeName()))
			{
				forEach(n, d ->
				{
					if ("marketProfile".equalsIgnoreCase(d.getNodeName()) || "craftProfile".equalsIgnoreCase(d.getNodeName()))
					{
						final NamedNodeMap attrs = d.getAttributes();
						if (attrs != null && attrs.getNamedItem("id") != null)
						{
							final String profileId = attrs.getNamedItem("id").getNodeValue();
							final EconomyProfile profile = new EconomyProfile(profileId);

							forEach(d, itemNode ->
							{
								if ("item".equalsIgnoreCase(itemNode.getNodeName()))
								{
									try
									{
										final int id = parseInteger(itemNode.getAttributes(), "id");
										final String name = parseString(itemNode.getAttributes(), "name");
										final long minPrice = parseLong(itemNode.getAttributes(), "minPrice");
										final long maxPrice = parseLong(itemNode.getAttributes(), "maxPrice");
										final long minQty = parseLong(itemNode.getAttributes(), "minQty");
										final long maxQty = parseLong(itemNode.getAttributes(), "maxQty");
										profile.addItem(new EconomyItem(id, name, minPrice, maxPrice, minQty, maxQty));
									}
									catch (Exception e)
									{
										LOGGER.warning(getClass().getSimpleName() + ": Error parsing item in profile " + profileId);
									}
								}
								else if ("recipe".equalsIgnoreCase(itemNode.getNodeName()))
								{
									try
									{
										final int id = parseInteger(itemNode.getAttributes(), "id");
										final String name = parseString(itemNode.getAttributes(), "name");
										final long minFee = parseLong(itemNode.getAttributes(), "minFee");
										final long maxFee = parseLong(itemNode.getAttributes(), "maxFee");
										profile.addRecipe(new EconomyRecipe(id, name, minFee, maxFee));
									}
									catch (Exception e)
									{
										LOGGER.warning(getClass().getSimpleName() + ": Error parsing recipe in profile " + profileId);
									}
								}
							});
							_profiles.put(profileId, profile);
						}
					}
				});
			}
		});
	}

	public EconomyProfile getProfile(String id)
	{
		return _profiles.get(id);
	}

	public static FakeTradersEconomyParser getInstance()
	{
		return SingletonHolder.INSTANCE;
	}

	private static class SingletonHolder
	{
		protected static final FakeTradersEconomyParser INSTANCE = new FakeTradersEconomyParser();
	}
}
