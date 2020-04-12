package com.example.telegrambotspring.entities;

import java.util.Objects;

public class Message {
	private String text;
	private long data;

	public Message(String text, long data) {
		this.text = text;
		this.data = data;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public long getData() {
		return data;
	}

	public void setData(long data) {
		this.data = data;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Message message = (Message) o;
		return data == message.data &&
				text.equals(message.text);
	}

	@Override
	public int hashCode() {
		return Objects.hash(text, data);
	}

	@Override
	public String toString() {
		return "Message{" +
				"text='" + text + '\'' +
				", data=" + data +
				'}';
	}
}
