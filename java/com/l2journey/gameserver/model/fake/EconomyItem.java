package com.l2journey.gameserver.model.fake;

public class EconomyItem
{
	private final int _itemId;
	private final String _name;
	private final long _minPrice;
	private final long _maxPrice;
	private final long _minQty;
	private final long _maxQty;

	public EconomyItem(int itemId, String name, long minPrice, long maxPrice, long minQty, long maxQty)
	{
		_itemId = itemId;
		_name = name;
		_minPrice = minPrice;
		_maxPrice = maxPrice;
		_minQty = minQty;
		_maxQty = maxQty;
	}

	public int getItemId()
	{
		return _itemId;
	}

	public String getName()
	{
		return _name;
	}

	public long getMinPrice()
	{
		return _minPrice;
	}

	public long getMaxPrice()
	{
		return _maxPrice;
	}

	public long getMinQty()
	{
		return _minQty;
	}

	public long getMaxQty()
	{
		return _maxQty;
	}
}
