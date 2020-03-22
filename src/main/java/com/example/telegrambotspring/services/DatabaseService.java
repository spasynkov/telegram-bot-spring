package com.example.telegrambotspring.services;

import java.util.List;
import java.util.concurrent.Callable;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.telegrambotspring.entities.SongCouplet;
import com.example.telegrambotspring.repositories.SongsRepository;

@Service
public class DatabaseService {
	private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseService.class);

	private SongsRepository repository;

	@Autowired
	public DatabaseService(SongsRepository repository) {
		this.repository = repository;
	}

	public JSONArray getAll() {
		List<SongCouplet> list = repository.findAll();
		return collectToJsonArray(list);
	}

	public JSONArray getAllByArtist(String artist) {
		List<SongCouplet> list = repository.findAllByArtist(artist);
		return collectToJsonArray(list);
	}

	public JSONArray getAllByArtistAndSong(String artist, String song) {
		List<SongCouplet> list = repository.findAllByArtistAndSong(artist, song);
		return collectToJsonArray(list);
	}

	private JSONArray collectToJsonArray(List<SongCouplet> list) {
		return new JSONArray(list);
		/*return list.parallelStream()
				.sorted()
				.map(JSONObject::new)
				.collect(Collector.of(JSONArray::new,
						JSONArray::put,
						JSONArray::put,
						Collector.Characteristics.CONCURRENT));*/
	}

	public JSONObject addSong(SongCouplet couplet, String artist, String song) {
		return safeCall(() -> {
			validateData(couplet, artist, song);
			SongCouplet result = repository.insert(couplet);
			return new JSONObject(result);
		});
	}

	public JSONObject editSong(SongCouplet couplet, String artist, String song) {
		return safeCall(() -> {
			validateData(couplet, artist, song);
			SongCouplet result = repository.updateOrInsert(couplet);
			return new JSONObject(result);
		});
	}

	public JSONObject deleteSong(SongCouplet couplet, String artist, String song) {
		return safeCall(() -> {
			validateData(couplet, artist, song);
			repository.delete(couplet);
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

	private void validateData(SongCouplet couplet, String artist, String song) throws Exception {
		if (!artist.equals(couplet.getArtist())) {
			throw new Exception("Incorrect request data: content artist not equals request artist");
		}

		if (!song.equals(couplet.getSong())) {
			throw new Exception("Incorrect request data: content song not equals request song");
		}
	}
}
