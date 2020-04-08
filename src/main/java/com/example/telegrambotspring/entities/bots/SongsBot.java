package com.example.telegrambotspring.entities.bots;

import java.util.*;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.telegrambotspring.entities.Chat;
import com.example.telegrambotspring.services.ResponseService;
import com.example.telegrambotspring.services.TelegramBotApiRequestsSender;
import com.example.telegrambotspring.utils.Pair;
import com.example.telegrambotspring.utils.Utils;


/**
 * The class ??????
 * ????????????????
 * наследник класса:
 * @see     com.example.telegrambotspring.entities.bots.AbstractTelegramBot
 * @version 1.0.1
 */
public class SongsBot extends AbstractTelegramBot {
	private static final Logger LOGGER = LoggerFactory.getLogger(SongsBot.class);

	private boolean isMasterModeOn = false;
	private boolean isAddSongOn = false;

	public SongsBot(String token, Map<Chat, Pair<Long, String>> answersForChats, UpdatesStrategy strategy) {
		super(token, answersForChats, strategy);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		SongsBot bot = (SongsBot) o;
		return super.equals(o) &&
				isMasterModeOn == bot.isMasterModeOn &&
				isAddSongOn == bot.isAddSongOn;
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), isMasterModeOn, isAddSongOn);
	}

	@Override
	public String toString() {
		String string = super.toString();
		return "SongsBot{" +
				string.substring(string.indexOf("{") + 1, string.lastIndexOf("}")) +
				(isMasterModeOn ? ", isMasterModeOn=true" : "") +
				(isAddSongOn ? ", isAddSongOn=true" : "") +
				'}';
	}

	@Override
	protected void processDirectMessage(TelegramBotApiRequestsSender requestsSender, JSONObject update) throws Exception {
		JSONObject message = update.getJSONObject("message");
		String lang = message.getJSONObject("from").getString("language_code");
		String text = message.getString("text");
		int chatId = message.getJSONObject("chat").getInt("id");

		String normalizedText = Utils.normalizeString(text);
		if ("master".equals(normalizedText)) {
			becameMaster(requestsSender, chatId, lang);
		} else if (Utils.normalizeString(getMessageString("add_song", lang)).equalsIgnoreCase(normalizedText)) {
			readyToAddSong(requestsSender, chatId, lang);
		} else if (Utils.normalizeString(getMessageString("forget_me", lang)).equalsIgnoreCase(normalizedText)) {
			processForgetMe(requestsSender, chatId, lang);
		} else if (getMessageString("start", lang).equals(text)) {
			requestsSender.sendMessage(this, chatId, messageSource.getMessage("greetings", null, Locale.forLanguageTag(lang)));
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

	private void becameMaster(TelegramBotApiRequestsSender requestsSender, int chatId, String lang) throws Exception {
		isMasterModeOn = true;

		JSONArray tableRows = new JSONArray();
		JSONArray rowOfButtons = new JSONArray();
		tableRows.put(rowOfButtons);

		JSONObject addSongButton = new JSONObject();
		addSongButton.put("text", getMessageString("add_song", lang));
		rowOfButtons.put(addSongButton);
		JSONObject forgetMeButton = new JSONObject();
		forgetMeButton.put("text", getMessageString("forget_me", lang));
		rowOfButtons.put(forgetMeButton);

		JSONObject replyKeyboard = new JSONObject();
		replyKeyboard.put("keyboard", tableRows);
		replyKeyboard.put("one_time_keyboard", true);
		replyKeyboard.put("resize_keyboard", true);
		requestsSender.sendMessage(this, chatId, getMessageString("master_greetings", lang),
				replyKeyboard);
	}

	private void readyToAddSong(TelegramBotApiRequestsSender requestsSender, int chatId, String lang) throws Exception {
		isAddSongOn = true;
		requestsSender.sendMessage(this, chatId, getMessageString("writing_song", lang));
	}

	private void processForgetMe(TelegramBotApiRequestsSender requestsSender, int chatId, String lang) throws Exception {
		isMasterModeOn = false;
		isAddSongOn = false;
		JSONObject json = new JSONObject();
		json.put("remove_keyboard", true);
		requestsSender.sendMessage(this, chatId, getMessageString("greeting", lang), json);
	}

	private void sendNotImplemented(TelegramBotApiRequestsSender requestsSender, JSONObject update) throws Exception {
		int chatId = update.getJSONObject("message").getJSONObject("chat").getInt("id");
		requestsSender.sendMessage(this, chatId, "Not implemented yet!");
	}


	/**
	 * ???
	 * @param responseService - ???????
	 * @param update текст сообщения в формате json полученный с телеграмм
	 * @return возвращает - ???????
	 */
	protected void processGroupMessage(ResponseService responseService, JSONObject update) {
		JSONObject message = update.getJSONObject("message");
		LOGGER.debug("update" + update);

		long date = message.getLong("date");
		if (System.currentTimeMillis() - Utils._1_MINUTE > date * Utils.MILLIS_MULTIPLIER) {
			LOGGER.debug("Skipping message as it's too old");
			return;
		}

		String text = message.optString("text", "");
		LOGGER.debug("text" + text);

		String response = null;
		try {
			response = responseService.getResponse(text);
			LOGGER.debug("text ->response " + response);

		} catch (Exception e) {
			LOGGER.error("Unable to find suitable response", e);
		}
		if (response != null && !response.isEmpty()) {
			/** получаем структуру данных по чату */
			JSONObject chat = message.getJSONObject("chat");
			/** получаем идентификатор чата */
			int chatId = chat.getInt("id");
			/** получаем принадлежность чата */
			String type = chat.getString("type");
			/**
			 * создаем новый объект чата
			 * создаем
			 *
			 * ????????????????????????????
			 * */
			answersForChats.put(new Chat(chatId, type), new Pair<>(date, response));
		}
	}
}
