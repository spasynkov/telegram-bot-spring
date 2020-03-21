package com.example.telegrambotspring.services;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RequestsSender {
	private static final Logger LOGGER = LoggerFactory.getLogger(RequestsSender.class);

	@Value("${telegram.bot.token}")
	private String token;
	private String apiUrl = "https://api.telegram.org/bot";

	@Value("${telegram.bot.timeout}")
	private String apiTimeout;

	private AtomicLong offset = new AtomicLong(0);

	public void setOffset(long offset) {
		this.offset.set(offset);
	}

	private String getRequestUrl() {
		return apiUrl + token + "/";
	}

	public JSONObject getMe() throws Exception {
		return (JSONObject) sendGet("getMe");
	}

	public JSONObject sendMessage(int chatId, String text) throws Exception {
		return (JSONObject) sendGet("sendMessage?chat_id=" + chatId + "&text=" + URLEncoder.encode(text, "UTF-8"));
	}

	public JSONObject sendMessage(int chatId, String text, JSONObject keyboardMarkup) throws Exception {
		JSONObject json = new JSONObject();
		json.put("chat_id", chatId);
		json.put("text", text);
		json.put("reply_markup", keyboardMarkup);
		return (JSONObject) sendPost("sendMessage", json);
	}

	public JSONArray getUpdates() throws Exception {
		return (JSONArray) sendGet(String.format("getUpdates?%s&timeout=%s&allowed_updates=%%5B%%22message%%22%%5D",
				offset.get() != 0 ? "offset=" + (offset.get() + 1) : "",
				apiTimeout));
	}

	public JSONObject getChat(String chatName) throws Exception {
		return (JSONObject) sendGet("getChat?chat_id=@" + chatName);
	}

	public JSONObject getChat(int chatId) throws Exception {
		return (JSONObject) sendGet("getChat?chat_id=" + chatId);
	}

	private Object sendPost(String methodName, JSONObject json) throws Exception {
		final String requestUrl = getRequestUrl() + methodName;

		LOGGER.info("sending request at: " + requestUrl);

		HttpPost post = new HttpPost(requestUrl);
		post.setEntity(new StringEntity(json.toString(), "UTF-8"));
		post.setHeader("Accept", "application/json");
		post.setHeader("Content-Type", "application/json");
		HttpClient client = HttpClients.createDefault();
		HttpResponse resp = client.execute(post);

		return parseResponse(resp, requestUrl);
	}

	private Object sendGet(String methodNameAndUrlParams) throws Exception {
		final String requestUrl = getRequestUrl() + methodNameAndUrlParams;

		LOGGER.info("sending request at: " + requestUrl);

		HttpGet httpget = new HttpGet(requestUrl);
		HttpClient client = HttpClients.createDefault();
		HttpResponse resp = client.execute(httpget);

		return parseResponse(resp, requestUrl);
	}

	private Object parseResponse(HttpResponse response, String requestUrl) throws Exception {
		String content = getContent(response.getEntity().getContent());
		JSONObject json = new JSONObject(content);
		if (json.getBoolean("ok")) {
			return json.get("result");
		} else {
			String description = json.optString("description", "");
			String error_code = json.optString("error_code", "");
			String error = String.format("Failed to execute '%s': %s (%s)", requestUrl, error_code, description);
			throw new Exception(error);
		}
	}

	private String getContent(InputStream content) throws IOException {
		return new String(getBytes(content), StandardCharsets.UTF_8);
	}

	private byte[] getBytes(InputStream stream) throws IOException {
		byte[] result = new byte[stream.available()];
		stream.read(result);
		return result;
	}
}
