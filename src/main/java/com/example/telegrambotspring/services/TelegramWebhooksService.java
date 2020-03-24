package com.example.telegrambotspring.services;

import java.util.Map;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.telegrambotspring.entities.Chat;
import com.example.telegrambotspring.utils.Pair;

@Service
public class TelegramWebhooksService {
	private static final Logger LOGGER = LoggerFactory.getLogger(TelegramWebhooksService.class);

	@Value("${telegram.bot.token}")
	private String botToken;
	private ResponseService responseService;
	private Map<Chat, Pair<Long, String>> answersForChats;

	@Autowired
	public TelegramWebhooksService(ResponseService responseService, @Qualifier("answersForChats") Map<Chat, Pair<Long, String>> answersForChats) {
		this.responseService = responseService;
		this.answersForChats = answersForChats;
	}

	public String proceedTelegramApiWebhook(String botToken, String jsonString) {
		LOGGER.info("Got request: " + jsonString);

		if (!this.botToken.equals(botToken)) {
			String errorText = "Unknown bot token";
			LOGGER.debug(errorText);
			return "{\"error\": \"" + errorText + "\"}";
		}

		JSONObject message = new JSONObject(jsonString).getJSONObject("message");
		String text = message.getString("text");
		long date = message.getLong("date");

		JSONObject chat = message.getJSONObject("chat");
		int chatId = chat.getInt("id");
		String chatType = chat.getString("type");

		String response;
		try {
			response = responseService.getResponse(text);
		} catch (Exception e) {
			String errorText = "Unable to find suitable response";
			LOGGER.debug(errorText, e);
			return "{\"error\": \"" + errorText + "\"}";
		}

		answersForChats.put(new Chat(chatId, chatType), new Pair<>(date, response));

		return "{\"status\": \"ok\"}";
	}
}
