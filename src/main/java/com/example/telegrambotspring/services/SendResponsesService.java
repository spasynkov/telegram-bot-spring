package com.example.telegrambotspring.services;

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
	private ResponseService responseService;

	@Value("${app.send-message-latency.group}")
	private long sendMessageLatencyGroup;

	@Value("${app.send-message-latency.direct}")
	private long sendMessageLatencyDirect;

	@Autowired
	public SendResponsesService(Received received,
	                            SongsBot bot,    // TODO: hardcoded bot instance. rewrite with chat response object
	                            TelegramBotApiRequestsSender requestsSender, ThreadPoolTaskExecutor executor, ResponseService responseService) {
		this.received = received;
		this.requestsSender = requestsSender;
		this.responseService = responseService;


		Runnable runnable = () -> {
			try {
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

	private void sendResponses(SongsBot bot) {
		Iterator<Map.Entry<Integer, List<Message>>> iterator = received.getMessages().entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry<Integer, List<Message>> entry = iterator.next();
			int indexLastMessage = entry.getValue().size() - 1;
			Message message = entry.getValue().get(indexLastMessage);
			long timeMessage = message.getTime();
			String typeMessage = message.getType();

			long latency = typeMessage.equalsIgnoreCase("group")
					? sendMessageLatencyGroup
					: sendMessageLatencyDirect;

			if (timeMessage * Utils.MILLIS_MULTIPLIER < System.currentTimeMillis() - latency) {
				try {
					bot.processUpdates(requestsSender, responseService, message);
					iterator.remove();
				} catch (Exception e) {
					LOGGER.error("Unable to send response or delete chat from map", e);
				}
			}
		}
	}
}
