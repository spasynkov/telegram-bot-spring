package com.example.telegrambotspring.entities;

import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class Received {
	private Map<Integer, List<Message>> messages = new ConcurrentHashMap<>();


//	public Received(Map<Chat, List<Message>> incomingMessage) {
//		this.incomingMessage = incomingMessage;
//	}

	public Map<Integer, List<Message>> getMessages() {
		return messages;
	}

	//
	public void addMessage(JSONObject jsonObject) {
		int chatId = getMessageChatId(jsonObject);
		String type = getType(jsonObject);
		String text = getMessageText(jsonObject);
		long time = getMessageTime(jsonObject);
		long updateId = getUpdateId(jsonObject);
		String languageCode = getLanguageCode(jsonObject);
		Message message = new Message(type, text, time, updateId, languageCode, chatId);
		List<Message> messageList = new LinkedList<>();
		if (messages.get(chatId) == null) {
			messageList.add(message);
			messages.put(chatId, messageList);
		} else {
			messages.get(chatId).add(message);
		}
	}

	private int getMessageChatId(JSONObject json) {
		return Integer.parseInt(json.optJSONObject("message")
				.optJSONObject("chat")
				.optString("id", ""));

	}

	private String getType(JSONObject json) {
		return json.optJSONObject("message")
				.optJSONObject("chat")
				.optString("type");
	}

	private String getMessageText(JSONObject json) {
		return json.optJSONObject("message")
				.optString("text", "");
	}

	private long getMessageTime(JSONObject json) {
		return Long.parseLong(json.optJSONObject("message")
				.optString("date", ""));
	}

	private long getUpdateId(JSONObject json) {
		return json.getLong("update_id");
	}

	private String getLanguageCode(JSONObject json) {
		return json.getJSONObject("message").getJSONObject("from").getString("language_code");
	}

}


