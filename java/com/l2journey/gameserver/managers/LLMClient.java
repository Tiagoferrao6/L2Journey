package com.l2journey.gameserver.managers;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.l2journey.commons.threads.ThreadPool;

/**
 * Asynchronous HTTP REST Client for local Ollama LLM Inference API (http://localhost:11434/api/generate).
 */
public class LLMClient
{
	private static final Logger LOGGER = Logger.getLogger(LLMClient.class.getName());
	private static final String OLLAMA_URL = "http://localhost:11434/api/generate";
	private static final String DEFAULT_MODEL = "qwen2.5:1.5b";

	private final HttpClient _httpClient;

	protected LLMClient()
	{
		_httpClient = HttpClient.newBuilder()
			.connectTimeout(Duration.ofSeconds(5))
			.build();
	}

	public void generateAsync(String prompt, Consumer<String> callback)
	{
		generateAsync(DEFAULT_MODEL, prompt, callback);
	}

	public void generateAsync(String model, String prompt, Consumer<String> callback)
	{
		ThreadPool.execute(() -> {
			try
			{
				String jsonPayload = buildJsonPayload(model, prompt);
				HttpRequest request = HttpRequest.newBuilder()
					.uri(URI.create(OLLAMA_URL))
					.header("Content-Type", "application/json")
					.timeout(Duration.ofSeconds(12))
					.POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
					.build();

				HttpResponse<String> response = _httpClient.send(request, HttpResponse.BodyHandlers.ofString());
				if (response.statusCode() == 200)
				{
					String responseText = extractResponseText(response.body());
					if (responseText != null && !responseText.isEmpty())
					{
						callback.accept(responseText.trim());
					}
					else
					{
						callback.accept(null);
					}
				}
				else
				{
					LOGGER.warning("LLMClient: Ollama API status code " + response.statusCode());
					callback.accept(null);
				}
			}
			catch (Exception e)
			{
				LOGGER.log(Level.FINE, "LLMClient: Failed connecting to local Ollama service", e);
				callback.accept(null);
			}
		});
	}

	private String buildJsonPayload(String model, String prompt)
	{
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("\"model\":\"").append(escapeJson(model)).append("\",");
		sb.append("\"prompt\":\"").append(escapeJson(prompt)).append("\",");
		sb.append("\"stream\":false");
		sb.append("}");
		return sb.toString();
	}

	private String extractResponseText(String jsonBody)
	{
		if (jsonBody == null) return null;
		int keyIndex = jsonBody.indexOf("\"response\":");
		if (keyIndex == -1) return null;

		int start = jsonBody.indexOf("\"", keyIndex + 11);
		if (start == -1) return null;

		StringBuilder sb = new StringBuilder();
		boolean escaped = false;
		for (int i = start + 1; i < jsonBody.length(); i++)
		{
			char c = jsonBody.charAt(i);
			if (escaped)
			{
				if (c == 'n') sb.append('\n');
				else if (c == 'r') sb.append('\r');
				else if (c == 't') sb.append('\t');
				else sb.append(c);
				escaped = false;
			}
			else if (c == '\\')
			{
				escaped = true;
			}
			else if (c == '"')
			{
				break;
			}
			else
			{
				sb.append(c);
			}
		}
		return sb.toString();
	}

	private String escapeJson(String input)
	{
		if (input == null) return "";
		return input.replace("\\", "\\\\")
			.replace("\"", "\\\"")
			.replace("\n", "\\n")
			.replace("\r", "\\r")
			.replace("\t", "\\t");
	}

	public static LLMClient getInstance()
	{
		return SingletonHolder.INSTANCE;
	}

	private static class SingletonHolder
	{
		protected static final LLMClient INSTANCE = new LLMClient();
	}
}
