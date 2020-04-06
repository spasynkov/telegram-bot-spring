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
 * класс реализует лонику работы c БД со свойствами
 * <b>repository</b>
 * @author  Stas Pasynkov
 * @see     com.example.telegrambotspring.services.DatabaseResponseService
 * @version 1.0.1
 */
@Service
public class DatabaseService {
	/** переменная для записи логов  */
	private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseService.class);

	/** объект класса репоизиторий - класс реализующий логику работы с БД */
	private SongsRepository repository;

	/**
	 * Конструктор - создание нового объекта с определенными значениями
	 * @param repository объект класса репоизиторий
	 */
	@Autowired
	public DatabaseService(SongsRepository repository) {
		this.repository = repository;
	}

	/**
	 * Метод чтения всех песен через обращение к репозиторию
	 * @return возвращает коллекцию списка всех песен преобразованную в joson-массив
	 */
	public JSONArray getAll() {
		List<SongVerse> list = repository.findAll();
		return collectToJsonArray(list);
	}

	/**
	 * Метод чтения всех песен данного испольнителя через обращение к репозиторию
	 * @return возвращает коллекцию всех песен данного испольнителя преобразованную в joson-массив
	 */
	public JSONArray getAllByArtist(String artist) {
		List<SongVerse> list = repository.findAllByArtist(artist);

		return collectToJsonArray(list);
	}

	/**
	 * Метод чтения всех версий песен данного испольнителя и названия песни - через обращение к репозиторию
	 * @return возвращает коллекцию всех версий песен данного испольнителя и названия песни преобразованную в joson-массив
	 */
	public JSONArray getAllByArtistAndSong(String artist, String song) {
		List<SongVerse> list = repository.findAllByArtistAndSong(artist, song);
		return collectToJsonArray(list);
	}

	/**
	 * Метод преобразует полученный список в joson-массив
	 * @return возвращает полученный список преобразованный в joson-массив
	 */
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

	/**
	 * Метод добавления новой песни в БД
	 * вызывается в отдельном потоке
	 * @param verse объект типа SongVerse со всеми атрибутами
	 * @param artist имя исполнителя
	 * @param song название песни
	 * @return возвращает объект типа SongVerse
	 * текст песни и все ее атрибуды с присвоенным Id из БД одной строкой в виде json объекта
	 */
	public JSONObject addSong(SongVerse verse, String artist, String song) {
		/**
		 * Вызов метода в тдельном потоке используя лямбда выражения
		 */
		return safeCall(() -> {
			validateData(verse, artist, song);
			SongVerse result = repository.insert(verse);
			return new JSONObject(result);
		});
	}

	/**
	 * Метод изменения текущей песни - через обращение к репозиторию
	 * @return возвращает объект типа SongVerse
	 * текст песни и все ее атрибуды из БД одной строкой в виде json объекта
	 */
	public JSONObject editSong(SongVerse verse, String artist, String song) {
		/**
		 * Вызов метода в тдельном потоке используя лямбда выражения
		 */
		return safeCall(() -> {
			validateData(verse, artist, song);
			SongVerse result = repository.updateOrInsert(verse);
			return new JSONObject(result);
		});
	}

	/**
	 * Метод удаления всех версий песен данного испольнителя и названия песни - через обращение к репозиторию
	 * @return возвращает пустой объект типа SongVerse в виде json объекта
	 */
	public JSONObject deleteSong(SongVerse verse, String artist, String song) {
		/**
		 * Вызов метода в тдельном потоке используя лямбда выражения
		 */
		return safeCall(() -> {
			validateData(verse, artist, song);
			repository.delete(verse);
			return new JSONObject();
		});
	}

	/**
	 * безопасный вызов метода с проверкой всех исключений
	 * @return возвращает полученный список преобразованный в joson
	 */
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

	/**
	 * Метод выполняет проверку, что имя исполнителя и название песни полученные в запросе совпадают
	 * с именем исполнителя и названием песни находямися в объекте новой песни
	 * @param verse объект типа SongVerse со всеми атрибутами
	 * @param artist имя исполнителя
	 * @param song название песни
	 * @return возвращает объект типа SongVerse -
	 * текст песни и все ее атрибуты с присвоенным Id из БД одной строкой
	 */
	private void validateData(SongVerse verse, String artist, String song) throws Exception {
		if (!artist.equals(verse.getArtist())) {
			throw new Exception("Incorrect request data: content artist not equals request artist");
		}

		if (!song.equals(verse.getSong())) {
			throw new Exception("Incorrect request data: content song not equals request song");
		}
	}
}
