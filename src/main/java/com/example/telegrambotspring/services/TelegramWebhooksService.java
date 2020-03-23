package com.example.telegrambotspring.services;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TelegramWebhooksService {
	private static final Logger LOGGER = LoggerFactory.getLogger(TelegramWebhooksService.class);

	@Value("${telegram.bot.token}")
	private String botToken;
	private ResponseService responseService;

	@Autowired
	public TelegramWebhooksService(ResponseService responseService) {
		this.responseService = responseService;
	}

	public String proceedTelegramApiWebhook(String botToken, String jsonString) {
		LOGGER.info("Got request: " + jsonString);

		if (!this.botToken.equals(botToken)) {
			String errorText = "Unknown bot token";
			LOGGER.debug(errorText);
			return "{\"error\": \"" + errorText + "\"}";
		}

		JSONObject message = new JSONObject(jsonString).getJSONObject("message");
		int chatId = message.getJSONObject("chat").getInt("id");
		String text = message.getString("text");

		try {
			responseService.getResponse(text);
		} catch (Exception e) {
			String errorText = "Unable to find suitable response";
			LOGGER.debug(errorText, e);
			return "{\"error\": \"" + errorText + "\"}";
		}

		return "{\"status\": \"ok\"}";
	}
}
