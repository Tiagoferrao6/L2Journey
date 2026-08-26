package com.l2journey.gameserver.model.actor.holders.fakeplayer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Holds item catalogs for a specific city categorized by materials, supplies, and items.
 */
public class CityCatalogHolder
{
	public static class CatalogItem
	{
		private final int _itemId;
		private final long _minCount;
		private final long _maxCount;
		private final long _minPrice;
		private final long _maxPrice;
		private final int _minEnchant;
		private final int _maxEnchant;

		public CatalogItem(int itemId, long minCount, long maxCount, long minPrice, long maxPrice)
		{
			this(itemId, minCount, maxCount, minPrice, maxPrice, 0, 0);
		}

		public CatalogItem(int itemId, long minCount, long maxCount, long minPrice, long maxPrice, int minEnchant, int maxEnchant)
		{
			_itemId = itemId;
			_minCount = minCount;
			_maxCount = maxCount;
			_minPrice = minPrice;
			_maxPrice = maxPrice;
			_minEnchant = minEnchant;
			_maxEnchant = maxEnchant;
		}

		public int getItemId()
		{
			return _itemId;
		}

		public long getMinCount()
		{
			return _minCount;
		}

		public long getMaxCount()
		{
			return _maxCount;
		}

		public long getMinPrice()
		{
			return _minPrice;
		}

		public long getMaxPrice()
		{
			return _maxPrice;
		}

		public int getMinEnchant()
		{
			return _minEnchant;
		}

		public int getMaxEnchant()
		{
			return _maxEnchant;
		}
	}

	public enum CatalogCategory
	{
		MATERIALS,
		SUPPLIES,
		ITEMS,
		CUSTOM
	}

	private final String _cityName;
	private final List<CatalogItem> _materials = new ArrayList<>();
	private final List<CatalogItem> _supplies = new ArrayList<>();
	private final List<CatalogItem> _items = new ArrayList<>();

	public CityCatalogHolder(String cityName)
	{
		_cityName = cityName;
	}

	public String getCityName()
	{
		return _cityName;
	}

	public void addMaterial(CatalogItem item)
	{
		_materials.add(item);
	}

	public void addSupply(CatalogItem item)
	{
		_supplies.add(item);
	}

	public void addItem(CatalogItem item)
	{
		_items.add(item);
	}

	public List<CatalogItem> getMaterials()
	{
		return Collections.unmodifiableList(_materials);
	}

	public List<CatalogItem> getSupplies()
	{
		return Collections.unmodifiableList(_supplies);
	}

	public List<CatalogItem> getItems()
	{
		return Collections.unmodifiableList(_items);
	}

	public List<CatalogItem> getByCategory(CatalogCategory category)
	{
		switch (category)
		{
			case MATERIALS:
				return getMaterials();
			case SUPPLIES:
				return getSupplies();
			case ITEMS:
				return getItems();
			default:
				return Collections.emptyList();
		}
	}
}
