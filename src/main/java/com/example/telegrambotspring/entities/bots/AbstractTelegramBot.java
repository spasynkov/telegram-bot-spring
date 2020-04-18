package com.example.telegrambotspring.entities.bots;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
//import org.slf4j.log;
//import org.slf4j.logFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;

import com.example.telegrambotspring.entities.Chat;
import com.example.telegrambotspring.services.ResponseService;
import com.example.telegrambotspring.services.TelegramBotApiRequestsSender;
import com.example.telegrambotspring.utils.Pair;

/**
 * The abstract class уровня (слоя) Service
 * сервис работы с телеграмм ботом
 * реализация класса:
 * @see     com.example.telegrambotspring.entities.bots.SongsBot
 * @version 1.0.1
 */
@Getter
@ToString(exclude = { "messageSource", "answersForChats" })
@Slf4j
@EqualsAndHashCode(exclude = { "messageSource" })
public abstract class AbstractTelegramBot {
//	private static final log log = logFactory.getlog(AbstractTelegramBot.class);
	/** мапа содержашая все ответы в чат */
	@Getter(AccessLevel.NONE)
	protected final Map<Chat, Pair<Long, String>> answersForChats;
	/** идентификатор для доступа к боту */
	protected String token;
	/** update_id последнего обработанного обновления
	 * Все обновления с update_id меньшим или равным offset
	 * будут отмечены как подтверждённые и не будут больше возвращаться сервером
	 */
	protected AtomicLong offset = new AtomicLong(0);
	/** время последнего обновления */
	protected long lastUpdateTime;
	/** режим работы бота */
	protected UpdatesStrategy strategy;
	/** ??? */
	@Autowired
	protected MessageSource messageSource;

	/**
	 * Конструктор абстрактного класса - создание нового объекта с определенными значениями
	 * @param token идентификатор для доступа к боту
	 * @param answersForChats мапа ответа в чат
	 * @param strategy режим работы бота
	 */
	public AbstractTelegramBot(String token, Map<Chat, Pair<Long, String>> answersForChats, UpdatesStrategy strategy) {
		this.token = token;
		this.answersForChats = answersForChats;
		this.strategy = strategy;
	}



	public List<JSONObject> getUpdates(TelegramBotApiRequestsSender requestsSender) throws Exception {
		JSONArray updates = requestsSender.getUpdates(this);
		lastUpdateTime = System.currentTimeMillis();

		List<JSONObject> result = new LinkedList<>();
		for (Object o : updates) {
			if (!(o instanceof JSONObject)) {
				log.debug(o + " is not JSONObject");
				continue;
			}
			result.add((JSONObject) o);
		}

		return result;
	}

	/**
	 * Метод обработки запросов
	 * @param responseService - серис формирования ответов
	 * @param requestsSender - отправка запросов
	 * @param updates массив с текстами сообщений в формате json полученных с телеграмм
	 */
	public void processUpdates(ResponseService responseService, TelegramBotApiRequestsSender requestsSender, JSONObject... updates) {
		for (JSONObject update : updates) {
			/** определяем тип чата - личный/групповой */
			String chatType = update.optJSONObject("message")
					.optJSONObject("chat")
					.optString("type", "");

			if (chatType.isEmpty()) {
				log.debug("Unable to get chat type: " + update);
				continue;
			}
			/** флаг процесса обработки запроса */
			boolean processed = true;

			if ("private".equalsIgnoreCase(chatType)) {
				try {
					processDirectMessage(requestsSender, update);
				} catch (Exception e) {
					processed = false;
					log.error("Unable to process direct message", e);
				}
			} else {
				try {
					log.debug("responseService " + responseService);
					log.debug("update " + update);

					processGroupMessage(responseService, update);
				} catch (Exception e) {
					processed = false;
					log.error("Unable to process group message", e);
				}
			}
			/** если процесс обработки запроса прошел без ошибок*/
			if (processed) {
				/** Уникальный идентификатор входящего обновления */
				long update_id = update.getLong("update_id");
				/** update_id последнего обработанного обновления */
				offset = new AtomicLong(update_id);
			}
		}
	}

	protected String getMessageString(String messageName, String lang) {
		return messageSource.getMessage(messageName, null, Locale.forLanguageTag(lang));
	}

	protected abstract void processDirectMessage(TelegramBotApiRequestsSender requestsSender, JSONObject update) throws Exception;

	/**
	 * Метод обработки групповых сообщений
	 * @param responseService - сервис формирования ответов
	 * @param update текст сообщения в формате json полученный с телеграмм - содержимое поля message
	 */
	protected abstract void processGroupMessage(ResponseService responseService, JSONObject update);

	/**
	 * перечисление  - стратегий работы бота
	 * LONG_POOLING - периодически опрашивает сервера телеграмма на наличие новых сообщений
	 * WEBHOOKS - указываем телеграму определенный URL, куда он будет отправлять нам каждое новое сообщение для бота
	 */
	public enum UpdatesStrategy {
		LONG_POOLING,
		WEBHOOKS
	}
}
