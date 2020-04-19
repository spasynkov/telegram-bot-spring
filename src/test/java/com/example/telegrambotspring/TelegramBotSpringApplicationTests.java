package com.example.telegrambotspring;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.telegrambotspring.controllers.MainController;
import com.example.telegrambotspring.controllers.RestController;
import com.example.telegrambotspring.controllers.TelegramApiListenerController;
import com.example.telegrambotspring.entities.Chat;
import com.example.telegrambotspring.entities.bots.SongsBot;
import com.example.telegrambotspring.services.ResponseService;
import com.example.telegrambotspring.services.SendResponsesService;
import com.example.telegrambotspring.services.TelegramBotApiRequestsSender;
import com.example.telegrambotspring.services.TelegramWebhooksService;
import com.example.telegrambotspring.utils.Pair;

@SpringBootTest
class TelegramBotSpringApplicationTests {

	@Autowired
	private MainController mainController;

	@Autowired
	private RestController restController;

	@Autowired
	private TelegramApiListenerController apiListenerController;

	@Autowired
	private Map<Chat, Pair<Long, String>> answersForChats;

	@Autowired
	private SongsBot songsBot;

	@Autowired
	private ResponseService responseService;

	@Autowired
	private SendResponsesService sendResponsesService;

	@Autowired
	private TelegramBotApiRequestsSender telegramBotApiRequestsSender;

	@Autowired
	private TelegramWebhooksService telegramWebhooksService;

	@Test
	void contextLoads() {
		assertThat(mainController).isNotNull();
		assertThat(restController).isNotNull();
		assertThat(apiListenerController).isNotNull();

		assertThat(answersForChats).isNotNull();
		assertThat(songsBot).isNotNull();

		assertThat(responseService).isNotNull();
		assertThat(sendResponsesService).isNotNull();
		assertThat(telegramBotApiRequestsSender).isNotNull();
		assertThat(telegramWebhooksService).isNotNull();
	}

}
