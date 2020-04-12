package com.example.telegrambotspring.entities;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class Received {
	private Map<Chat, List<Message>> incomingMessage;

	public Received(Map<Chat, List<Message>> incomingMessage) {
		this.incomingMessage = incomingMessage;
	}

	public Map<Chat, List<Message>> getIncomingMessage() {
		return incomingMessage;
	}

	public void addMessage(int chatId, boolean isGroup, String text, long data) {
		List<Message> messageList = new ArrayList<>();
		Message message = new Message(text, data);
		Chat chat = new Chat(chatId, isGroup);
		if (incomingMessage.get(chat) == null) {
			messageList.add(message);
			incomingMessage.put(chat, messageList);
		} else {
			incomingMessage.get(chat).add(message);
		}
	}

	public String getTextMessage(Chat chat) {
		return incomingMessage.get(chat).get(0).getText();
	}

	public long getDateMessage(Chat chat) {
		return incomingMessage.get(chat).get(0).getData();
	}
}


