package com.example.telegrambotspring.entities.bots;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;

import com.example.telegrambotspring.entities.Chat;
import com.example.telegrambotspring.services.ResponseService;
import com.example.telegrambotspring.services.TelegramBotApiRequestsSender;
import com.example.telegrambotspring.utils.Pair;

@Getter
@ToString(exclude = { "messageSource", "answersForChats" })
@Slf4j
@EqualsAndHashCode(exclude = { "messageSource" })
public abstract class AbstractTelegramBot {
	@Getter(AccessLevel.NONE)
	protected final Map<Chat, Pair<Long, String>> answersForChats;
	protected String token;
	protected AtomicLong offset = new AtomicLong(0);
	protected long lastUpdateTime;
	protected UpdatesStrategy strategy;
	@Autowired
	protected MessageSource messageSource;

	public AbstractTelegramBot(String token, Map<Chat, Pair<Long, String>> answersForChats, UpdatesStrategy strategy) {
		this.token = token;
		this.answersForChats = answersForChats;
		this.strategy = strategy;
	}


	public List<JSONObject> getUpdates(TelegramBotApiRequestsSender requestsSender) throws Exception {
		JSONArray updates = requestsSender.getUpdates(this);
		lastUpdateTime = System.currentTimeMillis();

		List<JSONObject> result = new LinkedList<>();
		for (Object o : updates) {
			if (!(o instanceof JSONObject)) {
				log.debug(o + " is not JSONObject");
				continue;
			}
			result.add((JSONObject) o);
		}

		return result;
	}

	public void processUpdates(ResponseService responseService, TelegramBotApiRequestsSender requestsSender, JSONObject... updates) {
		for (JSONObject update : updates) {
			String chatType = update.optJSONObject("message")
					.optJSONObject("chat")
					.optString("type", "");

			if (chatType.isEmpty()) {
				log.debug("Unable to get chat type: " + update);
				continue;
			}

			boolean processed = true;

			if ("private".equalsIgnoreCase(chatType)) {
				try {
					processDirectMessage(requestsSender, update);
				} catch (Exception e) {
					processed = false;
					log.error("Unable to process direct message", e);
				}
			} else {
				try {
					processGroupMessage(responseService, update);
				} catch (Exception e) {
					processed = false;
					log.error("Unable to process group message", e);
				}
			}

			if (processed) {
				long update_id = update.getLong("update_id");
				offset = new AtomicLong(update_id);
			}
		}
	}

	protected String getMessageString(String messageName, String lang) {
		return messageSource.getMessage(messageName, null, Locale.forLanguageTag(lang));
	}

	protected abstract void processDirectMessage(TelegramBotApiRequestsSender requestsSender, JSONObject update) throws Exception;

	protected abstract void processGroupMessage(ResponseService responseService, JSONObject update);

	public enum UpdatesStrategy {
		LONG_POOLING,
		WEBHOOKS
	}
}
