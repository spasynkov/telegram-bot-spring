package com.example.telegrambotspring.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;

import com.example.telegrambotspring.services.TelegramWebhooksService;

/**
 * The class уровня (слоя) REST Controller
 * в данном классе будет реализована логика обработки клиентских запросов
 * REST это передача состояний ресурса между сервером и клиентом
 * В REST почти во всех случаях используется протокол HTTP
 * ресурсы в REST могут быть представлены в любой форме — JSON, XML, текст, или даже HTML
 * Ресурс в REST — это все, что может быть передано между клиентом и сервером
 * Действия в REST определяются http-методами.
 * Get, Post, Put, Delete, Patch, и другие.
 * Самые часто-используемые обозначаются аббревиатурой CRUD:
 * Create — POST
 * Read — GET
 * Update — PUT
 * Delete — DELETE
 * <b>webhooksService</b>
 * @author  Stas Pasynkov
 * @see     com.example.telegrambotspring.controllers.MainController
 * @see     com.example.telegrambotspring.controllers.RestController
 * @version 1.0.1
 */

@RestController
@RequestMapping(value = "/rest", method = RequestMethod.POST, produces = "application/json")
public class TelegramApiListenerController {
	/** Поле ??? */
	private TelegramWebhooksService webhooksService;

	@Autowired
	public TelegramApiListenerController(TelegramWebhooksService webhooksService) {
		this.webhooksService = webhooksService;
	}


  /**
   * Метод обработки запросов от бота в режиме webhooks - бот реагирует только на входящие сообщения
   * @param botToken - уникальный идентификатор, ключ для доступа к боту
   * @param jsonString - текст сообщения в формате json
   * @return возвращает - результат обработки сообщения с чата
   * @see com.example.telegrambotspring.BotRunner#run(String...)
   */
	@RequestMapping("/{botToken}")
	public String proceedTelegramApiWebhook(@PathVariable String botToken, @RequestBody String jsonString) {

		return webhooksService.proceedTelegramApiWebhook(botToken, jsonString);
	}
}
