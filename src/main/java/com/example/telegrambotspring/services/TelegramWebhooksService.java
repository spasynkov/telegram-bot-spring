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

/**
 * The class уровня (слоя) Business Service - слоя бизнес-логики
 * содержат бизнес-логику и вызывают методы на уровне хранилища
 * класс реализует лонику работы обработки сообщений от бота в режиме webhooks со свойствами
 * <b>responseService</b> and <b>requestsSender</b> and <b>answersForChats</b> and <b>bots</b>
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
public class TelegramWebhooksService implements SafeCallable {


	/** переменная интерфейса ResponseService */
	private ResponseService responseService;

	/** объект класса TelegramBotApiRequestsSender */
	private TelegramBotApiRequestsSender requestsSender;

	/** мапа ответа в чат */
	private Map<Chat, Pair<Long, String>> answersForChats;

	/** список объектов абстрактоного класса AbstractTelegramBot */
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
	 * Метод обработки сообщенией от телеграмм
	 * @param botToken уникальный идентификатор, ключ для доступа к боту
	 * @param jsonString текст сообщения в формате json полученный с телеграмм
	 * @return возвращает результат обработки сообщения с чата (OK OR код ошибки)
	 */
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


	/**
	 * метод поиска нужного бота в списке ботов (bots)
	 * @param botToken уникальный идентификатор, ключ для доступа к боту
	 * @return возвращает искомого бота иначе null
	 */
	private AbstractTelegramBot findBotByToken(String botToken) {
		for (AbstractTelegramBot bot : bots) {
			if (bot.getToken().equals(botToken)) {
				return bot;
			}
		}
		return null;
	}
}
