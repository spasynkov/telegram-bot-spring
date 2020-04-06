package com.example.telegrambotspring.controllers;

import com.example.telegrambotspring.entities.SongVerse;
import com.example.telegrambotspring.services.DatabaseService;
import com.example.telegrambotspring.services.TelegramBotApiRequestsSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
	/** переменная для записи логов  */
	private static final Logger MYLOGGER = LoggerFactory.getLogger(RestController.class);

    /** объект класса для работы с БД следующего уровня - сервисный  - сервисный объект для работы с БД*/
	private DatabaseService service;

	/**
	 * Конструктор - создание нового объекта с определенными значениями
	 * @param service сервисный объект для работы с БД
	 */
	@Autowired
	public RestController(DatabaseService service) {
		this.service = service;
	}

    /**
     * Метод получения полного списка песен
     * @return возвращает список всех песен с текстом и др. параметрами типа SongVerse одной строкой
     */
	@GetMapping("/all")
	public String getAll() {
		return service.getAll().toString();
	}

	/**
	 * Метод получения полного списка песен данного исполнителя
	 * @param artistName имя исполнителя
	 * @return возвращает список всех песен данного исполнителя с текстом и др. параметрами типа SongVerse одной строкой
	 */
	@GetMapping("/all/artist/{artistName}")
	public String getAllByArtist(@PathVariable String artistName) {
		return service.getAllByArtist(artistName).toString();
	}

	/**
	 * Метод получения полного списка всех версий песен данного исполнителя и названия песни
	 * @param artistName имя исполнителя
	 * @param songName название песни
	 * @return возвращает список всех версий песен данного исполнителя и названия песни с текстом и др. параметрами типа SongVerse одной строкой
	 */
	@GetMapping("/all/artist/{artistName}/song/{songName}")
	public String getAllByArtist(@PathVariable String artistName, @PathVariable String songName) {
		return service.getAllByArtistAndSong(artistName, songName).toString();
	}

	/**
	 * Метод посылает запрос на добавления новой песни в БД
	 * @param verse объект типа SongVerse со всеми атрибутами
	 * @param artistName имя исполнителя
	 * @param songName название песни
	 * @return возвращает объект типа SongVerse
	 * текст песни и все ее атрибуды с присвоенным Id из БД одной строкой
	 */
	@PutMapping("/all/artist/{artistName}/song/{songName}")
	public String addSong(@RequestBody SongVerse verse, @PathVariable String artistName, @PathVariable String songName) {
		return service.addSong(verse, artistName, songName).toString();
	}

	/**
	 * Метод посылает запрос на изменение песни в БД
	 * @param verse объект типа SongVerse со всеми атрибутами
	 * @param artistName имя исполнителя
	 * @param songName название песни
	 * @return возвращает объект типа SongVerse
	 * текст песни и все ее атрибуды с присвоенным Id из БД одной строкой
	 */
	@PostMapping("/all/artist/{artistName}/song/{songName}")
	public String editSong(@RequestBody SongVerse verse, @PathVariable String artistName, @PathVariable String songName) {
		return service.editSong(verse, artistName, songName).toString();
	}

	/**
	 * Метод посылает запрос на удаление песни из БД
	 * @param verse объект типа SongVerse со всеми атрибутами
	 * @param artistName имя исполнителя
	 * @param songName название песни
	 * @return возвращает пустой объект типа SongVerse в виде стоки
	 */
	@DeleteMapping("/all/artist/{artistName}/song/{songName}")
	public String deleteSong(@RequestBody SongVerse verse, @PathVariable String artistName, @PathVariable String songName) {
		return service.deleteSong(verse, artistName, songName).toString();
	}
}
