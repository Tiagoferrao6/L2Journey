package com.l2journey.gameserver.data.xml.impl;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import com.l2journey.commons.util.IXmlReader;
import com.l2journey.gameserver.model.actor.dna.HunterDNA;

public class FakeHuntersDNAParser implements IXmlReader
{
	private static final Logger LOGGER = Logger.getLogger(FakeHuntersDNAParser.class.getName());
	private final Map<String, HunterDNA> _dnaProfiles = new ConcurrentHashMap<>();

	protected FakeHuntersDNAParser()
	{
		load();
	}

	@Override
	public synchronized void load()
	{
		_dnaProfiles.clear();
		parseDatapackFile("config/npcs/fake_hunters_dna.xml");
		LOGGER.info(getClass().getSimpleName() + ": Loaded " + _dnaProfiles.size() + " hunter DNA profiles.");
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
						final NamedNodeMap attrs = profileNode.getAttributes();
						if (attrs != null && attrs.getNamedItem("id") != null)
						{
							final String id = attrs.getNamedItem("id").getNodeValue();
							int preservation = 50;
							int sociability = 50;
							int greed = 50;
							int aggressiveness = 50;
							int altruism = 50;
							String shift = "ALL_DAY";

							for (Node child = profileNode.getFirstChild(); child != null; child = child.getNextSibling())
							{
								if ("traits".equalsIgnoreCase(child.getNodeName()))
								{
									final NamedNodeMap traitAttrs = child.getAttributes();
									preservation = parseInteger(traitAttrs, "preservation", 50);
									sociability = parseInteger(traitAttrs, "sociability", 50);
									greed = parseInteger(traitAttrs, "greed", 50);
									aggressiveness = parseInteger(traitAttrs, "aggressiveness", 50);
									altruism = parseInteger(traitAttrs, "altruism", 50);
								}
								else if ("shift".equalsIgnoreCase(child.getNodeName()))
								{
									shift = child.getTextContent().trim();
								}
							}

							final HunterDNA dna = new HunterDNA(id, preservation, sociability, greed, aggressiveness, altruism, shift);
							_dnaProfiles.put(id, dna);
						}
					}
				});
			}
		});
	}

	public HunterDNA getDNA(String profileId)
	{
		return _dnaProfiles.get(profileId);
	}

	public Map<String, HunterDNA> getProfiles()
	{
		return _dnaProfiles;
	}

	public static FakeHuntersDNAParser getInstance()
	{
		return SingletonHolder.INSTANCE;
	}

	private static class SingletonHolder
	{
		protected static final FakeHuntersDNAParser INSTANCE = new FakeHuntersDNAParser();
	}
}
