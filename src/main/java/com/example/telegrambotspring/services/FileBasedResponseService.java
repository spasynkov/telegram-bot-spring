package com.example.telegrambotspring.services;

import com.example.telegrambotspring.entities.SongVerse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.LinkedList;
import java.util.List;

@Service
@Deprecated
public class FileBasedResponseService extends AbstractResponseService implements ResponseService {
	@Value("${telegram.bot.textsDirectory}")
	private String textsDirectoryName;
	private File file;

	public FileBasedResponseService() {
	}

	@Override
	public void updateSongVerses(String artist) throws Exception {
		try {
			initFile();
		} catch (FileNotFoundException e) {
			throw new Exception("Unable to init file with texts", e);
		}

		if (file.isFile()) {
			SongVerse set = collectDataFromFile(file);
			if (set != null) sets.add(set);
		} else if (file.isDirectory()) {
			File[] files = file.listFiles();
			if (files == null) return;

			for (File file : files) {
				if (file.isDirectory()) continue;
				SongVerse SongVerse = collectDataFromFile(file);
				if (SongVerse != null) sets.add(SongVerse);
			}
		}
	}

	private void initFile() throws FileNotFoundException {
		this.file = new File(textsDirectoryName);
		if (!file.exists()) {
			throw new FileNotFoundException("Unable to find '" + textsDirectoryName + "' at '" + file.toPath() + "'");
		}
	}

	private SongVerse collectDataFromFile(File file) {
		List<String> strings = new LinkedList<>();

		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			while (reader.ready()) {
				strings.add(reader.readLine());
			}
		} catch (Exception e) {
			return null;
		}

		return new SongVerse("", "", 0, strings);
	}
}
