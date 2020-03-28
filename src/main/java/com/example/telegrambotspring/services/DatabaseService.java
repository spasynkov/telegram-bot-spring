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

@Service
public class DatabaseService {
	private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseService.class);

	private SongsRepository repository;

	@Autowired
	public DatabaseService(SongsRepository repository) {
		this.repository = repository;
	}

	public JSONArray getAll() {
		List<SongVerse> list = repository.findAll();
		return collectToJsonArray(list);
	}

	public JSONArray getAllByArtist(String artist) {
		List<SongVerse> list = repository.findAllByArtist(artist);
		return collectToJsonArray(list);
	}

	public JSONArray getAllByArtistAndSong(String artist, String song) {
		List<SongVerse> list = repository.findAllByArtistAndSong(artist, song);
		return collectToJsonArray(list);
	}

	private JSONArray collectToJsonArray(List<SongVerse> list) {
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
		return safeCall(() -> {
            validateData(verse, artist, song);
            SongVerse result = repository.insert(verse);
			return new JSONObject(result);
		});
	}

    public JSONObject editSong(SongVerse verse, String artist, String song) {
		return safeCall(() -> {
            validateData(verse, artist, song);
            SongVerse result = repository.updateOrInsert(verse);
			return new JSONObject(result);
		});
	}

    public JSONObject deleteSong(SongVerse verse, String artist, String song) {
		return safeCall(() -> {
            validateData(verse, artist, song);
            repository.delete(verse);
			return new JSONObject();
		});
	}

	private JSONObject safeCall(Callable<JSONObject> lambda) {
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
        if (!artist.equals(verse.getArtist())) {
			throw new Exception("Incorrect request data: content artist not equals request artist");
		}

        if (!song.equals(verse.getSong())) {
			throw new Exception("Incorrect request data: content song not equals request song");
		}
	}
}
