package com.example.telegrambotspring.services;


/**
 * The interface уровня (слоя) Service
 * сервис формирования ответов
 * классы наследники:
 * @see     com.example.telegrambotspring.services.DatabaseResponseService
 * @see     com.example.telegrambotspring.services.AbstractResponseService
 * @version 1.0.1
 */
public interface ResponseService {
	void updateSongVerses(String artist) throws Exception;

	/**
	 * метод - формирования ответа
	 * @param text массив строк содержащих message из тела запроса
	 * @return возвращает строку с ответом для телеграмм
	 */
	String getResponse(String... text) throws Exception;
}
