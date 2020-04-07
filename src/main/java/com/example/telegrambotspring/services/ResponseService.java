package com.example.telegrambotspring.services;


/**
 * The interface уровня (слоя) Service
 * ответ сервиса  ????????????????
 * классы наследники:
 * @see     com.example.telegrambotspring.services.DatabaseResponseService
 * @see     com.example.telegrambotspring.services.AbstractResponseService
 * @version 1.0.1
 */
public interface ResponseService {
	void updateSongVerses(String artist) throws Exception;

	String getResponse(String... text) throws Exception;
}
