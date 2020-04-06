package com.example.telegrambotspring.repositories;

import com.example.telegrambotspring.entities.SongVerse;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;


/**
 * The interface уровня (слоя) Repository
 * реализует лонику работы непосредсвенно c БД
 * наследуется от MongoRepository, имплементирует все его методы
 * и добавляет свои
 * @author  Stas Pasynkov
 * @see     com.example.telegrambotspring.repositories.SongsRepositoryCustom
 * @see     com.example.telegrambotspring.repositories.SongsRepositoryCustomImpl
 * @version 1.0.1
 */
public interface SongsRepository extends MongoRepository<SongVerse, String>, SongsRepositoryCustom<SongVerse> {

	/**
	 * Метод чтения всех песен данного испольнителя через обращение к репозиторию
	 * @param artist имя исполнителя
	 * @return возвращает коллекцию всех песен данного испольнителя преобразованную в joson-массив
	 */
	List<SongVerse> findAllByArtist(String artist);

	List<SongVerse> findAllByArtistAndSong(String artist, String song);

}
