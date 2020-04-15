package com.example.telegrambotspring.entities.bots;

import com.example.telegrambotspring.entities.Message;
import com.example.telegrambotspring.entities.Received;
import com.example.telegrambotspring.services.ResponseService;
import com.example.telegrambotspring.services.TelegramBotApiRequestsSender;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

public abstract class AbstractTelegramBot {
	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractTelegramBot.class);
	protected Received received;
	protected String token;
	protected AtomicLong offset = new AtomicLong(0);
	protected long lastUpdateTime;
	protected UpdatesStrategy strategy;
	@Autowired
	protected MessageSource messageSource;

	public AbstractTelegramBot(String token, Received received, UpdatesStrategy strategy) {
		this.token = token;
		this.received = received;
		this.strategy = strategy;
	}

	public String getToken() {
		return token;
	}

	public AtomicLong getOffset() {
		return offset;
	}

	public long getLastUpdateTime() {
		return lastUpdateTime;
	}

	public UpdatesStrategy getStrategy() {
		return strategy;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		SongsBot bot = (SongsBot) o;
		return lastUpdateTime == bot.lastUpdateTime &&
				Objects.equals(received, bot.received) &&
				Objects.equals(token, bot.token) &&
				Objects.equals(offset, bot.offset) &&
				strategy == bot.strategy;
	}

	@Override
	public int hashCode() {
		return Objects.hash(received, token, offset, lastUpdateTime, strategy);
	}

	@Override
	public String toString() {
		return "Bot{" +
				"token='" + token + '\'' +
				", offset=" + offset +
				", lastUpdateTime=" + lastUpdateTime +
				", strategy=" + strategy +
				'}';
	}

	public List<JSONObject> getUpdates(TelegramBotApiRequestsSender requestsSender) throws Exception {
		JSONArray updates = requestsSender.getUpdates(this);
		lastUpdateTime = System.currentTimeMillis();

		List<JSONObject> result = new LinkedList<>();
		for (Object o : updates) {
			if (!(o instanceof JSONObject)) {
				LOGGER.debug(o + " is not JSONObject");
				continue;
			}
			result.add((JSONObject) o);
		}

		return result;
	}

	/**
	 * This is problem point.
	 * We did have one method "processUpdates" with return type void.
	 * I am need return type - String, but for this it is necessary "processDirectMessage" must return String. I don't have idea.
	 *
	 * @param requestsSender
	 * @param updates
	 */

	public void processUpdates(TelegramBotApiRequestsSender requestsSender, ResponseService responseService, Message... updates) {
		for (Message update : updates) {
			String chatType = update.getType();

			if (chatType.isEmpty()) {
				LOGGER.debug("Unable to get chat type: " + update);
				continue;
			}
			boolean processed = true;
			if ("private".equalsIgnoreCase(chatType)) {
				try {
					processDirectMessage(requestsSender, update);
				} catch (Exception e) {
					processed = false;
					LOGGER.error("Unable to process direct message", e);
				}
			} else {
				try {
					processGroupMessage(requestsSender, responseService, update);//вернуть в ответ в sendResponseService
				} catch (Exception e) {
					processed = false;
					LOGGER.error("Unable to process group message", e);
				}
			}

			if (processed) {
				long update_id = update.getUpdateId();
				offset = new AtomicLong(update_id);
			}
		}
	}


	protected String getMessageString(String messageName, String lang) {
		return messageSource.getMessage(messageName, null, Locale.forLanguageTag(lang));
	}

	protected abstract void processDirectMessage(TelegramBotApiRequestsSender requestsSender, Message update) throws Exception;

	protected abstract void processGroupMessage(TelegramBotApiRequestsSender requestsSender, ResponseService responseService, Message update);

	public enum UpdatesStrategy {
		LONG_POOLING,
		WEBHOOKS
	}
}
