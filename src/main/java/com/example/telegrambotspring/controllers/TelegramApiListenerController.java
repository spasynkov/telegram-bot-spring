package com.example.telegrambotspring.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;

import com.example.telegrambotspring.services.TelegramWebhooksService;

@RestController
@RequestMapping(value = "/rest", method = RequestMethod.POST, produces = "application/json")
public class TelegramApiListenerController {

	private TelegramWebhooksService webhooksService;

	@Autowired
	public TelegramApiListenerController(TelegramWebhooksService webhooksService) {
		this.webhooksService = webhooksService;
	}

	@RequestMapping("/{botToken}")
	public String proceedTelegramApiWebhook(@PathVariable String botToken, @RequestBody String jsonString) {
		return webhooksService.proceedTelegramApiWebhook(botToken, jsonString);
	}
}
