package com.l2journey.gameserver.data.xml;

import java.io.File;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import com.l2journey.commons.util.IXmlReader;
import com.l2journey.gameserver.model.actor.holders.fakeplayer.CityCatalogHolder;
import com.l2journey.gameserver.model.actor.holders.fakeplayer.CityCatalogHolder.CatalogCategory;
import com.l2journey.gameserver.model.actor.holders.fakeplayer.CityCatalogHolder.CatalogItem;
import com.l2journey.gameserver.model.actor.holders.fakeplayer.FakeShopHolder;
import com.l2journey.gameserver.model.actor.holders.fakeplayer.FakeShopHolder.ShopType;

/**
 * XML Parser and Data Registry for City Catalogs and FakeShop profiles.
 */
public class FakeShopData implements IXmlReader
{
	private static final Logger LOGGER = Logger.getLogger(FakeShopData.class.getName());

	private final Map<String, CityCatalogHolder> _cityCatalogs = new ConcurrentHashMap<>();
	private final Map<String, FakeShopHolder> _fakeShops = new ConcurrentHashMap<>();

	protected FakeShopData()
	{
		load();
	}

	@Override
	public void load()
	{
		_cityCatalogs.clear();
		_fakeShops.clear();

		parseDatapackFile("data/fakeplayers/city_catalogs.xml");
		parseDatapackFile("data/fakeplayers/fake_shops.xml");

		LOGGER.info(getClass().getSimpleName() + ": Loaded " + _cityCatalogs.size() + " city catalogs and " + _fakeShops.size() + " fake shop profiles.");
	}

	@Override
	public void parseDocument(Document doc, File f)
	{
		for (Node n = doc.getFirstChild(); n != null; n = n.getNextSibling())
		{
			if ("catalogs".equalsIgnoreCase(n.getNodeName()))
			{
				parseCatalogs(n);
			}
			else if ("shops".equalsIgnoreCase(n.getNodeName()))
			{
				parseShops(n);
			}
		}
	}

	private void parseCatalogs(Node node)
	{
		for (Node catNode = node.getFirstChild(); catNode != null; catNode = catNode.getNextSibling())
		{
			if ("city".equalsIgnoreCase(catNode.getNodeName()))
			{
				final String name = parseString(catNode.getAttributes(), "name");
				if ((name == null) || name.isEmpty())
				{
					continue;
				}

				final CityCatalogHolder catalog = new CityCatalogHolder(name);

				for (Node categoryNode = catNode.getFirstChild(); categoryNode != null; categoryNode = categoryNode.getNextSibling())
				{
					final String nodeName = categoryNode.getNodeName().toLowerCase();
					if ("materials".equals(nodeName) || "supplies".equals(nodeName) || "items".equals(nodeName))
					{
						for (Node itemNode = categoryNode.getFirstChild(); itemNode != null; itemNode = itemNode.getNextSibling())
						{
							if ("item".equalsIgnoreCase(itemNode.getNodeName()))
							{
								final NamedNodeMap attrs = itemNode.getAttributes();
								final int itemId = parseInteger(attrs, "id");
								final long minCount = parseLong(attrs, "minCount", 1L);
								final long maxCount = parseLong(attrs, "maxCount", minCount);
								final long minPrice = parseLong(attrs, "minPrice", 100L);
								final long maxPrice = parseLong(attrs, "maxPrice", minPrice);

								final CatalogItem item = new CatalogItem(itemId, minCount, maxCount, minPrice, maxPrice);

								if ("materials".equals(nodeName))
								{
									catalog.addMaterial(item);
								}
								else if ("supplies".equals(nodeName))
								{
									catalog.addSupply(item);
								}
								else
								{
									catalog.addItem(item);
								}
							}
						}
					}
				}

				_cityCatalogs.put(name.toLowerCase(), catalog);
			}
		}
	}

	private void parseShops(Node node)
	{
		for (Node shopNode = node.getFirstChild(); shopNode != null; shopNode = shopNode.getNextSibling())
		{
			if ("fakeshop".equalsIgnoreCase(shopNode.getNodeName()))
			{
				final NamedNodeMap attrs = shopNode.getAttributes();
				final String name = parseString(attrs, "name");
				final String title = parseString(attrs, "title", "Store");
				final int classId = parseInteger(attrs, "classId", 53);
				final int level = parseInteger(attrs, "level", 40);
				final int x = parseInteger(attrs, "x", 0);
				final int y = parseInteger(attrs, "y", 0);
				final int z = parseInteger(attrs, "z", 0);
				final int heading = parseInteger(attrs, "heading", 0);
				final String city = parseString(attrs, "city", "Gludio");
				final String typeStr = parseString(attrs, "type", "SELL");
				final String catStr = parseString(attrs, "category", "MATERIALS");
				final int interval = parseInteger(attrs, "updateInterval", 30);
				final int maxItems = parseInteger(attrs, "maxItems", 5);

				ShopType shopType;
				try
				{
					shopType = ShopType.valueOf(typeStr.toUpperCase());
				}
				catch (Exception e)
				{
					shopType = ShopType.SELL;
				}

				CatalogCategory category;
				try
				{
					category = CatalogCategory.valueOf(catStr.toUpperCase());
				}
				catch (Exception e)
				{
					category = CatalogCategory.MATERIALS;
				}

				final FakeShopHolder holder = new FakeShopHolder(name, title, classId, level, x, y, z, heading, city, shopType, category, interval, maxItems);

				for (Node child = shopNode.getFirstChild(); child != null; child = child.getNextSibling())
				{
					if ("customitem".equalsIgnoreCase(child.getNodeName()) || "item".equalsIgnoreCase(child.getNodeName()))
					{
						final NamedNodeMap itemAttrs = child.getAttributes();
						final int itemId = parseInteger(itemAttrs, "id");
						final long count = parseLong(itemAttrs, "count", 1L);
						final long price = parseLong(itemAttrs, "price", 100L);

						holder.addCustomItem(new CatalogItem(itemId, count, count, price, price));
					}
				}

				_fakeShops.put(name.toLowerCase(), holder);
			}
		}
	}

	public CityCatalogHolder getCityCatalog(String cityName)
	{
		return _cityCatalogs.get(cityName.toLowerCase());
	}

	public FakeShopHolder getFakeShop(String name)
	{
		return _fakeShops.get(name.toLowerCase());
	}

	public Collection<FakeShopHolder> getFakeShops()
	{
		return _fakeShops.values();
	}

	public static FakeShopData getInstance()
	{
		return SingletonHolder.INSTANCE;
	}

	private static class SingletonHolder
	{
		protected static final FakeShopData INSTANCE = new FakeShopData();
	}
}
