package com.example.telegrambotspring.entities.bots;

import com.example.telegrambotspring.entities.Message;
import com.example.telegrambotspring.entities.Received;
import com.example.telegrambotspring.services.ResponseService;
import com.example.telegrambotspring.services.TelegramBotApiRequestsSender;
import com.example.telegrambotspring.utils.Utils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class SongsBot extends AbstractTelegramBot {
	private static final Logger LOGGER = LoggerFactory.getLogger(SongsBot.class);

	private boolean isMasterModeOn = false;
	private boolean isAddSongOn = false;

	public SongsBot(String token, Received received, UpdatesStrategy strategy) {
		super(token, received, strategy);
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
	protected void processDirectMessage(TelegramBotApiRequestsSender requestsSender, Message update) throws Exception {

		String lang = update.getLanguageCode();
		String text = update.getText();
		int chatId = update.getChatId();

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

	private void sendNotImplemented(TelegramBotApiRequestsSender requestsSender, Message update) throws Exception {
		int chatId = update.getChatId();
		requestsSender.sendMessage(this, chatId, "Not implemented yet!");
	}

	protected void processGroupMessage(TelegramBotApiRequestsSender requestsSender, ResponseService responseService, Message message) {
		long time = message.getTime();
		if (System.currentTimeMillis() - Utils._1_MINUTE > time * Utils.MILLIS_MULTIPLIER) {
			LOGGER.debug("Skipping message as it's too old");
			return;
		}

		String text = message.getText();
		try {
			String response = responseService.getResponse(text);
			requestsSender.sendMessage(this, message.getChatId(), response);
		} catch (Exception e) {
			LOGGER.error("Unable to find suitable response", e);
		}
	}
}

