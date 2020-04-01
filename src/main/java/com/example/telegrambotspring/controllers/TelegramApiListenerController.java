package com.example.telegrambotspring.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;

import com.example.telegrambotspring.services.TelegramWebhooksService;

/**
 * The class ...
 * <b>webhooksService</b>
 * @author  Stas Pasynkov
 * @see     com.example.telegrambotspring.controllers.TelegramApiListenerController
 * @version 1.0
 */

@RestController
@RequestMapping(value = "/rest", method = RequestMethod.POST, produces = "application/json")
public class TelegramApiListenerController {
	/** Поле  */
	private TelegramWebhooksService webhooksService;

	@Autowired
	public TelegramApiListenerController(TelegramWebhooksService webhooksService) {
		this.webhooksService = webhooksService;
	}


/**
 * Метод обработки запросов от бота в режиме webhooks - бот реагирует только на входящие сообщения
 * @param botToken - уникальный идентификатор, ключ для доступа к боту
 * @param jsonString - текст сообщения в формате json
 * @return возвращает
 */
	@RequestMapping("/{botToken}")
	public String proceedTelegramApiWebhook(@PathVariable String botToken, @RequestBody String jsonString) {
		return webhooksService.proceedTelegramApiWebhook(botToken, jsonString);
	}
}
