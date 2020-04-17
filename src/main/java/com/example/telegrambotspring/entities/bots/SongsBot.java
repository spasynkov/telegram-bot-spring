package com.example.telegrambotspring.entities.bots;

import com.example.telegrambotspring.entities.Chat;
import com.example.telegrambotspring.services.ResponseService;
import com.example.telegrambotspring.services.TelegramBotApiRequestsSender;
import com.example.telegrambotspring.utils.Pair;
import com.example.telegrambotspring.utils.Utils;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;


/**
 * The class ??????
 * ????????????????
 * со свойствами
 * <b>isMasterModeOn</b> and <b>isAddSongOn</b>
 * наследник класса:
 * @see     com.example.telegrambotspring.entities.bots.AbstractTelegramBot
 * @version 1.0.1
 */
@Slf4j
@ToString
@EqualsAndHashCode
public class SongsBot extends AbstractTelegramBot {
//	private static final Logger LOGGER = LoggerFactory.getLogger(SongsBot.class);

	private boolean isMasterModeOn = false;
	private boolean isAddSongOn = false;

	public SongsBot(String token, Map<Chat, Pair<Long, String>> answersForChats, UpdatesStrategy strategy) {
		super(token, answersForChats, strategy);
	}

	/**
	 * переопределяем метод equals
	 * @return возвращает boolean результат сравнения чатов по следующим полям
	 * isMasterModeOn, isAddSongOn
	 *
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		SongsBot bot = (SongsBot) o;
		return super.equals(o) &&
				isMasterModeOn == bot.isMasterModeOn &&
				isAddSongOn == bot.isAddSongOn;
	}

	/**
	 * переопределяем метод hashCode
	 * @return возвращает hashCode Objects
	 *
	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), isMasterModeOn, isAddSongOn);
	}

	/**
	 * переопределяем метод toString
	 * @return возвращает отформатированную строку
	 *
	@Override
	public String toString() {
		String string = super.toString();
		return "SongsBot{" +
				string.substring(string.indexOf("{") + 1, string.lastIndexOf("}")) +
				(isMasterModeOn ? ", isMasterModeOn=true" : "") +
				(isAddSongOn ? ", isAddSongOn=true" : "") +
				'}';
	}
	 */

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
	 * Метод обработки групповых сообщений
	 * @param responseService - сервис формирования ответов
	 * @param update текст сообщения в формате json полученный с телеграмм - содержимое поля message
	 */
	protected void processGroupMessage(ResponseService responseService, JSONObject update) {

		/**  прочитаем текст сообщения */
		JSONObject message = update.getJSONObject("message");
		log.debug("update" + update);
		log.debug("message" + message);

		/**  время формирования сообщения */
		long date = message.getLong("date");

		/**  проверка актуальности сообщения - т.е. сообщение сформировано не более 1мин назад */
		if (System.currentTimeMillis() - Utils._1_MINUTE > date * Utils.MILLIS_MULTIPLIER) {
			log.debug("Skipping message as it's too old");
			return;
		}

		/**  текст сообщения */
		String text = message.optString("text", "");
		log.debug("text " + text);

		/**  текст ответа */
		String response = null;
		try {
			/**  формирование ответа */
			response = responseService.getResponse(text);
			log.debug("text ->response " + response);

		} catch (Exception e) {
			log.error("Unable to find suitable response", e);
		}
		if (response != null && !response.isEmpty()) {
			/** получаем структуру данных по чату */
			JSONObject chat = message.getJSONObject("chat");
			/** получаем идентификатор чата */
			int chatId = chat.getInt("id");
			/** получаем принадлежность чата */
			String type = chat.getString("type");
			log.debug("new Pair<>(date, response)" + new Pair<>(date, response));

			/**
			 * создаем новый объект чата
			 * создаем новый объект содержащий время запроса и тескт ответа
			 * сохраняем в мапе новую запись содержащию объект чата и текст ответа
			 * */
			answersForChats.put(new Chat(chatId, type), new Pair<>(date, response));
		}
	}
}
