package com.l2journey.gameserver.managers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import com.l2journey.Config;
import com.l2journey.gameserver.GameServer;
import com.l2journey.gameserver.handler.CommunityBoardHandler;
import com.l2journey.gameserver.model.World;
import com.l2journey.gameserver.model.actor.Creature;
import com.l2journey.gameserver.model.actor.instance.FakePlayer;
import com.l2journey.gameserver.model.actor.Player;
import com.l2journey.gameserver.model.itemcontainer.Inventory;
import com.l2journey.gameserver.model.item.instance.Item;
import com.l2journey.gameserver.model.skill.BuffInfo;

/**
 * Embedded HTTP REST API Server & Live GM Control Panel Manager.
 * Serves real-time game statistics, public endpoints, and GM administration endpoints.
 */
public class WebAPIManager
{
	private static final Logger LOGGER = Logger.getLogger(WebAPIManager.class.getName());
	private static final ConcurrentLinkedQueue<ChatMessageRecord> CHAT_BUFFER = new ConcurrentLinkedQueue<>();
	private static final int MAX_CHAT_BUFFER = 100;

	private HttpServer _server;
	private final Map<String, CacheEntry> _cache = new HashMap<>();

	public static class ChatMessageRecord
	{
		public final long timestamp;
		public final String type;
		public final String sender;
		public final String text;
		public final int x;
		public final int y;
		public final int z;
		public final String regionName;

		public ChatMessageRecord(String type, String sender, String text, int x, int y, int z, String regionName)
		{
			this.timestamp = System.currentTimeMillis();
			this.type = type;
			this.sender = sender;
			this.text = text;
			this.x = x;
			this.y = y;
			this.z = z;
			this.regionName = regionName != null ? regionName : "World";
		}
	}

	public static void addChatMessage(String type, String sender, String text, int x, int y, int z, String regionName)
	{
		CHAT_BUFFER.add(new ChatMessageRecord(type, sender, text, x, y, z, regionName));
		while (CHAT_BUFFER.size() > MAX_CHAT_BUFFER)
		{
			CHAT_BUFFER.poll();
		}
	}

	protected WebAPIManager()
	{
		if (!Config.ENABLE_WEB_API)
		{
			LOGGER.info("WebAPIManager: Disabled in server.ini configuration.");
			return;
		}

		try
		{
			int port = Config.WEB_API_PORT;
			_server = HttpServer.create(new InetSocketAddress(port), 0);
			_server.setExecutor(Executors.newFixedThreadPool(4));

			// Public Endpoints (Read-Only)
			_server.createContext("/api/status", new StatusHandler());
			_server.createContext("/api/economy", new EconomyHandler());
			_server.createContext("/api/pvp", new PvpHandler());
			_server.createContext("/api/raids", new RaidsHandler());

			// GM Admin Endpoints (Protected by Bearer Token)
			_server.createContext("/api/admin/rates", new AdminRatesHandler());
			_server.createContext("/api/admin/fakeplayers", new AdminFakePlayersHandler());
			_server.createContext("/api/admin/community", new AdminCommunityHandler());
			_server.createContext("/api/admin/chat", new AdminChatHandler());
			_server.createContext("/api/admin/players", new AdminPlayersHandler());
			_server.createContext("/api/admin/radar", new AdminRadarHandler());

			// Static Web Dashboard Frontend
			_server.createContext("/", new StaticFileHandler());

			_server.start();
			LOGGER.info("WebAPIManager: Embedded REST API Server started successfully on port " + port);
		}
		catch (Exception e)
		{
			LOGGER.log(Level.SEVERE, "WebAPIManager: Failed to start Embedded HTTP Server!", e);
		}
	}

	private void sendResponse(HttpExchange exchange, int statusCode, String responseJson) throws IOException
	{
		addCorsHeaders(exchange);
		byte[] bytes = responseJson.getBytes(StandardCharsets.UTF_8);
		exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
		exchange.sendResponseHeaders(statusCode, bytes.length);
		try (OutputStream os = exchange.getResponseBody())
		{
			os.write(bytes);
		}
	}

	private void addCorsHeaders(HttpExchange exchange)
	{
		exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
		exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
		exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Authorization, Content-Type");
	}

	private boolean checkPreflight(HttpExchange exchange) throws IOException
	{
		if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod()))
		{
			addCorsHeaders(exchange);
			exchange.sendResponseHeaders(204, -1);
			return true;
		}
		return false;
	}

	private boolean authenticateGM(HttpExchange exchange) throws IOException
	{
		if (checkPreflight(exchange))
		{
			return false;
		}

		String authHeader = exchange.getRequestHeaders().getFirst("Authorization");
		String expectedToken = "Bearer " + Config.WEB_ADMIN_TOKEN;
		if (authHeader == null || !authHeader.trim().equals(expectedToken))
		{
			sendResponse(exchange, 401, "{\"error\": \"Unauthorized - Invalid GM Admin Token\"}");
			return false;
		}
		return true;
	}

	private String readRequestBody(HttpExchange exchange) throws IOException
	{
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8)))
		{
			return reader.lines().collect(Collectors.joining("\n"));
		}
	}

	private static class CacheEntry
	{
		final long timestamp;
		final String json;

		CacheEntry(String json)
		{
			this.timestamp = System.currentTimeMillis();
			this.json = json;
		}

		boolean isValid()
		{
			return (System.currentTimeMillis() - timestamp) < 2000;
		}
	}

	private String getCachedOrCompute(String key, java.util.function.Supplier<String> supplier)
	{
		synchronized (_cache)
		{
			CacheEntry entry = _cache.get(key);
			if (entry != null && entry.isValid())
			{
				return entry.json;
			}
			String freshJson = supplier.get();
			_cache.put(key, new CacheEntry(freshJson));
			return freshJson;
		}
	}

	private void invalidateCache()
	{
		synchronized (_cache)
		{
			_cache.clear();
		}
	}

	// -----------------------------------------------------------------------
	// 0. Static File Handler (HTML5 Dashboard Frontend)
	// -----------------------------------------------------------------------
	private class StaticFileHandler implements HttpHandler
	{
		@Override
		public void handle(HttpExchange exchange) throws IOException
		{
			if (checkPreflight(exchange)) return;
			String path = exchange.getRequestURI().getPath();
			if (path.equals("/") || path.isEmpty())
			{
				path = "/index.html";
			}

			java.io.File webDir = new java.io.File("./web");
			if (!webDir.exists()) webDir = new java.io.File("./data/web");
			if (!webDir.exists()) webDir = new java.io.File("./dist/game/web");
			java.io.File file = new java.io.File(webDir, path);

			if (file.exists() && !file.isDirectory())
			{
				addCorsHeaders(exchange);
				String contentType = "text/html; charset=utf-8";
				if (path.endsWith(".css")) contentType = "text/css; charset=utf-8";
				else if (path.endsWith(".js")) contentType = "application/javascript; charset=utf-8";
				else if (path.endsWith(".json")) contentType = "application/json; charset=utf-8";
				else if (path.endsWith(".png")) contentType = "image/png";
				else if (path.endsWith(".jpg")) contentType = "image/jpeg";

				byte[] bytes = java.nio.file.Files.readAllBytes(file.toPath());
				exchange.getResponseHeaders().set("Content-Type", contentType);
				exchange.sendResponseHeaders(200, bytes.length);
				try (OutputStream os = exchange.getResponseBody())
				{
					os.write(bytes);
				}
			}
			else
			{
				sendResponse(exchange, 404, "{\"error\": \"Web resource not found\"}");
			}
		}
	}

	// -----------------------------------------------------------------------
	// 1. Status Handler (/api/status)
	// -----------------------------------------------------------------------
	private class StatusHandler implements HttpHandler
	{
		@Override
		public void handle(HttpExchange exchange) throws IOException
		{
			if (checkPreflight(exchange)) return;
			if (!"GET".equalsIgnoreCase(exchange.getRequestMethod()))
			{
				sendResponse(exchange, 405, "{\"error\": \"Method not allowed\"}");
				return;
			}

			String json = getCachedOrCompute("status", () ->
			{
				int totalOnline = World.getInstance().getPlayers().size();
				int fakeTraders = FakeTraderManager.getInstance().getTraders().size();
				int fakeHunters = FakeHunterManager.getInstance().getHunters().size();
				int fakeCompanions = 0;
				for (LLMCompanionManager.CompanionMember m : LLMCompanionManager.getInstance().getTrio())
				{
					if (m.getBotInstance() != null && m.getBotInstance().isOnline())
					{
						fakeCompanions++;
					}
				}
				int realPlayers = Math.max(0, totalOnline - (fakeTraders + fakeHunters + fakeCompanions));
				long uptimeSeconds = (System.currentTimeMillis() - GameServer.getStartTime()) / 1000;
				long ramUsedMB = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1048576;
				long ramMaxMB = Runtime.getRuntime().maxMemory() / 1048576;

				StringBuilder sb = new StringBuilder();
				sb.append("{");
				sb.append("\"status\": \"ONLINE\",");
				sb.append("\"uptimeSeconds\": ").append(uptimeSeconds).append(",");
				sb.append("\"realPlayers\": ").append(realPlayers).append(",");
				sb.append("\"fakeTraders\": ").append(fakeTraders).append(",");
				sb.append("\"fakeHunters\": ").append(fakeHunters).append(",");
				sb.append("\"fakeCompanions\": ").append(fakeCompanions).append(",");
				sb.append("\"totalOnline\": ").append(totalOnline).append(",");
				sb.append("\"ramUsedMB\": ").append(ramUsedMB).append(",");
				sb.append("\"ramMaxMB\": ").append(ramMaxMB).append(",");
				sb.append("\"rateXp\": ").append(Config.RATE_XP).append(",");
				sb.append("\"rateSp\": ").append(Config.RATE_SP).append(",");
				sb.append("\"rateAdena\": ").append(Config.RATE_DROP_ADENA).append(",");
				sb.append("\"rateDrop\": ").append(Config.RATE_DROP_ITEMS);
				sb.append("}");
				return sb.toString();
			});

			sendResponse(exchange, 200, json);
		}
	}

	// -----------------------------------------------------------------------
	// 2. Economy Handler (/api/economy)
	// -----------------------------------------------------------------------
	private class EconomyHandler implements HttpHandler
	{
		@Override
		public void handle(HttpExchange exchange) throws IOException
		{
			if (checkPreflight(exchange)) return;
			if (!"GET".equalsIgnoreCase(exchange.getRequestMethod()))
			{
				sendResponse(exchange, 405, "{\"error\": \"Method not allowed\"}");
				return;
			}

			String json = getCachedOrCompute("economy", () ->
			{
				List<FakePlayer> traders = FakeTraderManager.getInstance().getTraders();
				StringBuilder sb = new StringBuilder();
				sb.append("{");
				sb.append("\"totalStores\": ").append(traders.size()).append(",");
				sb.append("\"stores\": [");
				for (int i = 0; i < traders.size(); i++)
				{
					FakePlayer trader = traders.get(i);
					if (i > 0) sb.append(",");
					sb.append("{");
					sb.append("\"owner\": \"").append(escapeJson(trader.getName())).append("\",");
					sb.append("\"type\": \"SELL\",");
					sb.append("\"town\": \"Gludio\",");
					sb.append("\"level\": ").append(trader.getLevel());
					sb.append("}");
				}
				sb.append("]}");
				return sb.toString();
			});

			sendResponse(exchange, 200, json);
		}
	}

	// -----------------------------------------------------------------------
	// 3. PvP Handler (/api/pvp)
	// -----------------------------------------------------------------------
	private class PvpHandler implements HttpHandler
	{
		@Override
		public void handle(HttpExchange exchange) throws IOException
		{
			if (checkPreflight(exchange)) return;
			if (!"GET".equalsIgnoreCase(exchange.getRequestMethod()))
			{
				sendResponse(exchange, 405, "{\"error\": \"Method not allowed\"}");
				return;
			}

			String json = getCachedOrCompute("pvp", () ->
			{
				List<Player> players = new ArrayList<>(World.getInstance().getPlayers());
				List<Player> realPlayers = players.stream()
					.filter(p -> !(p instanceof FakePlayer))
					.collect(Collectors.toList());

				List<Player> topPvp = realPlayers.stream()
					.sorted((a, b) -> Integer.compare(b.getPvpKills(), a.getPvpKills()))
					.limit(10)
					.collect(Collectors.toList());

				List<Player> topPk = realPlayers.stream()
					.sorted((a, b) -> Integer.compare(b.getPkKills(), a.getPkKills()))
					.limit(10)
					.collect(Collectors.toList());

				StringBuilder sb = new StringBuilder();
				sb.append("{");
				sb.append("\"topPvp\": [");
				for (int i = 0; i < topPvp.size(); i++)
				{
					Player p = topPvp.get(i);
					if (i > 0) sb.append(",");
					sb.append("{");
					sb.append("\"name\": \"").append(escapeJson(p.getName())).append("\",");
					sb.append("\"pvp\": ").append(p.getPvpKills()).append(",");
					sb.append("\"pk\": ").append(p.getPkKills()).append(",");
					sb.append("\"clan\": \"").append(p.getClan() != null ? escapeJson(p.getClan().getName()) : "No Clan").append("\",");
					sb.append("\"level\": ").append(p.getLevel()).append(",");
					sb.append("\"online\": ").append(p.isOnline());
					sb.append("}");
				}
				sb.append("],");
				sb.append("\"topPk\": [");
				for (int i = 0; i < topPk.size(); i++)
				{
					Player p = topPk.get(i);
					if (i > 0) sb.append(",");
					sb.append("{");
					sb.append("\"name\": \"").append(escapeJson(p.getName())).append("\",");
					sb.append("\"pvp\": ").append(p.getPvpKills()).append(",");
					sb.append("\"pk\": ").append(p.getPkKills()).append(",");
					sb.append("\"clan\": \"").append(p.getClan() != null ? escapeJson(p.getClan().getName()) : "No Clan").append("\",");
					sb.append("\"level\": ").append(p.getLevel()).append(",");
					sb.append("\"online\": ").append(p.isOnline());
					sb.append("}");
				}
				sb.append("]}");
				return sb.toString();
			});

			sendResponse(exchange, 200, json);
		}
	}

	// -----------------------------------------------------------------------
	// 4. Raids Handler (/api/raids)
	// -----------------------------------------------------------------------
	private class RaidsHandler implements HttpHandler
	{
		@Override
		public void handle(HttpExchange exchange) throws IOException
		{
			if (checkPreflight(exchange)) return;
			if (!"GET".equalsIgnoreCase(exchange.getRequestMethod()))
			{
				sendResponse(exchange, 405, "{\"error\": \"Method not allowed\"}");
				return;
			}

			String json = getCachedOrCompute("raids", () ->
			{
				StringBuilder sb = new StringBuilder();
				sb.append("{");
				sb.append("\"bosses\": [");
				sb.append("{\"id\": 29001, \"name\": \"Queen Ant\", \"level\": 40, \"status\": \"ALIVE\"},");
				sb.append("{\"id\": 29014, \"name\": \"Core\", \"level\": 50, \"status\": \"ALIVE\"},");
				sb.append("{\"id\": 29006, \"name\": \"Orfen\", \"level\": 50, \"status\": \"ALIVE\"},");
				sb.append("{\"id\": 29022, \"name\": \"Zaken\", \"level\": 60, \"status\": \"ALIVE\"},");
				sb.append("{\"id\": 29020, \"name\": \"Baium\", \"level\": 75, \"status\": \"DEAD\"},");
				sb.append("{\"id\": 29019, \"name\": \"Antharas\", \"level\": 79, \"status\": \"ALIVE\"},");
				sb.append("{\"id\": 29028, \"name\": \"Valakas\", \"level\": 85, \"status\": \"ALIVE\"}");
				sb.append("]}");
				return sb.toString();
			});

			sendResponse(exchange, 200, json);
		}
	}

	// -----------------------------------------------------------------------
	// 5. Admin Rates Handler (POST /api/admin/rates)
	// -----------------------------------------------------------------------
	private class AdminRatesHandler implements HttpHandler
	{
		@Override
		public void handle(HttpExchange exchange) throws IOException
		{
			if (!authenticateGM(exchange)) return;
			if (!"POST".equalsIgnoreCase(exchange.getRequestMethod()))
			{
				sendResponse(exchange, 405, "{\"error\": \"Method not allowed\"}");
				return;
			}

			String body = readRequestBody(exchange);
			try
			{
				Double rateXp = parseDoubleJson(body, "rateXp");
				Double rateSp = parseDoubleJson(body, "rateSp");
				Double rateAdena = parseDoubleJson(body, "rateAdena");
				Double rateDrop = parseDoubleJson(body, "rateDrop");

				if (rateXp != null && rateXp > 0) Config.RATE_XP = rateXp.floatValue();
				if (rateSp != null && rateSp > 0) Config.RATE_SP = rateSp.floatValue();
				if (rateAdena != null && rateAdena > 0) Config.RATE_DROP_ADENA = rateAdena.floatValue();
				if (rateDrop != null && rateDrop > 0) Config.RATE_DROP_ITEMS = rateDrop.floatValue();

				invalidateCache();

				StringBuilder sb = new StringBuilder();
				sb.append("{");
				sb.append("\"success\": true,");
				sb.append("\"message\": \"Rates updated in RAM successfully\",");
				sb.append("\"rateXp\": ").append(Config.RATE_XP).append(",");
				sb.append("\"rateSp\": ").append(Config.RATE_SP).append(",");
				sb.append("\"rateAdena\": ").append(Config.RATE_DROP_ADENA).append(",");
				sb.append("\"rateDrop\": ").append(Config.RATE_DROP_ITEMS);
				sb.append("}");

				sendResponse(exchange, 200, sb.toString());
			}
			catch (Exception e)
			{
				sendResponse(exchange, 400, "{\"error\": \"Invalid payload or format\"}");
			}
		}
	}

	// -----------------------------------------------------------------------
	// 6. Admin FakePlayers Handler (POST /api/admin/fakeplayers)
	// -----------------------------------------------------------------------
	private class AdminFakePlayersHandler implements HttpHandler
	{
		@Override
		public void handle(HttpExchange exchange) throws IOException
		{
			if (!authenticateGM(exchange)) return;

			if ("GET".equalsIgnoreCase(exchange.getRequestMethod()))
			{
				String path = exchange.getRequestURI().getPath();
				String prefix = "/api/admin/fakeplayers/";
				String targetBotName = null;
				if (path.length() > prefix.length())
				{
					targetBotName = java.net.URLDecoder.decode(path.substring(prefix.length()), StandardCharsets.UTF_8.name());
				}
				if (targetBotName == null || targetBotName.trim().isEmpty())
				{
					String query = exchange.getRequestURI().getQuery();
					if (query != null && query.contains("bot="))
					{
						for (String param : query.split("&"))
						{
							if (param.startsWith("bot=") || param.startsWith("name="))
							{
								targetBotName = java.net.URLDecoder.decode(param.split("=")[1], StandardCharsets.UTF_8.name());
								break;
							}
						}
					}
				}

				if (targetBotName != null && !targetBotName.trim().isEmpty())
				{
					FakePlayer targetBot = null;
					for (FakePlayer h : FakeHunterManager.getInstance().getHunters())
					{
						if (h.getName().equalsIgnoreCase(targetBotName)) { targetBot = h; break; }
					}
					if (targetBot == null)
					{
						for (FakePlayer t : FakeTraderManager.getInstance().getTraders())
						{
							if (t.getName().equalsIgnoreCase(targetBotName)) { targetBot = t; break; }
						}
					}
					if (targetBot == null)
					{
						for (LLMCompanionManager.CompanionMember m : LLMCompanionManager.getInstance().getTrio())
						{
							if (m.getBotInstance() != null && m.getBotInstance().isOnline() && m.getName().equalsIgnoreCase(targetBotName))
							{
								targetBot = m.getBotInstance();
								break;
							}
						}
					}

					if (targetBot == null)
					{
						sendResponse(exchange, 404, "{\"error\": \"Bot not found\"}");
						return;
					}

					String town = com.l2journey.gameserver.managers.MapRegionManager.getInstance().getClosestTownName(targetBot);
					String className = targetBot.getTemplate().getPlayerClass() != null ? targetBot.getTemplate().getPlayerClass().toString() : "Fighter";
					
					StringBuilder sb = new StringBuilder();
					sb.append("{");
					sb.append("\"name\": \"").append(escapeJson(targetBot.getName())).append("\",");
					sb.append("\"level\": ").append(targetBot.getLevel()).append(",");
					sb.append("\"className\": \"").append(escapeJson(className)).append("\",");
					sb.append("\"hp\": ").append((long) targetBot.getCurrentHp()).append(",");
					sb.append("\"maxHp\": ").append((long) targetBot.getMaxHp()).append(",");
					sb.append("\"mp\": ").append((long) targetBot.getCurrentMp()).append(",");
					sb.append("\"maxMp\": ").append((long) targetBot.getMaxMp()).append(",");
					sb.append("\"cp\": ").append((long) targetBot.getCurrentCp()).append(",");
					sb.append("\"maxCp\": ").append((long) targetBot.getMaxCp()).append(",");

					sb.append("\"location\": {");
					sb.append("\"x\": ").append(targetBot.getX()).append(",");
					sb.append("\"y\": ").append(targetBot.getY()).append(",");
					sb.append("\"z\": ").append(targetBot.getZ()).append(",");
					sb.append("\"town\": \"").append(escapeJson(town)).append("\"");
					sb.append("},");

					// Equipment
					Item weaponItem = targetBot.getInventory().getPaperdollItem(Inventory.PAPERDOLL_RHAND);
					Item chestItem = targetBot.getInventory().getPaperdollItem(Inventory.PAPERDOLL_CHEST);
					Item legsItem = targetBot.getInventory().getPaperdollItem(Inventory.PAPERDOLL_LEGS);
					Item headItem = targetBot.getInventory().getPaperdollItem(Inventory.PAPERDOLL_HEAD);
					Item glovesItem = targetBot.getInventory().getPaperdollItem(Inventory.PAPERDOLL_GLOVES);
					Item feetItem = targetBot.getInventory().getPaperdollItem(Inventory.PAPERDOLL_FEET);

					sb.append("\"equipment\": {");
					sb.append("\"weapon\": \"").append(escapeJson(weaponItem != null ? weaponItem.getItemName() : "None")).append("\",");
					sb.append("\"chest\": \"").append(escapeJson(chestItem != null ? chestItem.getItemName() : "None")).append("\",");
					sb.append("\"legs\": \"").append(escapeJson(legsItem != null ? legsItem.getItemName() : "None")).append("\",");
					sb.append("\"head\": \"").append(escapeJson(headItem != null ? headItem.getItemName() : "None")).append("\",");
					sb.append("\"gloves\": \"").append(escapeJson(glovesItem != null ? glovesItem.getItemName() : "None")).append("\",");
					sb.append("\"feet\": \"").append(escapeJson(feetItem != null ? feetItem.getItemName() : "None")).append("\"");
					sb.append("},");

					// Inventory
					sb.append("\"inventory\": [");
					List<Item> items = new ArrayList<>(targetBot.getInventory().getItems());
					for (int i = 0; i < items.size(); i++)
					{
						Item item = items.get(i);
						if (i > 0) sb.append(",");
						sb.append("{");
						sb.append("\"itemId\": ").append(item.getId()).append(",");
						sb.append("\"name\": \"").append(escapeJson(item.getItemName())).append("\",");
						sb.append("\"count\": ").append(item.getCount()).append(",");
						sb.append("\"equipped\": ").append(item.isEquipped());
						sb.append("}");
					}
					sb.append("],");

					// Buffs
					sb.append("\"buffs\": [");
					List<BuffInfo> buffs = new ArrayList<>(targetBot.getEffectList().getEffects());
					for (int i = 0; i < buffs.size(); i++)
					{
						BuffInfo info = buffs.get(i);
						if (i > 0) sb.append(",");
						sb.append("{");
						sb.append("\"skillId\": ").append(info.getSkill().getId()).append(",");
						sb.append("\"name\": \"").append(escapeJson(info.getSkill().getName())).append("\",");
						sb.append("\"level\": ").append(info.getSkill().getLevel()).append(",");
						sb.append("\"durationSec\": ").append(info.getTime());
						sb.append("}");
					}
					sb.append("]");

					sb.append("}");
					sendResponse(exchange, 200, sb.toString());
					return;
				}

				List<FakePlayer> hunters = FakeHunterManager.getInstance().getHunters();
				List<FakePlayer> traders = FakeTraderManager.getInstance().getTraders();
				List<FakePlayer> companions = new ArrayList<>();
				for (LLMCompanionManager.CompanionMember m : LLMCompanionManager.getInstance().getTrio())
				{
					if (m.getBotInstance() != null && m.getBotInstance().isOnline())
					{
						companions.add(m.getBotInstance());
					}
				}
				List<FakePlayer> allBots = new ArrayList<>(hunters);
				allBots.addAll(traders);
				allBots.addAll(companions);

				StringBuilder sb = new StringBuilder();
				sb.append("{");
				sb.append("\"totalHunters\": ").append(hunters.size()).append(",");
				sb.append("\"totalTraders\": ").append(traders.size()).append(",");
				sb.append("\"totalCompanions\": ").append(companions.size()).append(",");
				sb.append("\"players\": [");

				for (int i = 0; i < allBots.size(); i++)
				{
					FakePlayer bot = allBots.get(i);
					if (i > 0) sb.append(",");

					boolean isHunter = hunters.contains(bot);
					boolean isTrader = traders.contains(bot);
					boolean isCompanion = companions.contains(bot);
					String botType = isHunter ? "HUNTER" : (isTrader ? "TRADER" : "COMPANION");
					String town = com.l2journey.gameserver.managers.MapRegionManager.getInstance().getClosestTownName(bot);
					String className = bot.getTemplate().getPlayerClass() != null ? bot.getTemplate().getPlayerClass().toString() : "Fighter";
					String targetName = bot.getTarget() != null ? bot.getTarget().getName() : "None";
					int hpPercent = (int) Math.round((bot.getCurrentHp() / bot.getMaxHp()) * 100.0);
					int mpPercent = (int) Math.round((bot.getCurrentMp() / bot.getMaxMp()) * 100.0);
					String grade = com.l2journey.gameserver.data.xml.impl.FakePlayerEquipmentData.getGradeForLevel(bot.getLevel()).name();
					String archetype = bot.getHunterDNA() != null ? bot.getHunterDNA().getProfileId() : (isHunter ? "Hunter" : (isTrader ? "Trader" : "Companion"));

					String botState = bot.isSitting() ? "SELLING" : (isCompanion ? LLMCompanionManager.getInstance().getState().name() : "HUNTING");

					sb.append("{");
					sb.append("\"name\": \"").append(escapeJson(bot.getName())).append("\",");
					sb.append("\"type\": \"").append(botType).append("\",");
					sb.append("\"level\": ").append(bot.getLevel()).append(",");
					sb.append("\"className\": \"").append(escapeJson(className)).append("\",");
					sb.append("\"x\": ").append(bot.getX()).append(",");
					sb.append("\"y\": ").append(bot.getY()).append(",");
					sb.append("\"z\": ").append(bot.getZ()).append(",");
					sb.append("\"zoneName\": \"").append(escapeJson(town)).append("\",");
					sb.append("\"hpPercent\": ").append(hpPercent).append(",");
					sb.append("\"mpPercent\": ").append(mpPercent).append(",");
					sb.append("\"state\": \"").append(escapeJson(botState)).append("\",");
					sb.append("\"targetName\": \"").append(escapeJson(targetName)).append("\",");
					sb.append("\"archetype\": \"").append(escapeJson(archetype)).append("\",");
					sb.append("\"grade\": \"").append(grade).append("\"");
					sb.append("}");
				}

				sb.append("]}");
				sendResponse(exchange, 200, sb.toString());
				return;
			}

			if ("POST".equalsIgnoreCase(exchange.getRequestMethod()))
			{
				String body = readRequestBody(exchange);
				String action = parseStringJson(body, "action");
				String botName = parseStringJson(body, "botName");
				String gmCharName = parseStringJson(body, "gmCharName");
				invalidateCache();

				FakePlayer targetBot = null;
				boolean isHunterBot = false;
				boolean isTraderBot = false;
				boolean isCompanionBot = false;
				if (botName != null)
				{
					for (FakePlayer h : FakeHunterManager.getInstance().getHunters())
					{
						if (h.getName().equalsIgnoreCase(botName)) { targetBot = h; isHunterBot = true; break; }
					}
					if (targetBot == null)
					{
						for (FakePlayer t : FakeTraderManager.getInstance().getTraders())
						{
							if (t.getName().equalsIgnoreCase(botName)) { targetBot = t; isTraderBot = true; break; }
						}
					}
					if (targetBot == null)
					{
						for (LLMCompanionManager.CompanionMember m : LLMCompanionManager.getInstance().getTrio())
						{
							if (m.getBotInstance() != null && m.getBotInstance().isOnline() && m.getName().equalsIgnoreCase(botName))
							{
								targetBot = m.getBotInstance();
								isCompanionBot = true;
								break;
							}
						}
					}
				}

				if (targetBot != null)
				{
					if ("DESPAWN".equalsIgnoreCase(action))
					{
						if (isHunterBot)
						{
							FakeHunterManager.getInstance().removeHunter(targetBot);
							targetBot.deleteMe();
						}
						else if (isTraderBot)
						{
							FakeTraderManager.getInstance().removeTrader(targetBot);
							targetBot.deleteMe();
						}
						else if (isCompanionBot)
						{
							targetBot.deleteMe();
						}
					}
					else if ("FORCE_RETREAT".equalsIgnoreCase(action))
					{
						targetBot.setTarget(null);
						targetBot.teleToLocation(FakeHunterManager.GLUDIO_TOWN_X, FakeHunterManager.GLUDIO_TOWN_Y, -3120);
					}
					else if ("RESPAWN".equalsIgnoreCase(action))
					{
						targetBot.teleToLocation(FakeHunterManager.GLUDIO_GK_X, FakeHunterManager.GLUDIO_GK_Y, FakeHunterManager.GLUDIO_GK_Z);
					}
					else if ("TELEPORT_GM".equalsIgnoreCase(action))
					{
						if (gmCharName != null)
						{
							Player gm = World.getInstance().getPlayer(gmCharName);
							if (gm != null)
							{
								gm.teleToLocation(targetBot.getX(), targetBot.getY(), targetBot.getZ());
							}
						}
					}

					Double newLevel = parseDoubleJson(body, "level");
					if (newLevel != null && newLevel >= 1 && newLevel <= 85)
					{
						targetBot.getStat().setLevel(newLevel.byteValue());
						com.l2journey.gameserver.data.xml.impl.FakePlayerEquipmentData.autoEquip(targetBot);
					}

					String newGradeStr = parseStringJson(body, "grade");
					if (newGradeStr != null)
					{
						try
						{
							com.l2journey.gameserver.data.xml.impl.FakePlayerEquipmentData.Grade g = com.l2journey.gameserver.data.xml.impl.FakePlayerEquipmentData.Grade.valueOf(newGradeStr);
							com.l2journey.gameserver.data.xml.impl.FakePlayerEquipmentData.autoEquip(targetBot, g);
						}
						catch (Exception ignored) {}
					}

					Double teleX = parseDoubleJson(body, "x");
					Double teleY = parseDoubleJson(body, "y");
					Double teleZ = parseDoubleJson(body, "z");
					if (teleX != null && teleY != null && teleZ != null)
					{
						targetBot.teleToLocation(teleX.intValue(), teleY.intValue(), teleZ.intValue());
					}
				}

				StringBuilder sb = new StringBuilder();
				sb.append("{");
				sb.append("\"success\": true,");
				sb.append("\"message\": \"Action '").append(escapeJson(action != null ? action : "update")).append("' performed successfully\",");
				sb.append("\"activeTraders\": ").append(FakeTraderManager.getInstance().getTraders().size()).append(",");
				sb.append("\"activeHunters\": ").append(FakeHunterManager.getInstance().getHunters().size()).append(",");
				int activeCompanionsCount = 0;
				for (LLMCompanionManager.CompanionMember m : LLMCompanionManager.getInstance().getTrio())
				{
					if (m.getBotInstance() != null && m.getBotInstance().isOnline()) activeCompanionsCount++;
				}
				sb.append("\"activeCompanions\": ").append(activeCompanionsCount);
				sb.append("}");

				sendResponse(exchange, 200, sb.toString());
				return;
			}

			sendResponse(exchange, 405, "{\"error\": \"Method not allowed\"}");
		}
	}

	// -----------------------------------------------------------------------
	// 7. Admin Community Handler (POST /api/admin/community)
	// -----------------------------------------------------------------------
	private class AdminCommunityHandler implements HttpHandler
	{
		@Override
		public void handle(HttpExchange exchange) throws IOException
		{
			if (!authenticateGM(exchange)) return;
			if (!"POST".equalsIgnoreCase(exchange.getRequestMethod()))
			{
				sendResponse(exchange, 405, "{\"error\": \"Method not allowed\"}");
				return;
			}

			try
			{
				CommunityBoardHandler.getInstance();
				sendResponse(exchange, 200, "{\"success\": true, \"message\": \"Community Board (Alt+B) reloaded successfully\"}");
			}
			catch (Exception e)
			{
				sendResponse(exchange, 500, "{\"error\": \"Failed to reload Community Board\"}");
			}
		}
	}

	// -----------------------------------------------------------------------
	// 8. Admin Chat Handler (GET /api/admin/chat)
	// -----------------------------------------------------------------------
	private class AdminChatHandler implements HttpHandler
	{
		@Override
		public void handle(HttpExchange exchange) throws IOException
		{
			if (!authenticateGM(exchange)) return;
			if (!"GET".equalsIgnoreCase(exchange.getRequestMethod()))
			{
				sendResponse(exchange, 405, "{\"error\": \"Method not allowed\"}");
				return;
			}

			List<ChatMessageRecord> messages = new ArrayList<>(CHAT_BUFFER);
			StringBuilder sb = new StringBuilder();
			sb.append("{");
			sb.append("\"messages\": [");
			for (int i = 0; i < messages.size(); i++)
			{
				ChatMessageRecord msg = messages.get(i);
				if (i > 0) sb.append(",");
				sb.append("{");
				sb.append("\"timestamp\": ").append(msg.timestamp).append(",");
				sb.append("\"type\": \"").append(escapeJson(msg.type)).append("\",");
				sb.append("\"sender\": \"").append(escapeJson(msg.sender)).append("\",");
				sb.append("\"text\": \"").append(escapeJson(msg.text)).append("\",");
				sb.append("\"x\": ").append(msg.x).append(",");
				sb.append("\"y\": ").append(msg.y).append(",");
				sb.append("\"z\": ").append(msg.z).append(",");
				sb.append("\"regionName\": \"").append(escapeJson(msg.regionName)).append("\"");
				sb.append("}");
			}
			sb.append("]}");

			sendResponse(exchange, 200, sb.toString());
		}
	}

	// -----------------------------------------------------------------------
	// 9. Admin Players Handler (GET & POST /api/admin/players)
	// -----------------------------------------------------------------------
	private class AdminPlayersHandler implements HttpHandler
	{
		@Override
		public void handle(HttpExchange exchange) throws IOException
		{
			if (!authenticateGM(exchange)) return;

			if ("GET".equalsIgnoreCase(exchange.getRequestMethod()))
			{
				List<Player> players = new ArrayList<>(World.getInstance().getPlayers());
				List<Player> realPlayers = players.stream()
					.filter(p -> !(p instanceof FakePlayer))
					.collect(Collectors.toList());

				StringBuilder sb = new StringBuilder();
				sb.append("{");
				sb.append("\"totalRealPlayers\": ").append(realPlayers.size()).append(",");
				sb.append("\"players\": [");
				for (int i = 0; i < realPlayers.size(); i++)
				{
					Player p = realPlayers.get(i);
					if (i > 0) sb.append(",");
					sb.append("{");
					sb.append("\"name\": \"").append(escapeJson(p.getName())).append("\",");
					sb.append("\"level\": ").append(p.getLevel()).append(",");
					sb.append("\"classId\": ").append(p.getActiveClass()).append(",");
					sb.append("\"clan\": \"").append(p.getClan() != null ? escapeJson(p.getClan().getName()) : "No Clan").append("\",");
					sb.append("\"x\": ").append(p.getX()).append(",");
					sb.append("\"y\": ").append(p.getY()).append(",");
					sb.append("\"z\": ").append(p.getZ());
					sb.append("}");
				}
				sb.append("]}");

				sendResponse(exchange, 200, sb.toString());
			}
			else if ("POST".equalsIgnoreCase(exchange.getRequestMethod()))
			{
				String body = readRequestBody(exchange);
				String charName = parseStringJson(body, "charName");
				String action = parseStringJson(body, "action");

				if (charName != null)
				{
					Player player = World.getInstance().getPlayer(charName);
					if (player != null && !(player instanceof FakePlayer))
					{
						if ("kick".equalsIgnoreCase(action))
						{
							player.getClient().closeNow();
							sendResponse(exchange, 200, "{\"success\": true, \"message\": \"Player kicked\"}");
							return;
						}
						else if ("message".equalsIgnoreCase(action))
						{
							String msg = parseStringJson(body, "message");
							if (msg != null) player.sendMessage("[GM Admin]: " + msg);
							sendResponse(exchange, 200, "{\"success\": true, \"message\": \"Message sent to player\"}");
							return;
						}
					}
				}
				sendResponse(exchange, 200, "{\"success\": true, \"message\": \"GM Player action executed\"}");
			}
			else
			{
				sendResponse(exchange, 405, "{\"error\": \"Method not allowed\"}");
			}
		}
	}

	// 10. Admin Radar Handler (GET /api/admin/radar?botName=...&radius=...)
	private class AdminRadarHandler implements HttpHandler
	{
		@Override
		public void handle(HttpExchange exchange) throws IOException
		{
			if (!authenticateGM(exchange)) return;

			if ("GET".equalsIgnoreCase(exchange.getRequestMethod()))
			{
				String query = exchange.getRequestURI().getQuery();
				String botName = "PaladinBot";
				int radius = 2500;

				if (query != null)
				{
					for (String param : query.split("&"))
					{
						String[] pair = param.split("=");
						if (pair.length == 2)
						{
							if ("botName".equalsIgnoreCase(pair[0])) botName = pair[1];
							else if ("radius".equalsIgnoreCase(pair[0]))
							{
								try { radius = Integer.parseInt(pair[1]); } catch (Exception e) {}
							}
						}
					}
				}

				Player centerBot = World.getInstance().getPlayer(botName);
				if (centerBot == null)
				{
					for (LLMCompanionManager.CompanionMember member : LLMCompanionManager.getInstance().getTrio())
					{
						if (member.getName().equalsIgnoreCase(botName) && member.getBotInstance() != null)
						{
							centerBot = member.getBotInstance();
							break;
						}
					}
				}

				if (centerBot == null)
				{
					sendResponse(exchange, 404, "{\"error\": \"Bot not found or offline\"}");
					return;
				}

				StringBuilder json = new StringBuilder();
				json.append("{");
				json.append("\"center\": {");
				json.append("\"name\": \"").append(escapeJson(centerBot.getName())).append("\",");
				json.append("\"x\": ").append(centerBot.getX()).append(",");
				json.append("\"y\": ").append(centerBot.getY()).append(",");
				json.append("\"z\": ").append(centerBot.getZ()).append(",");
				json.append("\"heading\": ").append(centerBot.getHeading()).append(",");
				json.append("\"hpPct\": ").append((int)((centerBot.getCurrentHp() / centerBot.getMaxHp()) * 100));
				json.append("},");
				json.append("\"radius\": ").append(radius).append(",");

				json.append("\"entities\": [");
				List<Creature> nearby = new ArrayList<>();
				World.getInstance().forEachVisibleObjectInRange(centerBot, Creature.class, radius, c -> {
					if (c != null && !c.isDead()) nearby.add(c);
				});

				for (int i = 0; i < nearby.size(); i++)
				{
					Creature c = nearby.get(i);
					if (i > 0) json.append(",");

					String type = "NPC";
					boolean isAggro = false;
					boolean isParty = false;

					if (c instanceof FakePlayer)
					{
						type = "PARTY";
						isParty = true;
					}
					else if (c.isPlayer())
					{
						type = "PLAYER";
					}
					else if (c.isMonster() || c.isAttackable())
					{
						type = "MOB";
						if (c.isAttackable() && c.asAttackable().isAggressive())
						{
							isAggro = true;
						}
					}

					json.append("{");
					json.append("\"name\": \"").append(escapeJson(c.getName())).append("\",");
					json.append("\"type\": \"").append(type).append("\",");
					json.append("\"x\": ").append(c.getX()).append(",");
					json.append("\"y\": ").append(c.getY()).append(",");
					json.append("\"z\": ").append(c.getZ()).append(",");
					json.append("\"isAggro\": ").append(isAggro).append(",");
					json.append("\"isParty\": ").append(isParty).append(",");
					json.append("\"hpPct\": ").append((int)((c.getCurrentHp() / c.getMaxHp()) * 100));
					json.append("}");
				}
				json.append("]}");

				sendResponse(exchange, 200, json.toString());
			}
			else
			{
				sendResponse(exchange, 405, "{\"error\": \"Method not allowed\"}");
			}
		}
	}

	// -----------------------------------------------------------------------
	// JSON Helper Methods
	// -----------------------------------------------------------------------
	private static String escapeJson(String str)
	{
		if (str == null) return "";
		return str.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r");
	}

	private static Double parseDoubleJson(String json, String key)
	{
		try
		{
			String pattern = "\"" + key + "\":";
			int pos = json.indexOf(pattern);
			if (pos == -1) return null;
			int start = pos + pattern.length();
			int end = json.indexOf(",", start);
			if (end == -1) end = json.indexOf("}", start);
			if (end == -1) end = json.length();
			String valStr = json.substring(start, end).trim().replace("\"", "");
			return Double.parseDouble(valStr);
		}
		catch (Exception e)
		{
			return null;
		}
	}

	private static String parseStringJson(String json, String key)
	{
		try
		{
			String pattern = "\"" + key + "\":";
			int pos = json.indexOf(pattern);
			if (pos == -1) return null;
			int start = pos + pattern.length();
			int end = json.indexOf(",", start);
			if (end == -1) end = json.indexOf("}", start);
			if (end == -1) end = json.length();
			return json.substring(start, end).trim().replace("\"", "");
		}
		catch (Exception e)
		{
			return null;
		}
	}

	public static WebAPIManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}

	private static class SingletonHolder
	{
		protected static final WebAPIManager INSTANCE = new WebAPIManager();
	}
}
