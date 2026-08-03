package com.l2journey.gameserver.model.actor.dna;

import java.util.ArrayList;
import java.util.List;

public class HunterProfile
{
	private final String _profileId;
	private final int _townReturnDelay;
	private final boolean _allowKS;
	private final boolean _pickupItems;
	private final boolean _groupAssist;
	private final int _kitingDistance;
	private final List<String> _assignedRoutes = new ArrayList<>();

	public HunterProfile(String profileId, int townReturnDelay, boolean allowKS, boolean pickupItems, boolean groupAssist, int kitingDistance)
	{
		_profileId = profileId;
		_townReturnDelay = townReturnDelay;
		_allowKS = allowKS;
		_pickupItems = pickupItems;
		_groupAssist = groupAssist;
		_kitingDistance = kitingDistance;
	}

	public String getProfileId()
	{
		return _profileId;
	}

	public int getTownReturnDelay()
	{
		return _townReturnDelay;
	}

	public boolean isAllowKS()
	{
		return _allowKS;
	}

	public boolean isPickupItems()
	{
		return _pickupItems;
	}

	public boolean isGroupAssist()
	{
		return _groupAssist;
	}

	public int getKitingDistance()
	{
		return _kitingDistance;
	}

	public List<String> getAssignedRoutes()
	{
		return _assignedRoutes;
	}

	public void addRoute(String routeId)
	{
		_assignedRoutes.add(routeId);
	}
}
