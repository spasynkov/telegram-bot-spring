package com.example.telegrambotspring.entities;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.telegrambotspring.services.ResponseService;
import com.example.telegrambotspring.services.TelegramBotApiRequestsSender;
import com.example.telegrambotspring.utils.Pair;
import com.example.telegrambotspring.utils.Utils;

public class Bot {
	private static final Logger LOGGER = LoggerFactory.getLogger(Bot.class);
	private static final int _1_MINUTE = 60 * 1000;

	private static final String ADD_SONG = "Запам'ятай пісню";
	private static final String WRITING_SONG = "Записую...";
	private static final String FORGET_ME = "Забудь мене";
	private static final String START = "/start";
	private static final String GREETINGS = "привіт)\nдавай поспіваємо";
	private static final String MASTER_GREETINGS = "Слухаю тебе";
	private final Map<Chat, Pair<Long, String>> answersForChats;
	private String token;
	private AtomicLong offset = new AtomicLong(0);
	private long lastUpdateTime;
	private boolean isMasterModeOn = false;
	private boolean isAddSongOn = false;

	public Bot(String token, Map<Chat, Pair<Long, String>> answersForChats) {
		this.token = token;
		this.answersForChats = answersForChats;
	}

	public String getToken() {
		return token;
	}

	public AtomicLong getOffset() {
		return offset;
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

	public void processUpdates(ResponseService responseService, TelegramBotApiRequestsSender requestsSender, List<JSONObject> updates) {
		for (JSONObject update : updates) {
			String chatType = update.optJSONObject("message")
					.optJSONObject("chat")
					.optString("type", "");

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
					processGroupMessage(responseService, update);
				} catch (Exception e) {
					processed = false;
					LOGGER.error("Unable to process group message", e);
				}
			}

			if (processed) {
				long update_id = update.getLong("update_id");
				offset = new AtomicLong(update_id);
			}
		}
	}

	private void processDirectMessage(TelegramBotApiRequestsSender requestsSender, JSONObject update) throws Exception {
		JSONObject message = update.getJSONObject("message");
		String text = message.getString("text");
		int chatId = message.getJSONObject("chat").getInt("id");

		String normalizedText = Utils.normalizeString(text);
		if ("master".equals(normalizedText)) {
			becameMaster(requestsSender, chatId);
		} else if (Utils.normalizeString(ADD_SONG).equalsIgnoreCase(normalizedText)) {
			readyToAddSong(requestsSender, chatId);
		} else if (Utils.normalizeString(FORGET_ME).equalsIgnoreCase(normalizedText)) {
			processForgetMe(requestsSender, chatId);
		} else if (START.equals(normalizedText)) {
			requestsSender.sendMessage(this, chatId, GREETINGS);
		} else {
			if (isMasterModeOn) {
				if (isAddSongOn) {
					List<String> result = new LinkedList<>();
					for (String s : text.split(System.lineSeparator())) {
						result.add(s.trim());
					}
				}
			} else {
				sendNotImplemented(requestsSender, update);
			}
		}
	}

	private void becameMaster(TelegramBotApiRequestsSender requestsSender, int chatId) throws Exception {
		isMasterModeOn = true;

		JSONArray tableRows = new JSONArray();
		JSONArray rowOfButtons = new JSONArray();
		tableRows.put(rowOfButtons);

		JSONObject addSongButton = new JSONObject();
		addSongButton.put("text", ADD_SONG);
		rowOfButtons.put(addSongButton);
		JSONObject forgetMeButton = new JSONObject();
		forgetMeButton.put("text", FORGET_ME);
		rowOfButtons.put(forgetMeButton);

		JSONObject replyKeyboard = new JSONObject();
		replyKeyboard.put("keyboard", tableRows);
		replyKeyboard.put("one_time_keyboard", true);
		replyKeyboard.put("resize_keyboard", true);
		requestsSender.sendMessage(this, chatId, "Welcome back, my master!\n" + MASTER_GREETINGS, replyKeyboard);
	}

	private void readyToAddSong(TelegramBotApiRequestsSender requestsSender, int chatId) throws Exception {
		isAddSongOn = true;
		requestsSender.sendMessage(this, chatId, WRITING_SONG);
	}

	private void processForgetMe(TelegramBotApiRequestsSender requestsSender, int chatId) throws Exception {
		isMasterModeOn = false;
		isAddSongOn = false;
		JSONObject json = new JSONObject();
		json.put("remove_keyboard", true);
		requestsSender.sendMessage(this, chatId, GREETINGS, json);
	}

	private void sendNotImplemented(TelegramBotApiRequestsSender requestsSender, JSONObject update) throws Exception {
		int chatId = update.getJSONObject("message").getJSONObject("chat").getInt("id");
		requestsSender.sendMessage(this, chatId, "Not implemented yet!");
	}

	private void processGroupMessage(ResponseService responseService, JSONObject update) {
		JSONObject message = update.getJSONObject("message");

		long date = message.getLong("date");
		if (System.currentTimeMillis() - _1_MINUTE > date * 1000) {
			LOGGER.debug("Skipping message as it's too old");
			return;
		}

		String text = message.optString("text", "");
		String response = null;
		try {
			response = responseService.getResponse(text);
		} catch (Exception e) {
			LOGGER.error("Unable to find suitable response", e);
		}
		if (response != null && !response.isEmpty()) {
			JSONObject chat = message.getJSONObject("chat");
			int chatId = chat.getInt("id");
			String type = chat.getString("type");
			answersForChats.put(new Chat(chatId, type), new Pair<>(date, response));
		}
	}

	public boolean isAlive() {
		return true;
	}

	public long getLastUpdateTime() {
		return lastUpdateTime;
	}
}
