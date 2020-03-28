package com.example.telegrambotspring.services;

public interface ResponseService {
	void updateSongVerses(String artist) throws Exception;

	String getResponse(String... text) throws Exception;
}
