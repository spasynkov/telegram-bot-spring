package com.example.telegrambotspring.services;

public interface ResponseService {
	void updateResponseSets() throws Exception;

	String getResponse(String... text) throws Exception;
}
