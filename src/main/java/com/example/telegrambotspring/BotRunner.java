package com.example.telegrambotspring;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.example.telegrambotspring.entities.Chat;
import com.example.telegrambotspring.services.ResponseService;
import com.example.telegrambotspring.services.TelegramBotApiRequestsSender;

@Component
public class BotRunner implements CommandLineRunner {
	private static final Logger LOGGER = LoggerFactory.getLogger(BotRunner.class);
	private static final int _1_MINUTE = 60 * 1000;
	private static final int _3_SECONDS = 3 * 1000;
	private static final int _5_SECONDS = 5 * 1000;
	private static final int MIN_LATENCY_BEFORE_SENDING_MESSAGE_IN_GROUP = _5_SECONDS;//_1_MINUTE;
	private static final int MIN_LATENCY_BEFORE_SENDING_MESSAGE_IN_PRIVATE = _3_SECONDS;
	private static final int LATENCY_BETWEEN_GETTING_UPDATES = _5_SECONDS;
	private static final String ADD_SONG = "Запам'ятай пісню";
	private static final String WRITING_SONG = "Записую...";
	private static final String FORGET_ME = "Забудь мене";
	private static final String START = "/start";
	private static final String GREETINGS = "привіт)\nдавай поспіваємо";
	private static final String MASTER_GREETINGS = "Слухаю тебе";
	private final TelegramBotApiRequestsSender requestsSender;
	private final ResponseService responseService;
	private final Map<Chat, Utils.Pair<Long, String>> answersForChats = new ConcurrentHashMap<>();
	private Thread sendResponsesThread;

	private boolean isMasterModeOn = false;
	private boolean isAddSongOn = false;

	private boolean isStopNeeded = false;

	@Autowired
	public BotRunner(TelegramBotApiRequestsSender requestsSender, ResponseService responseService) {
		this.requestsSender = requestsSender;
		this.responseService = responseService;
	}

	public void stop() {
		isStopNeeded = true;

		if (sendResponsesThread != null) {
			sendResponsesThread.interrupt();
		}
	}

	@Override
	public void run(String... args) throws Exception {
		initSendResponsesThread();
		while (!isStopNeeded) {
			List<JSONObject> updates = getUpdates();

			processUpdates(updates);
			TimeUnit.MILLISECONDS.sleep(LATENCY_BETWEEN_GETTING_UPDATES);
		}
	}

	private void initSendResponsesThread() {
		if (sendResponsesThread == null) {
			Runnable r = () -> {
				try {
					while (!Thread.currentThread().isInterrupted()) {
						sendResponses();

						TimeUnit.MILLISECONDS.sleep(Math.min(
								MIN_LATENCY_BEFORE_SENDING_MESSAGE_IN_GROUP,
								MIN_LATENCY_BEFORE_SENDING_MESSAGE_IN_PRIVATE));
					}
				} catch (Exception e) {
					Thread.currentThread().interrupt();
					LOGGER.debug("Exception occurred while sending responses");
				}
			};

			sendResponsesThread = new Thread(r);
			sendResponsesThread.start();
		}
	}

	private void sendResponses() {
		Iterator<Map.Entry<Chat, Utils.Pair<Long, String>>> iterator = answersForChats.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry<Chat, Utils.Pair<Long, String>> entry = iterator.next();
			int chatId = entry.getKey().getChatId();
			long lastMessageTime = entry.getValue().getFirst();
			String preparedResponse = entry.getValue().getSecond();

			long latency = entry.getKey().isGroup()
					? MIN_LATENCY_BEFORE_SENDING_MESSAGE_IN_GROUP
					: MIN_LATENCY_BEFORE_SENDING_MESSAGE_IN_PRIVATE;

			if (lastMessageTime * 1000 < System.currentTimeMillis() - latency) {
				try {
					requestsSender.sendMessage(chatId, preparedResponse);
					iterator.remove();
				} catch (Exception e) {
					LOGGER.error("Unable to send response or delete chat from map", e);
				}
			}
		}
	}

	private List<JSONObject> getUpdates() throws Exception {
		JSONArray updates = requestsSender.getUpdates();

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

	private void processUpdates(List<JSONObject> updates) {
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
					processDirectMessage(update);
				} catch (Exception e) {
					processed = false;
					LOGGER.error("Unable to process direct message", e);
				}
			} else {
				try {
					processGroupMessage(update);
				} catch (Exception e) {
					processed = false;
					LOGGER.error("Unable to process group message", e);
				}
			}

			if (processed) {
				long update_id = update.getLong("update_id");
				requestsSender.setOffset(update_id);
			}
		}
	}

	private void processDirectMessage(JSONObject update) throws Exception {
		JSONObject message = update.getJSONObject("message");
		String text = message.getString("text");
		int chatId = message.getJSONObject("chat").getInt("id");

		String normalizedText = Utils.normalizeString(text);
		if ("master".equals(normalizedText)) {
			becameMaster(chatId);
		} else if (Utils.normalizeString(ADD_SONG).equalsIgnoreCase(normalizedText)) {
			readyToAddSong(chatId);
		} else if (Utils.normalizeString(FORGET_ME).equalsIgnoreCase(normalizedText)) {
			processForgetMe(chatId);
		} else if (START.equals(normalizedText)) {
			requestsSender.sendMessage(chatId, GREETINGS);
		} else {
			if (isMasterModeOn) {
				if (isAddSongOn) {
					List<String> result = new LinkedList<>();
					for (String s : text.split(System.lineSeparator())) {
						result.add(s.trim());
					}
				}
			} else {
				sendNotImplemented(update);
			}
		}
	}

	private void becameMaster(int chatId) throws Exception {
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
		requestsSender.sendMessage(chatId, "Welcome back, my master!\n" + MASTER_GREETINGS, replyKeyboard);
	}

	private void readyToAddSong(int chatId) throws Exception {
		isAddSongOn = true;
		requestsSender.sendMessage(chatId, WRITING_SONG);
	}

	private void processForgetMe(int chatId) throws Exception {
		isMasterModeOn = false;
		isAddSongOn = false;
		JSONObject json = new JSONObject();
		json.put("remove_keyboard", true);
		requestsSender.sendMessage(chatId, GREETINGS, json);
	}

	private void sendNotImplemented(JSONObject update) throws Exception {
		int chatId = update.getJSONObject("message").getJSONObject("chat").getInt("id");
		requestsSender.sendMessage(chatId, "Not implemented yet!");
	}

	private void processGroupMessage(JSONObject update) {
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
			answersForChats.put(new Chat(chatId, "group".equalsIgnoreCase(type)), new Utils.Pair<>(date, response));
		}
	}
}
