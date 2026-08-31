package com.l2journey.gameserver.managers;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.l2journey.Config;
import com.l2journey.commons.threads.ThreadPool;
import com.l2journey.commons.util.StringUtil;
import com.l2journey.gameserver.network.IAuditablePacket;

public class PlayerActionLogger implements Runnable
{
	private static final PlayerActionLogger _instance = new PlayerActionLogger();
	
	private final ConcurrentLinkedQueue<AuditEntry> _queue = new ConcurrentLinkedQueue<>();
	private final SimpleDateFormat _dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	private final SimpleDateFormat _timestampFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
	
	private static class AuditEntry
	{
		public final long timestamp;
		public final String charName;
		public final String direction; // "IN" or "OUT"
		public final String packetName;
		public final Map<String, Object> data;
		
		public AuditEntry(String charName, String direction, String packetName, Map<String, Object> data)
		{
			this.timestamp = System.currentTimeMillis();
			this.charName = charName;
			this.direction = direction;
			this.packetName = packetName;
			this.data = data;
		}
	}
	
	protected PlayerActionLogger()
	{
		if (Config.AUDIT_ENABLED)
		{
			// Start background worker thread
			ThreadPool.scheduleAtFixedRate(this, 1000, 1000); // 1 second intervals
		}
	}
	
	public static PlayerActionLogger getInstance()
	{
		return _instance;
	}
	
	public void logPacket(String charName, String direction, IAuditablePacket packet)
	{
		if (!Config.AUDIT_ENABLED)
			return;
			
		if (Config.AUDIT_MODE.equals("LIST") && !Config.AUDIT_PLAYER_LIST.contains(charName.toLowerCase()))
			return;
			
		final Map<String, Object> data = packet.getAuditData();
		if (data == null)
			return;
			
		_queue.add(new AuditEntry(charName, direction, packet.getClass().getSimpleName(), data));
	}
	
	@Override
	public void run()
	{
		if (_queue.isEmpty())
			return;
			
		final String dateStr = _dateFormat.format(new Date());
		final String baseDir = Config.AUDIT_OUTPUT_DIRECTORY + dateStr + "/";
		final File dir = new File(baseDir);
		if (!dir.exists())
			dir.mkdirs();
			
		AuditEntry entry;
		while ((entry = _queue.poll()) != null)
		{
			final File file = new File(baseDir + entry.charName + ".jsonl");
			try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, true)))
			{
				bw.write(toJsonL(entry));
				bw.newLine();
			}
			catch (IOException e)
			{
				// Handle silently to not spam console, or log to general logger
			}
		}
	}
	
	private String toJsonL(AuditEntry entry)
	{
		final StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("\"timestamp\":\"").append(_timestampFormat.format(new Date(entry.timestamp))).append("\",");
		sb.append("\"charName\":\"").append(entry.charName).append("\",");
		sb.append("\"direction\":\"").append(entry.direction).append("\",");
		sb.append("\"packet\":\"").append(entry.packetName).append("\",");
		sb.append("\"data\":{");
		
		boolean first = true;
		for (Map.Entry<String, Object> kv : entry.data.entrySet())
		{
			if (!first)
				sb.append(",");
			first = false;
			
			sb.append("\"").append(kv.getKey()).append("\":");
			if (kv.getValue() instanceof String)
			{
				String val = ((String) kv.getValue()).replace("\"", "\\\""); // escape quotes
				sb.append("\"").append(val).append("\"");
			}
			else
			{
				sb.append(kv.getValue());
			}
		}
		
		sb.append("}}");
		return sb.toString();
	}
}
