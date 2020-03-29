package com.example.telegrambotspring;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import com.example.telegrambotspring.entities.bots.AbstractTelegramBot;
import com.example.telegrambotspring.services.ResponseService;
import com.example.telegrambotspring.services.TelegramBotApiRequestsSender;

@Component
public class BotRunner implements CommandLineRunner {
	private static final Logger LOGGER = LoggerFactory.getLogger(BotRunner.class);

	private final TaskExecutor executor;
	private final TelegramBotApiRequestsSender requestsSender;
	private final ResponseService responseService;
	private final List<AbstractTelegramBot> bots;

	@Value("${app.getting-updates-latency}")
	private long latencyBetweenGettingUpdates;

	@Autowired
	public BotRunner(ThreadPoolTaskExecutor executor,
	                 TelegramBotApiRequestsSender requestsSender,
	                 ResponseService responseService,
	                 AbstractTelegramBot... bots) {

		this.executor = executor;
		this.requestsSender = requestsSender;
		this.responseService = responseService;
		this.bots = new LinkedList<>(Arrays.asList(bots));
	}

	@Override
	public void run(String... args) {
		Iterator<AbstractTelegramBot> iterator = bots.iterator();
		while (iterator.hasNext()) {
			AbstractTelegramBot bot = iterator.next();

			// remove all bots with not long pooling updates strategy
			if (bot.getStrategy() != AbstractTelegramBot.UpdatesStrategy.LONG_POOLING) {
				iterator.remove();
				continue;
			}

			Runnable r = () -> {
				try {
					while (!Thread.currentThread().isInterrupted()) {
						try {
							sleepIfNeeded(bot);
						} catch (InterruptedException e) {
							Thread.currentThread().interrupt();
							LOGGER.warn("Exception while bot sleep (" + bot + ")", e);
							break;
						}

						List<JSONObject> updates = bot.getUpdates(requestsSender);
						bot.processUpdates(responseService, requestsSender, updates.toArray(new JSONObject[0]));
					}
				} catch (Exception e) {
					LOGGER.error("Unable to get or process updates by " + bot + ". Stopping bot...", e);
				}
			};
			executor.execute(r);
		}
	}

	private void sleepIfNeeded(AbstractTelegramBot bot) throws InterruptedException {
		long processingTime = System.currentTimeMillis() - bot.getLastUpdateTime();
		long sleepTime = latencyBetweenGettingUpdates - processingTime;
		if (sleepTime > 0) {
			TimeUnit.MILLISECONDS.sleep(latencyBetweenGettingUpdates);
		}
	}
}
