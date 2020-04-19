package com.example.telegrambotspring.services;

import com.example.telegrambotspring.entities.Chat;
import com.example.telegrambotspring.entities.bots.AbstractTelegramBot;
import com.example.telegrambotspring.utils.Pair;
import com.example.telegrambotspring.utils.SafeCallable;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class TelegramWebhooksService implements SafeCallable {

	private ResponseService responseService;
	private TelegramBotApiRequestsSender requestsSender;
	private Map<Chat, Pair<Long, String>> answersForChats;
	private List<AbstractTelegramBot> bots;

	@Autowired
	public TelegramWebhooksService(ResponseService responseService,
	                               TelegramBotApiRequestsSender requestsSender,
	                               @Qualifier("answersForChats") Map<Chat, Pair<Long, String>> answersForChats,
	                               List<AbstractTelegramBot> bots) {
		this.responseService = responseService;
		this.requestsSender = requestsSender;
		this.answersForChats = answersForChats;
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
				bot.processUpdates(responseService, requestsSender, json);
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
