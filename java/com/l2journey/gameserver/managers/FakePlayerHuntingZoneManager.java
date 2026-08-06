package com.l2journey.gameserver.managers;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.l2journey.commons.util.Rnd;
import com.l2journey.gameserver.model.Location;
import com.l2journey.gameserver.model.actor.instance.FakePlayer;

/**
 * Parses and manages dynamic XML-configured Hunting Zones and Waypoints (SINGLE, LINE, CIRCLE)
 * for AI FakePlayers and Companions.
 */
public class FakePlayerHuntingZoneManager
{
	private static final Logger LOGGER = Logger.getLogger(FakePlayerHuntingZoneManager.class.getName());

	public enum WaypointType
	{
		SINGLE,
		LINE,
		CIRCLE
	}

	public static class HuntingWaypoint
	{
		private final String _id;
		private final WaypointType _type;
		private final int _searchRadius;
		private final List<Location> _points;

		public HuntingWaypoint(String id, WaypointType type, int searchRadius, List<Location> points)
		{
			_id = id;
			_type = type;
			_searchRadius = searchRadius;
			_points = points;
		}

		public String getId() { return _id; }
		public WaypointType getType() { return _type; }
		public int getSearchRadius() { return _searchRadius; }
		public List<Location> getPoints() { return _points; }

		public Location getPoint(int index)
		{
			if (_points.isEmpty()) return null;
			int safeIdx = Math.max(0, Math.min(index, _points.size() - 1));
			return _points.get(safeIdx);
		}
	}

	public static class HuntingZone
	{
		private final String _id;
		private final String _name;
		private final int _minLevel;
		private final int _maxLevel;
		private final Location _townCenter;
		private final List<HuntingWaypoint> _waypoints;

		public HuntingZone(String id, String name, int minLevel, int maxLevel, Location townCenter, List<HuntingWaypoint> waypoints)
		{
			_id = id;
			_name = name;
			_minLevel = minLevel;
			_maxLevel = maxLevel;
			_townCenter = townCenter;
			_waypoints = waypoints;
		}

		public String getId() { return _id; }
		public String getName() { return _name; }
		public int getMinLevel() { return _minLevel; }
		public int getMaxLevel() { return _maxLevel; }
		public Location getTownCenter() { return _townCenter; }
		public List<HuntingWaypoint> getWaypoints() { return _waypoints; }

		public HuntingWaypoint getRandomWaypoint()
		{
			if (_waypoints.isEmpty()) return null;
			return _waypoints.get(Rnd.get(_waypoints.size()));
		}
	}

	public static class BotWaypointTracker
	{
		private String _zoneId;
		private HuntingWaypoint _waypoint;
		private int _currentPointIndex;
		private boolean _reverseLine;

		public BotWaypointTracker(String zoneId, HuntingWaypoint waypoint)
		{
			_zoneId = zoneId;
			_waypoint = waypoint;
			_currentPointIndex = 0;
			_reverseLine = false;
		}

		public String getZoneId() { return _zoneId; }
		public HuntingWaypoint getWaypoint() { return _waypoint; }
		public int getCurrentPointIndex() { return _currentPointIndex; }

		public Location getCurrentPoint()
		{
			return _waypoint != null ? _waypoint.getPoint(_currentPointIndex) : null;
		}

		public Location advanceToNextPoint()
		{
			if (_waypoint == null || _waypoint.getPoints().isEmpty()) return null;
			List<Location> pts = _waypoint.getPoints();

			if (_waypoint.getType() == WaypointType.SINGLE || pts.size() == 1)
			{
				return pts.get(0);
			}
			else if (_waypoint.getType() == WaypointType.CIRCLE)
			{
				_currentPointIndex = (_currentPointIndex + 1) % pts.size();
			}
			else if (_waypoint.getType() == WaypointType.LINE)
			{
				if (_reverseLine)
				{
					_currentPointIndex--;
					if (_currentPointIndex <= 0)
					{
						_currentPointIndex = 0;
						_reverseLine = false;
					}
				}
				else
				{
					_currentPointIndex++;
					if (_currentPointIndex >= pts.size() - 1)
					{
						_currentPointIndex = pts.size() - 1;
						_reverseLine = true;
					}
				}
			}
			return pts.get(_currentPointIndex);
		}
	}

	private final List<HuntingZone> _zones = new ArrayList<>();
	private final Map<Integer, BotWaypointTracker> _botTrackers = new ConcurrentHashMap<>();

	protected FakePlayerHuntingZoneManager()
	{
		loadXML();
	}

	public static FakePlayerHuntingZoneManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}

	private static class SingletonHolder
	{
		protected static final FakePlayerHuntingZoneManager INSTANCE = new FakePlayerHuntingZoneManager();
	}

	public void loadXML()
	{
		_zones.clear();
		File xmlFile = new File("./data/fakeplayer_hunting_zones.xml");
		if (!xmlFile.exists()) xmlFile = new File("./dist/game/data/fakeplayer_hunting_zones.xml");

		if (!xmlFile.exists())
		{
			LOGGER.warning("FakePlayerHuntingZoneManager: Config XML not found at " + xmlFile.getAbsolutePath() + ". Loading fallback zone.");
			loadFallbackZone();
			return;
		}

		try
		{
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(xmlFile);
			doc.getDocumentElement().normalize();

			NodeList zoneList = doc.getElementsByTagName("zone");
			for (int i = 0; i < zoneList.getLength(); i++)
			{
				Element zoneElem = (Element) zoneList.item(i);
				String id = zoneElem.getAttribute("id");
				String name = zoneElem.getAttribute("name");
				int minLvl = Integer.parseInt(zoneElem.getAttribute("minLevel"));
				int maxLvl = Integer.parseInt(zoneElem.getAttribute("maxLevel"));
				int townX = zoneElem.hasAttribute("townX") ? Integer.parseInt(zoneElem.getAttribute("townX")) : -84176;
				int townY = zoneElem.hasAttribute("townY") ? Integer.parseInt(zoneElem.getAttribute("townY")) : 243382;
				int townZ = zoneElem.hasAttribute("townZ") ? Integer.parseInt(zoneElem.getAttribute("townZ")) : -3729;

				List<HuntingWaypoint> waypoints = new ArrayList<>();
				NodeList wpList = zoneElem.getElementsByTagName("waypoint");
				for (int j = 0; j < wpList.getLength(); j++)
				{
					Element wpElem = (Element) wpList.item(j);
					String wpId = wpElem.getAttribute("id");
					WaypointType type = WaypointType.valueOf(wpElem.getAttribute("type").toUpperCase());
					int searchRadius = wpElem.hasAttribute("searchRadius") ? Integer.parseInt(wpElem.getAttribute("searchRadius")) : 1500;

					List<Location> points = new ArrayList<>();
					NodeList ptList = wpElem.getElementsByTagName("point");
					for (int k = 0; k < ptList.getLength(); k++)
					{
						Element ptElem = (Element) ptList.item(k);
						int x = Integer.parseInt(ptElem.getAttribute("x"));
						int y = Integer.parseInt(ptElem.getAttribute("y"));
						int z = Integer.parseInt(ptElem.getAttribute("z"));
						points.add(new Location(x, y, z));
					}
					waypoints.add(new HuntingWaypoint(wpId, type, searchRadius, points));
				}
				_zones.add(new HuntingZone(id, name, minLvl, maxLvl, new Location(townX, townY, townZ), waypoints));
			}
			LOGGER.info("FakePlayerHuntingZoneManager: Successfully loaded " + _zones.size() + " hunting zones from XML.");
		}
		catch (Exception e)
		{
			LOGGER.log(Level.SEVERE, "FakePlayerHuntingZoneManager: Failed parsing XML!", e);
			loadFallbackZone();
		}
	}

	private void loadFallbackZone()
	{
		List<Location> fallbackPoints = new ArrayList<>();
		fallbackPoints.add(new Location(-81000, 245000, -3650));
		HuntingWaypoint defaultWp = new HuntingWaypoint("fallback_wp", WaypointType.SINGLE, 1500, fallbackPoints);
		List<HuntingWaypoint> waypoints = new ArrayList<>();
		waypoints.add(defaultWp);
		_zones.add(new HuntingZone("TI_NORTH_FIELD", "Talking Island North Field", 1, 15, new Location(-84176, 243382, -3729), waypoints));
	}

	public HuntingZone getZoneForLevel(int level)
	{
		for (HuntingZone zone : _zones)
		{
			if (level >= zone.getMinLevel() && level <= zone.getMaxLevel())
			{
				return zone;
			}
		}
		// Return highest or lowest fallback
		return _zones.isEmpty() ? null : _zones.get(_zones.size() - 1);
	}

	public BotWaypointTracker getTracker(FakePlayer bot)
	{
		if (bot == null) return null;
		HuntingZone targetZone = getZoneForLevel(bot.getLevel());
		if (targetZone == null) return null;

		BotWaypointTracker tracker = _botTrackers.get(bot.getObjectId());
		if (tracker == null || !tracker.getZoneId().equals(targetZone.getId()))
		{
			HuntingWaypoint wp = targetZone.getRandomWaypoint();
			tracker = new BotWaypointTracker(targetZone.getId(), wp);
			_botTrackers.put(bot.getObjectId(), tracker);
		}
		return tracker;
	}

	public Location getCurrentOrNextPoint(FakePlayer bot)
	{
		BotWaypointTracker tracker = getTracker(bot);
		if (tracker == null) return null;

		Location current = tracker.getCurrentPoint();
		if (current != null && bot.isInsideRadius2D(current, 150))
		{
			return tracker.advanceToNextPoint();
		}
		return current;
	}

	public void switchWaypoint(FakePlayer bot)
	{
		if (bot == null) return;
		HuntingZone targetZone = getZoneForLevel(bot.getLevel());
		if (targetZone != null && !targetZone.getWaypoints().isEmpty())
		{
			HuntingWaypoint newWp = targetZone.getRandomWaypoint();
			_botTrackers.put(bot.getObjectId(), new BotWaypointTracker(targetZone.getId(), newWp));
			LOGGER.info("FakePlayerHuntingZoneManager: Bot " + bot.getName() + " trocou para o waypoint " + newWp.getId() + " (" + newWp.getType() + ").");
		}
	}
}
