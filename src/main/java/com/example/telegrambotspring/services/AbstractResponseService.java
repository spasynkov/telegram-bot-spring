package com.example.telegrambotspring.services;

import java.util.LinkedList;
import java.util.List;

import com.example.telegrambotspring.entities.SongCouplet;
import com.example.telegrambotspring.utils.Utils;

public abstract class AbstractResponseService implements ResponseService {
	protected List<SongCouplet> sets = new LinkedList<>();

	@Override
	public String getResponse(String... textParts) throws Exception {
		if (sets.isEmpty()) updateSongCouplets(null);

		SongCouplet foundSet = null;
		for (SongCouplet set : sets) {
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
