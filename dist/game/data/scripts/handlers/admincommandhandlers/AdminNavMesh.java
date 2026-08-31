package handlers.admincommandhandlers;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

import com.l2journey.commons.threads.ThreadPool;
import com.l2journey.gameserver.handler.IAdminCommandHandler;
import com.l2journey.gameserver.model.Location;
import com.l2journey.gameserver.model.actor.Player;

public class AdminNavMesh implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_rec_path",
		"admin_stop_rec"
	};
	
	private static final Map<Integer, RecordingSession> _recordingSessions = new ConcurrentHashMap<>();
	
	private static class RecordingSession
	{
		String routeName;
		List<Location> points = new ArrayList<>();
		ScheduledFuture<?> task;
	}
	
	@Override
	public boolean useAdminCommand(String command, Player activeChar)
	{
		if (command.startsWith("admin_rec_path"))
		{
			String[] parts = command.split(" ");
			if (parts.length < 2)
			{
				activeChar.sendMessage("Usage: //admin_rec_path <RouteName>");
				return false;
			}
			
			String routeName = parts[1];
			if (_recordingSessions.containsKey(activeChar.getObjectId()))
			{
				activeChar.sendMessage("You are already recording a path. Use //admin_stop_rec first.");
				return false;
			}
			
			RecordingSession session = new RecordingSession();
			session.routeName = routeName;
			session.points.add(new Location(activeChar.getX(), activeChar.getY(), activeChar.getZ()));
			
			session.task = ThreadPool.scheduleAtFixedRate(() ->
			{
				if (activeChar == null || !activeChar.isOnline())
				{
					stopRecording(activeChar);
					return;
				}
				
				Location lastLoc = session.points.get(session.points.size() - 1);
				double distance = Math.hypot(activeChar.getX() - lastLoc.getX(), activeChar.getY() - lastLoc.getY());
				
				// Record a point if moved more than 500 units
				if (distance >= 500)
				{
					session.points.add(new Location(activeChar.getX(), activeChar.getY(), activeChar.getZ()));
					activeChar.sendMessage("Point added: " + session.points.size());
				}
			}, 1000, 1000);
			
			_recordingSessions.put(activeChar.getObjectId(), session);
			activeChar.sendMessage("Started recording NavMesh route: " + routeName);
		}
		else if (command.startsWith("admin_stop_rec"))
		{
			stopRecording(activeChar);
		}
		
		return true;
	}
	
	private void stopRecording(Player player)
	{
		RecordingSession session = _recordingSessions.remove(player.getObjectId());
		if (session == null)
		{
			player.sendMessage("You are not recording a NavMesh route.");
			return;
		}
		
		if (session.task != null)
		{
			session.task.cancel(false);
		}
		
		// Export to GeoJSON
		exportGeoJson(player, session);
	}
	
	private void exportGeoJson(Player player, RecordingSession session)
	{
		try
		{
			File dir = new File("data/navmesh");
			if (!dir.exists())
			{
				dir.mkdirs();
			}
			
			File file = new File(dir, session.routeName + ".geojson");
			try (FileWriter writer = new FileWriter(file))
			{
				writer.write("{\n");
				writer.write("  \"type\": \"FeatureCollection\",\n");
				writer.write("  \"features\": [\n");
				writer.write("    {\n");
				writer.write("      \"type\": \"Feature\",\n");
				writer.write("      \"geometry\": {\n");
				writer.write("        \"type\": \"LineString\",\n");
				writer.write("        \"coordinates\": [\n");
				
				for (int i = 0; i < session.points.size(); i++)
				{
					Location loc = session.points.get(i);
					writer.write("          [" + loc.getX() + ", " + loc.getY() + ", " + loc.getZ() + "]");
					if (i < session.points.size() - 1)
					{
						writer.write(",");
					}
					writer.write("\n");
				}
				
				writer.write("        ]\n");
				writer.write("      },\n");
				writer.write("      \"properties\": {\n");
				writer.write("        \"name\": \"" + session.routeName + "\"\n");
				writer.write("      }\n");
				writer.write("    }\n");
				writer.write("  ]\n");
				writer.write("}\n");
			}
			
			player.sendMessage("NavMesh route '" + session.routeName + "' saved with " + session.points.size() + " points at data/navmesh/" + file.getName());
		}
		catch (Exception e)
		{
			player.sendMessage("Error saving NavMesh route: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
}
