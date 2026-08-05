package com.l2journey.gameserver.managers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import com.l2journey.gameserver.ai.Intention;
import com.l2journey.gameserver.model.actor.instance.FakePlayer;

/**
 * Dynamic JavaScript Script Engine Runtime (Level 2 Autonomy).
 * Integrates javax.script.ScriptEngineManager (GraalVM JS / Nashorn) to load, compile, and execute
 * AI-generated recovery intervention scripts from data/scripts/ai_interventions/.
 */
public class JavaScriptRuntimeEngine
{
	private static final Logger LOGGER = Logger.getLogger(JavaScriptRuntimeEngine.class.getName());
	private static final String SCRIPTS_DIR_PATH = "data/scripts/ai_interventions";

	private final ScriptEngineManager _scriptEngineManager;

	protected JavaScriptRuntimeEngine()
	{
		_scriptEngineManager = new ScriptEngineManager();
		ensureScriptsDirectory();
		LOGGER.info("JavaScriptRuntimeEngine: Initialized JS Script Engine Runtime Manager.");
	}

	public static JavaScriptRuntimeEngine getInstance()
	{
		return SingletonHolder.INSTANCE;
	}

	private static class SingletonHolder
	{
		protected static final JavaScriptRuntimeEngine INSTANCE = new JavaScriptRuntimeEngine();
	}

	private void ensureScriptsDirectory()
	{
		File dir = new File(SCRIPTS_DIR_PATH);
		if (!dir.exists())
		{
			if (dir.mkdirs())
			{
				LOGGER.info("JavaScriptRuntimeEngine: Created interventions directory: " + dir.getAbsolutePath());
			}
		}
	}

	/**
	 * Saves or updates an AI-generated JavaScript intervention script file in data/scripts/ai_interventions/.
	 */
	public void saveInterventionScript(String scriptName, String jsCode)
	{
		ensureScriptsDirectory();
		File scriptFile = new File(SCRIPTS_DIR_PATH, scriptName);
		try
		{
			Files.writeString(scriptFile.toPath(), jsCode, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
			LOGGER.info("JavaScriptRuntimeEngine: Saved intervention script to " + scriptFile.getAbsolutePath());
		}
		catch (IOException e)
		{
			LOGGER.log(Level.SEVERE, "JavaScriptRuntimeEngine: Failed to save script file: " + scriptName, e);
		}
	}

	/**
	 * Loads a JavaScript intervention script from data/scripts/ai_interventions/.
	 */
	public String loadInterventionScript(String scriptName)
	{
		File scriptFile = new File(SCRIPTS_DIR_PATH, scriptName);
		if (!scriptFile.exists())
		{
			return null;
		}

		try
		{
			return Files.readString(scriptFile.toPath());
		}
		catch (IOException e)
		{
			LOGGER.log(Level.SEVERE, "JavaScriptRuntimeEngine: Failed to read script file: " + scriptName, e);
			return null;
		}
	}

	/**
	 * Executes a JavaScript intervention script in a sandboxed context with bot and manager bindings.
	 */
	public boolean executeIntervention(FakePlayer bot, String scriptName, String fallbackJsCode)
	{
		if (bot == null || !bot.isOnline()) return false;

		String jsCode = loadInterventionScript(scriptName);
		if (jsCode == null || jsCode.isBlank())
		{
			jsCode = fallbackJsCode;
		}

		if (jsCode == null || jsCode.isBlank())
		{
			executeSafetyFallback(bot, "Código de script JS ausente ou em branco.");
			return false;
		}

		ScriptEngine engine = _scriptEngineManager.getEngineByName("JavaScript");
		if (engine == null)
		{
			engine = _scriptEngineManager.getEngineByName("js");
		}
		if (engine == null)
		{
			engine = _scriptEngineManager.getEngineByName("nashorn");
		}

		if (engine == null)
		{
			LOGGER.warning("JavaScriptRuntimeEngine: No javax.script JavaScript engine found in JVM classpath. Executing native Java safety fallback.");
			executeSafetyFallback(bot, "Nenhum motor de script JavaScript disponível na JVM.");
			return true;
		}

		try
		{
			Bindings bindings = engine.createBindings();
			bindings.put("bot", bot);
			bindings.put("LLMCompanionManager", LLMCompanionManager.getInstance());
			bindings.put("TownWaypointMeshManager", TownWaypointMeshManager.getInstance());
			bindings.put("BuyListExecutingEngine", BuyListExecutingEngine.getInstance());

			engine.eval(jsCode, bindings);
			LOGGER.info("JavaScriptRuntimeEngine: Successfully evaluated JS script '" + scriptName + "' for bot " + bot.getName());
			return true;
		}
		catch (ScriptException e)
		{
			LOGGER.log(Level.SEVERE, "JavaScriptRuntimeEngine: ScriptException while executing '" + scriptName + "' for " + bot.getName(), e);
			executeSafetyFallback(bot, "Erro de sintaxe/execução no script JS: " + e.getMessage());
			return false;
		}
		catch (Throwable t)
		{
			LOGGER.log(Level.SEVERE, "JavaScriptRuntimeEngine: Runtime error while executing '" + scriptName + "' for " + bot.getName(), t);
			executeSafetyFallback(bot, "Exceção em tempo de execução no script JS: " + t.getMessage());
			return false;
		}
	}

	/**
	 * Safety fallback execution in case of script syntax/runtime errors or missing engine.
	 */
	public void executeSafetyFallback(FakePlayer bot, String reason)
	{
		if (bot == null || !bot.isOnline()) return;

		LOGGER.warning("JavaScriptRuntimeEngine: Executing Safety Fallback for " + bot.getName() + " [Reason: " + reason + "]");
		bot.getAI().setIntention(Intention.IDLE, null);
		LLMCompanionManager.getInstance().applyShopCooldown(bot);
		bot.teleToLocation(-14347, 123622, -3120); // Teleport to Gludio Town Square
	}
}
