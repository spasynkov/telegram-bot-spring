package com.example.telegrambotspring.services;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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

@Service
public class SendResponsesService {
	private static final Logger LOGGER = LoggerFactory.getLogger(SendResponsesService.class);

	private TelegramBotApiRequestsSender requestsSender;
	private Map<Chat, Pair<Long, String>> answersForChats;

	@Value("${app.send-message-latency.group}")
	private long sendMessageLatencyGroup;

	@Value("${app.send-message-latency.direct}")
	private long sendMessageLatencyDirect;

	@Autowired
	public SendResponsesService(@Qualifier("answersForChats") Map<Chat, Pair<Long, String>> answersForChats,
	                            SongsBot bot,    // TODO: hardcoded bot instance. rewrite with chat response object
	                            TelegramBotApiRequestsSender requestsSender, ThreadPoolTaskExecutor executor) {

		this.answersForChats = answersForChats;
		this.requestsSender = requestsSender;

		Runnable runnable = () -> {
			try {
				while (!Thread.currentThread().isInterrupted()) {
					sendResponses(bot);

					TimeUnit.MILLISECONDS.sleep(Math.min(
							sendMessageLatencyGroup,
							sendMessageLatencyDirect));
				}
			} catch (Exception e) {
				Thread.currentThread().interrupt();
				LOGGER.debug("Exception occurred while sending responses");
			}
		};
		executor.execute(runnable);
	}

	private void sendResponses(SongsBot bot) {
		Iterator<Map.Entry<Chat, Pair<Long, String>>> iterator = answersForChats.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry<Chat, Pair<Long, String>> entry = iterator.next();
			int chatId = entry.getKey().getChatId();
			long lastMessageTime = entry.getValue().getFirst();
			String preparedResponse = entry.getValue().getSecond();

			long latency = entry.getKey().isGroup()
					? sendMessageLatencyGroup
					: sendMessageLatencyDirect;

			if (lastMessageTime * Utils.MILLIS_MULTIPLIER < System.currentTimeMillis() - latency) {
				try {
					requestsSender.sendMessage(bot, chatId, preparedResponse);
					iterator.remove();
				} catch (Exception e) {
					LOGGER.error("Unable to send response or delete chat from map", e);
				}
			}
		}
	}
}
