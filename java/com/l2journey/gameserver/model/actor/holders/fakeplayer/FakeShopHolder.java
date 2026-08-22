package com.l2journey.gameserver.model.actor.holders.fakeplayer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.l2journey.gameserver.model.actor.holders.fakeplayer.CityCatalogHolder.CatalogCategory;
import com.l2journey.gameserver.model.actor.holders.fakeplayer.CityCatalogHolder.CatalogItem;

/**
 * Holder for a FakeShop definition parsed from XML configuration.
 */
public class FakeShopHolder
{
	public enum ShopType
	{
		BUY,
		SELL,
		CRAFT
	}

	private final String _name;
	private final String _title;
	private final int _classId;
	private final int _level;
	private final int _x;
	private final int _y;
	private final int _z;
	private final int _heading;
	private final String _cityName;
	private final ShopType _shopType;
	private final CatalogCategory _category;
	private final int _updateIntervalMinutes;
	private final int _maxItems;
	private final List<CatalogItem> _customItems = new ArrayList<>();

	public FakeShopHolder(String name, String title, int classId, int level, int x, int y, int z, int heading,
		String cityName, ShopType shopType, CatalogCategory category, int updateIntervalMinutes, int maxItems)
	{
		_name = name;
		_title = title;
		_classId = classId;
		_level = level;
		_x = x;
		_y = y;
		_z = z;
		_heading = heading;
		_cityName = cityName;
		_shopType = shopType;
		_category = category;
		_updateIntervalMinutes = updateIntervalMinutes > 0 ? updateIntervalMinutes : 60;
		_maxItems = maxItems > 0 ? Math.min(maxItems, 5) : 5;
	}

	public String getName()
	{
		return _name;
	}

	public String getTitle()
	{
		return _title;
	}

	public int getClassId()
	{
		return _classId;
	}

	public int getLevel()
	{
		return _level;
	}

	public int getX()
	{
		return _x;
	}

	public int getY()
	{
		return _y;
	}

	public int getZ()
	{
		return _z;
	}

	public int getHeading()
	{
		return _heading;
	}

	public String getCityName()
	{
		return _cityName;
	}

	public ShopType getShopType()
	{
		return _shopType;
	}

	public CatalogCategory getCategory()
	{
		return _category;
	}

	public int getUpdateIntervalMinutes()
	{
		return _updateIntervalMinutes;
	}

	public int getMaxItems()
	{
		return _maxItems;
	}

	public void addCustomItem(CatalogItem item)
	{
		_customItems.add(item);
	}

	public List<CatalogItem> getCustomItems()
	{
		return Collections.unmodifiableList(_customItems);
	}
}
