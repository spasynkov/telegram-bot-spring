package com.example.telegrambotspring.entities.bots;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;

import com.example.telegrambotspring.entities.Chat;
import com.example.telegrambotspring.services.ResponseService;
import com.example.telegrambotspring.services.TelegramBotApiRequestsSender;
import com.example.telegrambotspring.utils.Pair;

/**
 * The abstract class уровня (слоя) Service
 * ????????????????
 * реализация класса:
 * @see     com.example.telegrambotspring.entities.bots.SongsBot
 * @version 1.0.1
 */
public abstract class AbstractTelegramBot {
	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractTelegramBot.class);
	/** мапа ответа в чат */
	protected final Map<Chat, Pair<Long, String>> answersForChats;
	/** идентификатор для доступа к боту */
	protected String token;
	/** ??? */
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

	/**
	 * Метод получения токина
	 * @return возвращает токин
	 */
	public String getToken() {
		return token;
	}

	/**
	 * Метод получения ???
	 * @return возвращает ???
	 */
	public AtomicLong getOffset() {
		return offset;
	}

	/**
	 * Метод получения ???
	 * @return возвращает ???
	 */
	public long getLastUpdateTime() {
		return lastUpdateTime;
	}

	/**
	 * Метод получения режим работы бота
	 * @return возвращает режим работы бота
	 */
	public UpdatesStrategy getStrategy() {
		return strategy;
	}

	/**
	 * Переопределяем метод equals
	 */
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		SongsBot bot = (SongsBot) o;
		return lastUpdateTime == bot.lastUpdateTime &&
				Objects.equals(answersForChats, bot.answersForChats) &&
				Objects.equals(token, bot.token) &&
				Objects.equals(offset, bot.offset) &&
				strategy == bot.strategy;
	}


	/**
	 * Переопределяем метод hashCode
	 */
	@Override
	public int hashCode() {
		return Objects.hash(answersForChats, token, offset, lastUpdateTime, strategy);
	}

	/**
	 * Переопределяем метод toString
	 */
	@Override
	public String toString() {
		return "Bot{" +
				"token='" + token + '\'' +
				", offset=" + offset +
				", lastUpdateTime=" + lastUpdateTime +
				", strategy=" + strategy +
				'}';
	}

	public List<JSONObject> getUpdates(TelegramBotApiRequestsSender requestsSender) throws Exception {
		JSONArray updates = requestsSender.getUpdates(this);
		lastUpdateTime = System.currentTimeMillis();

		List<JSONObject> result = new LinkedList<>();
		for (Object o : updates) {
			if (!(o instanceof JSONObject)) {
				LOGGER.debug(o + " is not JSONObject");
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
			//определяем тип чата - личный/групповой
			String chatType = update.optJSONObject("message")
					.optJSONObject("chat")
					.optString("type", "");

			if (chatType.isEmpty()) {
				LOGGER.debug("Unable to get chat type: " + update);
				continue;
			}
			/** флаг процесса обработки запроса */
			boolean processed = true;

			if ("private".equalsIgnoreCase(chatType)) {
				try {
					processDirectMessage(requestsSender, update);
				} catch (Exception e) {
					processed = false;
					LOGGER.error("Unable to process direct message", e);
				}
			} else {
				try {
					LOGGER.debug("responseService " + responseService);
					LOGGER.debug("update " + update);

					processGroupMessage(responseService, update);
				} catch (Exception e) {
					processed = false;
					LOGGER.error("Unable to process group message", e);
				}
			}
			/** если процесс обработки запроса прошел без ошибок*/
			if (processed) {

				long update_id = update.getLong("update_id");
				LOGGER.debug("update_id: " + update_id);

				offset = new AtomicLong(update_id);
				LOGGER.debug("offset: " + offset);

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
