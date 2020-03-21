package com.example.telegrambotspring.entities;

import java.util.Objects;

public class Chat {
	private int chatId;
	private boolean isGroup;

	public Chat(int chatId, boolean isGroup) {
		this.chatId = chatId;
		this.isGroup = isGroup;
	}

	public int getChatId() {
		return chatId;
	}

	public boolean isGroup() {
		return isGroup;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Chat chat = (Chat) o;
		return chatId == chat.chatId &&
				isGroup == chat.isGroup;
	}

	@Override
	public int hashCode() {
		return Objects.hash(chatId, isGroup);
	}

	@Override
	public String toString() {
		return (isGroup ? "Group" : "Chat") + "{chatId=" + chatId + '}';
	}
}
