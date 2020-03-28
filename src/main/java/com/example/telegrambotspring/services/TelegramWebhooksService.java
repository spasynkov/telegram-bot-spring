package com.example.telegrambotspring.services;

import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.example.telegrambotspring.entities.Chat;
import com.example.telegrambotspring.entities.AbstractTelegramBot;
import com.example.telegrambotspring.utils.Pair;

@Service
public class TelegramWebhooksService {
	private static final Logger LOGGER = LoggerFactory.getLogger(TelegramWebhooksService.class);

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
		LOGGER.info("Got request: " + jsonString);

		AbstractTelegramBot bot = findBotByToken(botToken);

		if (bot == null) {
			String errorText = "Unknown bot token";
			LOGGER.debug(errorText);
			return "{\"error\": \"" + errorText + "\"}";
		}

		JSONObject json = new JSONObject(jsonString);
		try {
			bot.processUpdates(responseService, requestsSender, json);
		} catch (Exception e) {
			String errorText = "Unable to process message";
			LOGGER.debug(errorText, e);
			return "{\"error\": \"" + errorText + "\"}";
		}

		return "{\"status\": \"ok\"}";
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
