package com.example.telegrambotspring.entities;

import java.util.Objects;

public class Message {
	private String text;
	private long time;
	private String type;
	private long updateId;
	private String languageCode;
	private int chatId;

	Message(String type, String text, long time, long updateId, String languageCode, int chatId) {
		this.text = text;
		this.time = time;
		this.type = type;
		this.updateId = updateId;
		this.languageCode = languageCode;
		this.chatId = chatId;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public long getTime() {
		return time;
	}

	public String getType() {
		return type;
	}

	public long getUpdateId() {
		return updateId;
	}

	public String getLanguageCode() {
		return languageCode;
	}

	public int getChatId() {
		return chatId;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Message message = (Message) o;
		return time == message.time &&

				text.equals(message.text) &&
				type.equals(message.type);
	}

	@Override
	public int hashCode() {
		return Objects.hash(text, time, type);
	}

	@Override
	public String toString() {
		return "Message{" +
				"text='" + text + '\'' +
				", time=" + time +
				", type='" + type + '\'' +
				'}';
	}
}
