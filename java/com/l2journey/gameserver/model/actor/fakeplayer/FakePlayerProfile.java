package com.l2journey.gameserver.model.actor.fakeplayer;

/**
 * Data model representing a persistent Fake Player profile (DNA, schedule, state).
 */
public class FakePlayerProfile
{
	private int _fakeId;
	private String _botType = "HUNTER"; // HUNTER or TRADER
	private int _classId;
	private int _dualClassId = -1;
	private int _aggressiveness = 5; // 1 to 10
	private int _courage = 5; // 1 to 10
	private int _partyTendency = 5; // 1 to 10
	private String _shift = "DAY"; // DAY, NIGHT, 4H, 8H
	private String _zoneId = "GLUDIO";
	private int _x;
	private int _y;
	private int _z;
	private int _heading;
	private String _inventory;
	private int _partyId;
	private boolean _isActive;
	private long _lastActiveTime;
	
	// Anti-Stuck mechanism
	private int _lastX;
	private int _lastY;
	private int _stuckTicks;

	public FakePlayerProfile()
	{
	}

	public FakePlayerProfile(int fakeId, String botType, int classId, int aggressiveness, int courage, int partyTendency, String shift, String zoneId)
	{
		_fakeId = fakeId;
		_botType = botType;
		_classId = classId;
		_aggressiveness = aggressiveness;
		_courage = courage;
		_partyTendency = partyTendency;
		_shift = shift;
		_zoneId = zoneId;
	}

	public int getFakeId()
	{
		return _fakeId;
	}

	public void setFakeId(int fakeId)
	{
		_fakeId = fakeId;
	}

	public String getBotType()
	{
		return _botType;
	}

	public void setBotType(String botType)
	{
		_botType = botType;
	}

	public int getClassId()
	{
		return _classId;
	}

	public void setClassId(int classId)
	{
		_classId = classId;
	}

	public int getDualClassId()
	{
		return _dualClassId;
	}

	public void setDualClassId(int dualClassId)
	{
		_dualClassId = dualClassId;
	}

	public int getAggressiveness()
	{
		return _aggressiveness;
	}

	public void setAggressiveness(int aggressiveness)
	{
		_aggressiveness = aggressiveness;
	}

	public int getCourage()
	{
		return _courage;
	}

	public void setCourage(int courage)
	{
		_courage = courage;
	}

	public int getPartyTendency()
	{
		return _partyTendency;
	}

	public void setPartyTendency(int partyTendency)
	{
		_partyTendency = partyTendency;
	}

	public String getShift()
	{
		return _shift;
	}

	public void setShift(String shift)
	{
		_shift = shift;
	}

	public String getZoneId()
	{
		return _zoneId;
	}

	public void setZoneId(String zoneId)
	{
		_zoneId = zoneId;
	}

	public int getX()
	{
		return _x;
	}

	public void setX(int x)
	{
		_x = x;
	}

	public int getY()
	{
		return _y;
	}

	public void setY(int y)
	{
		_y = y;
	}

	public int getZ()
	{
		return _z;
	}

	public void setZ(int z)
	{
		_z = z;
	}

	public int getHeading()
	{
		return _heading;
	}

	public void setHeading(int heading)
	{
		_heading = heading;
	}

	public String getInventory()
	{
		return _inventory;
	}

	public void setInventory(String inventory)
	{
		_inventory = inventory;
	}

	public int getPartyId()
	{
		return _partyId;
	}

	public void setPartyId(int partyId)
	{
		_partyId = partyId;
	}

	public boolean isActive()
	{
		return _isActive;
	}

	public void setActive(boolean active)
	{
		_isActive = active;
	}

	public long getLastActiveTime()
	{
		return _lastActiveTime;
	}

	public void setLastActiveTime(long lastActiveTime)
	{
		_lastActiveTime = lastActiveTime;
	}
	
	public int getLastX()
	{
		return _lastX;
	}
	
	public void setLastX(int lastX)
	{
		_lastX = lastX;
	}
	
	public int getLastY()
	{
		return _lastY;
	}
	
	public void setLastY(int lastY)
	{
		_lastY = lastY;
	}
	
	public int getStuckTicks()
	{
		return _stuckTicks;
	}
	
	public void setStuckTicks(int stuckTicks)
	{
		_stuckTicks = stuckTicks;
	}
}
