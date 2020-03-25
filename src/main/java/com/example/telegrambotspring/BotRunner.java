package com.example.telegrambotspring;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.example.telegrambotspring.entities.Bot;
import com.example.telegrambotspring.services.ResponseService;
import com.example.telegrambotspring.services.TelegramBotApiRequestsSender;

@Component
public class BotRunner implements CommandLineRunner {
	private static final Logger LOGGER = LoggerFactory.getLogger(BotRunner.class);

	private final TelegramBotApiRequestsSender requestsSender;
	private final ResponseService responseService;
	private final List<Bot> bots;

	@Value("${app.getting-updates-latency}")
	private long latencyBetweenGettingUpdates;

	@Autowired
	public BotRunner(TelegramBotApiRequestsSender requestsSender, ResponseService responseService, Bot... bots) {
		this.requestsSender = requestsSender;
		this.responseService = responseService;
		this.bots = new LinkedList<>(Arrays.asList(bots));
	}

	@Override
	public void run(String... args) throws Exception {
		boolean anyALiveBot = true;
		while (anyALiveBot) {
			anyALiveBot = false;    // starting new iterations with default false
			for (Bot bot : bots) {
				if (bot.isAlive()) {
					anyALiveBot = true;
					sleepIfNeeded(bot);

					List<JSONObject> updates = bot.getUpdates(requestsSender);

					bot.processUpdates(responseService, requestsSender, updates);
				}
			}
		}
	}

	private void sleepIfNeeded(Bot bot) throws InterruptedException {
		long processingTime = System.currentTimeMillis() - bot.getLastUpdateTime();
		long sleepTime = latencyBetweenGettingUpdates - processingTime;
		if (sleepTime > 0) {
			TimeUnit.MILLISECONDS.sleep(latencyBetweenGettingUpdates);
		}
	}
}
