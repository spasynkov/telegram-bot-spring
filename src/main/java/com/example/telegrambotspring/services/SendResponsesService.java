package com.example.telegrambotspring.services;

import com.example.telegrambotspring.entities.Chat;
import com.example.telegrambotspring.entities.Message;
import com.example.telegrambotspring.entities.Received;
import com.example.telegrambotspring.entities.bots.SongsBot;
import com.example.telegrambotspring.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class SendResponsesService {
	private static final Logger LOGGER = LoggerFactory.getLogger(SendResponsesService.class);

	private TelegramBotApiRequestsSender requestsSender;
	private Received received;

	@Value("${app.send-message-latency.group}")
	private long sendMessageLatencyGroup;

	@Value("${app.send-message-latency.direct}")
	private long sendMessageLatencyDirect;

	@Autowired
	public SendResponsesService(Received received,
	                            SongsBot bot,    // TODO: hardcoded bot instance. rewrite with chat response object
	                            TelegramBotApiRequestsSender requestsSender, ThreadPoolTaskExecutor executor) {
		this.received = received;
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
		Iterator<Map.Entry<Chat, List<Message>>> iterator = received.getIncomingMessage().entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry<Chat, List<Message>> entry = iterator.next();
			int chatId = entry.getKey().getChatId();
			String textMessage = entry.getValue().get(entry.getValue().size() - 1).getText();
			long lastMessageTime = entry.getValue().get(entry.getValue().size() - 1).getData();


			long latency = entry.getKey().isGroup()
					? sendMessageLatencyGroup
					: sendMessageLatencyDirect;

			if (lastMessageTime * Utils.MILLIS_MULTIPLIER < System.currentTimeMillis() - latency) {
				try {
					requestsSender.sendMessage(bot, chatId, textMessage);
					iterator.remove();
				} catch (Exception e) {
					LOGGER.error("Unable to send response or delete chat from map", e);
				}
			}
		}
	}
}
