package com.example.telegrambotspring.services;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.example.telegrambotspring.controllers.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import com.example.telegrambotspring.entities.Chat;
import com.example.telegrambotspring.entities.bots.SongsBot;
import com.example.telegrambotspring.utils.Pair;
import com.example.telegrambotspring.utils.Utils;

/**
 * The class уровня (слоя) Business Service - слоя бизнес-логики
 * содержат бизнес-логику и вызывают методы на уровне хранилища
 * класс отправки ответов в телеграмм
 * <b>requestsSender</b> and <b>answersForChats</b> and <b>sendMessageLatencyGroup</b> and <b>sendMessageLatencyDirect</b>
 * @author  Stas Pasynkov
 * @see     com.example.telegrambotspring.services.TelegramWebhooksService
 * @see     com.example.telegrambotspring.services.DatabaseResponseService
 * @see     com.example.telegrambotspring.services.DatabaseService
 * @see     com.example.telegrambotspring.services.ResponseService
 * @see     com.example.telegrambotspring.services.SendResponsesService
 * @see     com.example.telegrambotspring.services.TelegramBotApiRequestsSender
 * @version 1.0.1
 */
@Service
public class SendResponsesService {
	private static final Logger LOGGER = LoggerFactory.getLogger(SendResponsesService.class);
	private static final Logger MYLOGGER = LoggerFactory.getLogger(SendResponsesService.class);

	/** объект класса TelegramBotApiRequestsSender ??? */
	private TelegramBotApiRequestsSender requestsSender;
	/** мапа ответа в чат */
	private Map<Chat, Pair<Long, String>> answersForChats;

	/** время задержки для групповых сообщений */
	@Value("${app.send-message-latency.group}")
	private long sendMessageLatencyGroup;

	/** время задержки для личных сообщений */
	@Value("${app.send-message-latency.direct}")
	private long sendMessageLatencyDirect;

	/**
	 * Конструктор - создание нового объекта с определенными значениями
	 * @param answersForChats - мапа ответа в чат
	 * @param bot - ???
	 * @param requestsSender - отправка запросов
	 * @param executor - ???
	 */
	@Autowired
	public SendResponsesService(@Qualifier("answersForChats") Map<Chat, Pair<Long, String>> answersForChats,
	                            SongsBot bot,    // TODO: hardcoded bot instance. rewrite with chat response object
	                            TelegramBotApiRequestsSender requestsSender, ThreadPoolTaskExecutor executor) {
		MYLOGGER.debug("Старт SendResponsesService");

		this.answersForChats = answersForChats;
		this.requestsSender = requestsSender;

		Runnable runnable = () -> {
			try {
				/**  цикл пока не будет установлен флаг прерывания потока */
				while (!Thread.currentThread().isInterrupted()) {
					try {
						sendResponses(bot);
					} catch (Exception e) {
						LOGGER.debug("Exception occurred while sending responses", e);
					}

					TimeUnit.MILLISECONDS.sleep(Math.min(
							sendMessageLatencyGroup,
							sendMessageLatencyDirect));
				}
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				LOGGER.debug("Thread " + Thread.currentThread().getName() + " was interrupted");
			}
		};
		executor.execute(runnable);
	}

	/**
	 * Метод отправки ответов в телеграмм
	 * @param bot - ???
	 */
	private void sendResponses(SongsBot bot) {
		Iterator<Map.Entry<Chat, Pair<Long, String>>> iterator = answersForChats.entrySet().iterator();
		MYLOGGER.debug("answersForChats= " + answersForChats);

		/**  цикл пока в мапе есть готовые ответы для отправки */
		while (iterator.hasNext()) {
			MYLOGGER.debug("в мапе есть готовые ответы для отправки= ");
			Map.Entry<Chat, Pair<Long, String>> entry = iterator.next();
			/**  получаем идентификатор чата */
			int chatId = entry.getKey().getChatId();
			MYLOGGER.debug("получаем идентификатор чата= " + chatId);
			/** время формирования запроса */
			long lastMessageTime = entry.getValue().getFirst();

			/** текст куплета песни для ответа */
			String preparedResponse = entry.getValue().getSecond();

			/** тайм-аут актуальности ответа в соответсвии с типом чата  */
			long latency = entry.getKey().isGroup()
					? sendMessageLatencyGroup
					: sendMessageLatencyDirect;


//			if (lastMessageTime * Utils.MILLIS_MULTIPLIER < System.currentTimeMillis() - latency) {
			if (true) {
				LOGGER.debug("Запрос еще актуален");

				try {
					requestsSender.sendMessage(bot, chatId, preparedResponse);
					/**  удаляем этот запрос из мапы */
					iterator.remove();
				} catch (Exception e) {
					LOGGER.error("Unable to send response or delete chat from map", e);
				}
			}
		}
	}
}
