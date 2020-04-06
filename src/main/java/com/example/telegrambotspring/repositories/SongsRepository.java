package com.example.telegrambotspring.repositories;

import com.example.telegrambotspring.entities.SongVerse;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * The interface уровня (слоя) Repository
 * реализует логику работы непосредсвенно c БД
 * наследуется от MongoRepository, имплементирует все его методы
 * и тут можно добавлять свои методы в репозиторий, которые спринг распознает по их названию
 * и автоматом сам создаст их реализацию
 * @author  Stas Pasynkov
 * @see     com.example.telegrambotspring.repositories.SongsRepositoryCustom
 * @see     com.example.telegrambotspring.repositories.SongsRepositoryCustomImpl
 * @version 1.0.1
 */
public interface SongsRepository extends MongoRepository<SongVerse, String>, SongsRepositoryCustom<SongVerse> {

	/**
	 * Метод чтения всех песен данного испольнителя непосредсвенно из БД
	 * @param artist имя исполнителя
	 * @return возвращает коллекцию всех песен данного испольнителя
	 */
	List<SongVerse> findAllByArtist(String artist);

	/**
	 * Метод чтения всех версий песен данного испольнителя и названия песни непосредсвенно из БД
	 * @param artist имя исполнителя
	 * @param song название песни
	 * @return возвращает коллекцию всех версий песен данного испольнителя и названия песни
	 */
	List<SongVerse> findAllByArtistAndSong(String artist, String song);

}
