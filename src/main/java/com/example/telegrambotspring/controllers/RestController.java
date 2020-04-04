package com.example.telegrambotspring.controllers;

import com.example.telegrambotspring.entities.SongVerse;
import com.example.telegrambotspring.services.DatabaseService;
import com.example.telegrambotspring.services.TelegramBotApiRequestsSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * The class уровня (слоя) Controller
 * обработка запросов web интерфейса
 * <b>service</b>
 * @author  Stas Pasynkov
 *
 * @see     com.example.telegrambotspring.controllers.MainController
 * @see     com.example.telegrambotspring.controllers.TelegramApiListenerController
 * @version 1.0.1
 */
@org.springframework.web.bind.annotation.RestController
@RequestMapping(value = "/rest", produces = "application/json")
public class RestController {
	private static final Logger MYLOGGER = LoggerFactory.getLogger(RestController.class);

    /** переменная класса ???  */
	private DatabaseService service;

	@Autowired
	public RestController(DatabaseService service) {
		this.service = service;
	}


    /**
     * Метод вывода полного списка песен
     * @return возвращает список всех песен с текстом и др. параметрами типа SongVerse
     * @see     com.example.telegrambotspring.entities.SongVerse
     */
	@GetMapping("/all")
	public String getAll() {
		MYLOGGER.debug("MYLOGGER : RestController - > getAll");

		String str = service.getAll().toString();
        MYLOGGER.debug("MYLOGGER : RestController - > getAll" + str);

        return str;
//		return service.getAll().toString();
	}

	@GetMapping("/all/artist/{artistName}")
	public String getAllByArtist(@PathVariable String artistName) {
		MYLOGGER.debug("MYLOGGER : RestController - > getAllByArtist");

		return service.getAllByArtist(artistName).toString();
	}


    /**
     * Метод вызова страницы редактирования песни
     * @param artistName - имя артиста
     * @param songName - название песни
     * @return возвращает главную страницу web интерфейса index.html
     */
	@GetMapping("/all/artist/{artistName}/song/{songName}")
	public String getAllByArtist(@PathVariable String artistName, @PathVariable String songName) {
		MYLOGGER.debug("MYLOGGER : RestController - > getAllByArtist_2");

		return service.getAllByArtistAndSong(artistName, songName).toString();
	}

	@PutMapping("/all/artist/{artistName}/song/{songName}")
	public String addSong(@RequestBody SongVerse verse, @PathVariable String artistName, @PathVariable String songName) {
		MYLOGGER.debug("MYLOGGER : RestController - > addSong");

		return service.addSong(verse, artistName, songName).toString();
	}

	@PostMapping("/all/artist/{artistName}/song/{songName}")
	public String editSong(@RequestBody SongVerse verse, @PathVariable String artistName, @PathVariable String songName) {
		MYLOGGER.debug("MYLOGGER : RestController - > editSong");

		return service.editSong(verse, artistName, songName).toString();
	}

	@DeleteMapping("/all/artist/{artistName}/song/{songName}")
	public String deleteSong(@RequestBody SongVerse verse, @PathVariable String artistName, @PathVariable String songName) {
		MYLOGGER.debug("MYLOGGER : RestController - > deleteSong");

		return service.deleteSong(verse, artistName, songName).toString();
	}
}
