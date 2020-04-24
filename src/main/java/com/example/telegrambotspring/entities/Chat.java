package com.example.telegrambotspring.entities;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@ToString
@EqualsAndHashCode
public class Chat {
	private int chatId;
	private boolean isGroup;

	public Chat(int chatId, boolean isGroup) {
		this.chatId = chatId;
		this.isGroup = isGroup;
	}

	public Chat(int chatId, String type) {
		this.chatId = chatId;
		this.isGroup = "group".equalsIgnoreCase(type);
	}
}
