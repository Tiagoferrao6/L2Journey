package com.l2journey.gameserver.managers;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * Rate Limiter and Anti-Spam Manager for LLM Bot Chat Channels.
 * Enforces minimum delay (5 seconds default) between messages per channel to prevent flooding.
 */
public class LLMRateLimiter
{
	private static final Logger LOGGER = Logger.getLogger(LLMRateLimiter.class.getName());
	private static final long DEFAULT_COOLDOWN_MS = 5000; // 5s interval

	private final Map<String, Long> _lastMessageTimestamps = new ConcurrentHashMap<>();

	protected LLMRateLimiter()
	{
		LOGGER.info("LLMRateLimiter: Initialized Chat Anti-Spam & Rate Limiting Engine.");
	}

	public static LLMRateLimiter getInstance()
	{
		return SingletonHolder.INSTANCE;
	}

	private static class SingletonHolder
	{
		protected static final LLMRateLimiter INSTANCE = new LLMRateLimiter();
	}

	/**
	 * Checks if bot can send message on channel without violating rate limit.
	 */
	public boolean canSendMessage(int botObjectId, String channel)
	{
		String key = botObjectId + ":" + channel;
		Long lastTime = _lastMessageTimestamps.get(key);
		if (lastTime == null)
		{
			return true;
		}

		long elapsed = System.currentTimeMillis() - lastTime;
		return elapsed >= DEFAULT_COOLDOWN_MS;
	}

	/**
	 * Registers timestamp when bot sends message on channel.
	 */
	public void registerMessage(int botObjectId, String channel)
	{
		String key = botObjectId + ":" + channel;
		_lastMessageTimestamps.put(key, System.currentTimeMillis());
	}

	/**
	 * Clears cooldown timestamps (useful for testing or server reset).
	 */
	public void reset()
	{
		_lastMessageTimestamps.clear();
	}
}
