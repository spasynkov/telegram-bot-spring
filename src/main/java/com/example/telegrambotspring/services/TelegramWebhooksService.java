package com.example.telegrambotspring.services;

import com.example.telegrambotspring.entities.Received;
import com.example.telegrambotspring.entities.bots.AbstractTelegramBot;
import com.example.telegrambotspring.utils.SafeCallable;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TelegramWebhooksService implements SafeCallable {


	private ResponseService responseService;
	private TelegramBotApiRequestsSender requestsSender;
	private List<AbstractTelegramBot> bots;
	private Received received;

	@Autowired
	public TelegramWebhooksService(ResponseService responseService,
	                               TelegramBotApiRequestsSender requestsSender,
	                               Received received,
	                               List<AbstractTelegramBot> bots) {
		this.responseService = responseService;
		this.requestsSender = requestsSender;
		this.received = received;
		this.bots = bots;
	}

	public String proceedTelegramApiWebhook(String botToken, String jsonString) {
		return safeCall(() -> {
			LOGGER.info("Got request: " + jsonString);
			AbstractTelegramBot bot = findBotByToken(botToken);
			if (bot == null) {
				String errorText = "Unknown bot token";
				LOGGER.debug(errorText);
				return new JSONObject("{\"error\": \"" + errorText + "\"}");
			}

			JSONObject json = new JSONObject(jsonString);
			try {
				int chatId = Integer.parseInt(json.optJSONObject("message")
						.optJSONObject("chat")
						.optString("id", ""));
				boolean chatType = json.optJSONObject("message")
						.optJSONObject("chat")
						.optString("type")
						.equalsIgnoreCase("group");
				String chatText = json.optJSONObject("message")
						.optString("text", "");
				long chatDate = Long.parseLong(json.optJSONObject("message")
						.optString("date", ""));
				received.addMessage(chatId, chatType, chatText, chatDate);
//				bot.processUpdates(responseService, requestsSender, json);
			} catch (Exception e) {
				String errorText = "Unable to process message";
				LOGGER.debug(errorText, e);
				return new JSONObject("{\"error\": \"" + errorText + "\"}");
			}
			return new JSONObject("{\"status\": \"ok\"}");
		}).toString();
	}

	private AbstractTelegramBot findBotByToken(String botToken) {
		for (AbstractTelegramBot bot : bots) {
			if (bot.getToken().equals(botToken)) {
				return bot;
			}
		}
		return null;
	}
}
