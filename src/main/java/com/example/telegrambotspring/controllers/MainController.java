package com.example.telegrambotspring.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * The class уровня (слоя) Controller
 * вызов web интерфейса
 * @author  Stas Pasynkov
 *
 * @see     com.example.telegrambotspring.controllers.RestController
 * @see     com.example.telegrambotspring.controllers.TelegramApiListenerController
 * @version 1.0.1
 */
@Controller
public class MainController {

	/**
	 * Метод вызова главной страницы web интерфейса
	 * @return возвращает главную страницу web интерфейса index.html
	 */
	@RequestMapping("/")
	public String index() {
		return "index.html";
	}
	//Test commit1
}
