package com.example.telegrambotspring.services;

import com.example.telegrambotspring.entities.bots.AbstractTelegramBot;
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

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Callable;

@Service
public class TelegramBotApiRequestsSender {
	private static final Logger LOGGER = LoggerFactory.getLogger(TelegramBotApiRequestsSender.class);
	/** Все запросы к Telegram Bot API должны осуществляться через HTTPS */
	private String apiUrl = "http://api.telegram.org/bot";

	@Value("${telegram.bot.longPoolingTimeout}")
	private String apiTimeout;

	private String getRequestUrl(AbstractTelegramBot bot) {
		return apiUrl + bot.getToken() + "/";
	}

	public JSONObject getMe(AbstractTelegramBot bot) {
		return safeCall(() -> (JSONObject) sendGet(bot, "getMe"));
	}

	/**
	 * Метод отправки ответов в телеграмм
	 * @param bot поля телеграмм бота
	 * @param chatId идентификатор чата
	 * @param text текст куплета песни для ответа
	 * @return возвращает ответ на запрос sendMessage
	 */
	public JSONObject sendMessage(AbstractTelegramBot bot, int chatId, String text) {
		LOGGER.debug("sendMessage = ");

		return safeCall(() -> (JSONObject) sendGet(bot, "sendMessage?chat_id=" + chatId + "&text=" + URLEncoder.encode(text, "UTF-8")));
	}

	public JSONObject sendMessage(AbstractTelegramBot bot, int chatId, String text, JSONObject keyboardMarkup) {
		return safeCall(() -> {
			JSONObject json = new JSONObject();
			json.put("chat_id", chatId);
			json.put("text", text);
			json.put("reply_markup", keyboardMarkup);
			return (JSONObject) sendPost(bot, "sendMessage", json);
		});
	}

	public JSONArray getUpdates(AbstractTelegramBot bot) throws Exception {
		return (JSONArray) sendGet(bot,
				String.format("getUpdates?%s&timeout=%s&allowed_updates=%%5B%%22message%%22%%5D",
						bot.getOffset().get() != 0 ? "offset=" + (bot.getOffset().get() + 1) : "",
						apiTimeout));
	}

	public JSONObject getChat(AbstractTelegramBot bot, String chatName) {
		return safeCall(() -> (JSONObject) sendGet(bot, "getChat?chat_id=@" + chatName));
	}

	public JSONObject getChat(AbstractTelegramBot bot, int chatId) {
		return safeCall(() -> (JSONObject) sendGet(bot, "getChat?chat_id=" + chatId));
	}

	private Object sendPost(AbstractTelegramBot bot, String methodName, JSONObject json) throws Exception {
		final String requestUrl = getRequestUrl(bot) + methodName;

		LOGGER.info("sending request at: " + requestUrl);

		HttpPost post = new HttpPost(requestUrl);
		LOGGER.info("post: " + post);

		post.setEntity(new StringEntity(json.toString(), "UTF-8"));
		post.setHeader("Accept", "application/json");
		post.setHeader("Content-Type", "application/json");
		HttpClient client = HttpClients.createDefault();
		HttpResponse resp = client.execute(post);

		return parseResponse(resp, requestUrl);
	}

	/**
	 * Метод отправки запроса
	 * @param bot поля телеграмм бота
	 * @param methodNameAndUrlParams имя метода запроса и его параметры
	 * @return возвращает  статуса ответа на запрос sendMessage от телеграмм
	 */
	private Object sendGet(AbstractTelegramBot bot, String methodNameAndUrlParams) throws Exception {
		/** формирование строки запроса для метода Telegram Bot API sendMessage */
		final String requestUrl = getRequestUrl(bot) + methodNameAndUrlParams;

		LOGGER.info("sending request at: " + requestUrl);

		/** создание объекта HttpGet строки запроса для метода Telegram Bot API sendMessage */
		HttpGet httpget = new HttpGet(requestUrl);
		LOGGER.debug("httpget= " + httpget);


		/** org.apache.http.impl.client.InternalHttpClient@67302d01 */
		HttpClient client = HttpClients.createDefault();


		HttpResponse resp = client.execute(httpget);

		return parseResponse(resp, requestUrl);
	}

	/**
	 * Метод чтения статуса ответа на запрос sendMessage от телеграмм
	 * @param response ???
	 * @param requestUrl ???
	 * @return возвращает - ???
	 */
	private Object parseResponse(HttpResponse response, String requestUrl) throws Exception {
		String content = getContent(response.getEntity().getContent());
		LOGGER.debug("content= " + content);


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

	/**
	 * безопасный вызов метода с проверкой всех исключений
	 * @return возвращает ???? joson
	 */
	private JSONObject safeCall(Callable<JSONObject> lambda) {
		try {
			return lambda.call();
		} catch (Exception e) {
			LOGGER.error("Exception occurred while processing callable function", e);

			JSONObject resp = new JSONObject();
			resp.put("error", e.getLocalizedMessage());
			return resp;
		}
	}
}
