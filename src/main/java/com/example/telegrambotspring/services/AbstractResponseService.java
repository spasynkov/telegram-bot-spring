package com.example.telegrambotspring.services;

import com.example.telegrambotspring.entities.SongVerse;
import com.example.telegrambotspring.entities.bots.SongsBot;
import com.example.telegrambotspring.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;

public abstract class AbstractResponseService implements ResponseService {
	protected List<SongVerse> sets = new LinkedList<>();
	private static final Logger MYLOGGER = LoggerFactory.getLogger(AbstractResponseService.class);


	@Override
	public String getResponse(String... textParts) throws Exception {
		//если список песен пустой, то обновим его из БД
		MYLOGGER.debug("sets" + sets);

		if (sets.isEmpty()) updateSongVerses(null);
		MYLOGGER.debug("udate sets" + sets);


		SongVerse foundSet = null;
		for (SongVerse set : sets) {
			List<String> strings = set.getText();
			for (int i = 0; i < strings.size(); i++) {
				if (Utils.normalizeString(strings.get(i)).equalsIgnoreCase(Utils.normalizeString(textParts[0]))
						&& i + textParts.length < strings.size()) {

					boolean suits = true;
					for (int j = 0; j < textParts.length; j++) {
						if (!Utils.normalizeString(strings.get(i + j)).equalsIgnoreCase(Utils.normalizeString(textParts[j]))) {
							suits = false;
							break;
						}
					}
					if (suits && foundSet != null) throw new Exception("Multiply strings found for: " + textParts[0]);
					foundSet = set;
				}
			}
		}

		if (foundSet == null) throw new Exception("Unable to find suitable strings for: " + textParts[0]);

		List<String> strings = foundSet.getText();
		for (int i = 0; i < strings.size(); i++) {
			if (Utils.normalizeString(strings.get(i)).equalsIgnoreCase(Utils.normalizeString(textParts[0]))
					&& i + textParts.length < strings.size()) {
				boolean suits = true;
				for (int j = 0; j < textParts.length; j++) {
					if (!Utils.normalizeString(strings.get(i + j)).equalsIgnoreCase(Utils.normalizeString(textParts[j]))) {
						suits = false;
						break;
					}
				}
				if (suits && i + textParts.length < strings.size()) {
					return strings.get(i + textParts.length);
				}
			}
		}
		return null;
	}
}
