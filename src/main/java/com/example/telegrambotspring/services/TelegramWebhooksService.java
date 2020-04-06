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
import com.example.telegrambotspring.entities.bots.AbstractTelegramBot;
import com.example.telegrambotspring.utils.Pair;

/**
 * The class уровня (слоя) Business Service - слоя бизнес-логики
 * содержат бизнес-логику и вызывают методы на уровне хранилища
 * класс реализует лонику работы обработки сообщений от бота в режиме webhooks
 * <b>LOGGER</b> and <b>responseService</b> and <b>requestsSender</b> and <b>answersForChats</b> and <b>bots</b>
 * @author  Stas Pasynkov
 * @see     com.example.telegrambotspring.services.AbstractResponseService
 * @see     com.example.telegrambotspring.services.DatabaseResponseService
 * @see     com.example.telegrambotspring.services.DatabaseService
 * @see     com.example.telegrambotspring.services.ResponseService
 * @see     com.example.telegrambotspring.services.SendResponsesService
 * @see     com.example.telegrambotspring.services.TelegramBotApiRequestsSender
 * @version 1.0.1
 */
@Service
public class TelegramWebhooksService {
	/** переменная для записи логов  */
	private static final Logger LOGGER = LoggerFactory.getLogger(TelegramWebhooksService.class);

	/** переменная интерфейса ResponseService
	 * @see com.example.telegrambotspring.services.ResponseService
	 */
	private ResponseService responseService;
	/** объект класса TelegramBotApiRequestsSender
	 * @see com.example.telegrambotspring.services.TelegramBotApiRequestsSender
	 */
	private TelegramBotApiRequestsSender requestsSender;
	/** мапа ответа в чат
	 * @see com.example.telegrambotspring.entities.Chat
	 */
	private Map<Chat, Pair<Long, String>> answersForChats;
	/** список объектов абстрактоного класса AbstractTelegramBot
	 * @see com.example.telegrambotspring.entities.bots.AbstractTelegramBot
	 */
	private List<AbstractTelegramBot> bots;

	/**
	 * Конструктор - создание нового объекта с определенными значениями
	 * @param responseService - переменная интерфейса ResponseService
	 * @param requestsSender - объект класса TelegramBotApiRequestsSender
	 * @param answersForChats - мапа ответа в чат
	 * @param bots - список объектов абстрактоного класса AbstractTelegramBot
	 */
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

	/**
	 * ???
	 * @param botToken - уникальный идентификатор, ключ для доступа к боту
	 * @param jsonString - текст сообщения в формате json
	 * @return возвращает - результат обработки сообщения с чата (OK OR код ошибки)
	 */
	public String proceedTelegramApiWebhook(String botToken, String jsonString) {
		LOGGER.info("Got request: " + jsonString);

		/** объект абстрактоного класса AbstractTelegramBot
		 * @see com.example.telegrambotspring.entities.bots.AbstractTelegramBot
		 */
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
