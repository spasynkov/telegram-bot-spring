package com.example.telegrambotspring.services;

import com.example.telegrambotspring.entities.SongVerse;
import com.example.telegrambotspring.repositories.SongsRepository;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.Callable;


/**
 * The class уровня (слоя) Business Service - слоя бизнес-логики
 * содержат бизнес-логику и вызывают методы на уровне хранилища
 * класс реализует лонику работы c БД
 * <b>repository</b>
 * @author  Stas Pasynkov
 * @see     com.example.telegrambotspring.services
 * @version 1.0.1
 */
@Service
public class DatabaseService {
	private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseService.class);

	private SongsRepository repository;

	@Autowired
	public DatabaseService(SongsRepository repository) {
		this.repository = repository;
	}

	/**
	 * Метод чтения полного списка песен через обращение к репозиторию
	 * @return возвращает коллекцию списка всех песен
	 */
	public JSONArray getAll() {
		LOGGER.debug("MYLOGGER : DatabaseService - > getAll");

		List<SongVerse> list = repository.findAll();
		return collectToJsonArray(list);
	}

	public JSONArray getAllByArtist(String artist) {
		LOGGER.debug("MYLOGGER : DatabaseService - > getAllByArtist");

		List<SongVerse> list = repository.findAllByArtist(artist);
		return collectToJsonArray(list);
	}

	public JSONArray getAllByArtistAndSong(String artist, String song) {
		LOGGER.debug("MYLOGGER : DatabaseService - > getAllByArtistAndSong");

		List<SongVerse> list = repository.findAllByArtistAndSong(artist, song);
		return collectToJsonArray(list);
	}

	private JSONArray collectToJsonArray(List<SongVerse> list) {
		LOGGER.debug("MYLOGGER : DatabaseService - > collectToJsonArray");

		return new JSONArray(list);
		/*return list.parallelStream()
				.sorted()
				.map(JSONObject::new)
				.collect(Collector.of(JSONArray::new,
						JSONArray::put,
						JSONArray::put,
						Collector.Characteristics.CONCURRENT));*/
	}

	public JSONObject addSong(SongVerse verse, String artist, String song) {
		LOGGER.debug("MYLOGGER : DatabaseService - > addSong");

		return safeCall(() -> {
			validateData(verse, artist, song);
			SongVerse result = repository.insert(verse);
			return new JSONObject(result);
		});
	}

	public JSONObject editSong(SongVerse verse, String artist, String song) {
		LOGGER.debug("MYLOGGER : DatabaseService - > editSong");

		return safeCall(() -> {
			validateData(verse, artist, song);
			SongVerse result = repository.updateOrInsert(verse);
			return new JSONObject(result);
		});
	}

	public JSONObject deleteSong(SongVerse verse, String artist, String song) {
		LOGGER.debug("MYLOGGER : DatabaseService - > deleteSong");

		return safeCall(() -> {
			validateData(verse, artist, song);
			repository.delete(verse);
			return new JSONObject();
		});
	}

	private JSONObject safeCall(Callable<JSONObject> lambda) {
		LOGGER.debug("MYLOGGER : DatabaseService - > deleteSong");

		try {
			return lambda.call();
		} catch (Exception e) {
			LOGGER.error("Exception occurred while processing callable function", e);

			JSONObject resp = new JSONObject();
			resp.put("error", e.getLocalizedMessage());
			return resp;
		}
	}

	private void validateData(SongVerse verse, String artist, String song) throws Exception {
		LOGGER.debug("MYLOGGER : DatabaseService - > deleteSong");

		if (!artist.equals(verse.getArtist())) {
			throw new Exception("Incorrect request data: content artist not equals request artist");
		}

		if (!song.equals(verse.getSong())) {
			throw new Exception("Incorrect request data: content song not equals request song");
		}
	}
}
