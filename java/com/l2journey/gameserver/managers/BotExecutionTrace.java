package com.l2journey.gameserver.managers;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Circular in-memory event trace log buffer (30 capacity) for tracking FakePlayer AI companion actions,
 * intent changes, navigation failures, and shop transaction errors.
 */
public class BotExecutionTrace
{
	private static final int MAX_CAPACITY = 30;
	private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

	private final Queue<String> _logs = new ConcurrentLinkedQueue<>();
	private int _consecutiveFailures = 0;
	private String _lastFailureCategory = null;

	public void addLog(String message)
	{
		String timestamp = LocalTime.now().format(TIME_FORMATTER);
		String entry = "[" + timestamp + "] " + message;
		
		_logs.add(entry);
		while (_logs.size() > MAX_CAPACITY)
		{
			_logs.poll();
		}
	}

	public void recordFailure(String category, String details)
	{
		addLog("FAILURE (" + category + "): " + details);

		if (category != null && category.equalsIgnoreCase(_lastFailureCategory))
		{
			_consecutiveFailures++;
		}
		else
		{
			_lastFailureCategory = category;
			_consecutiveFailures = 1;
		}
	}

	public void recordSuccess()
	{
		_consecutiveFailures = 0;
		_lastFailureCategory = null;
	}

	public int getConsecutiveFailures()
	{
		return _consecutiveFailures;
	}

	public String getLastFailureCategory()
	{
		return _lastFailureCategory;
	}

	public List<String> getLogs()
	{
		return new ArrayList<>(_logs);
	}

	public String getFormattedTrace()
	{
		StringBuilder sb = new StringBuilder();
		for (String log : _logs)
		{
			sb.append(log).append("\n");
		}
		return sb.toString();
	}

	public void clear()
	{
		_logs.clear();
		_consecutiveFailures = 0;
		_lastFailureCategory = null;
	}
}
