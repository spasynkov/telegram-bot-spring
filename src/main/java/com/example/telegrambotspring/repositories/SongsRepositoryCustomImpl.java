package com.example.telegrambotspring.repositories;

import com.example.telegrambotspring.controllers.RestController;
import com.example.telegrambotspring.entities.SongVerse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

/**
 * The class уровня (слоя) Repository
 * предназначен для создания собстенных методов,
 * которым спринг не сможет автоматом создать реализацию по названию
 * и которые будут  добавлены в репозитория
 * <b>mongoTemplate</b>
 * @author  Stas Pasynkov
 * @see     com.example.telegrambotspring.repositories.SongsRepositoryCustom
 * @version 1.0
 */
@Repository
public class SongsRepositoryCustomImpl<T extends SongVerse> implements SongsRepositoryCustom<T> {
	/**
	 * переменная типа MongoTemplate для доступа к репозиторию
	 */
	private final MongoTemplate mongoTemplate;
    private static final Logger MYLOGGER = LoggerFactory.getLogger(SongsRepositoryCustomImpl.class);

	/**
	 * Конструктор - создание нового объекта с определенными значениями
	 * @param mongoTemplate переменная доступа к репозиторию
	 */
    @Autowired
	public SongsRepositoryCustomImpl(MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}

	/**
	 * Реализация метода объявленного в интерфейсе SongsRepositoryCustom
	 * @param object переменная содежращая все параметры отредактированной песни
	 * @return возвращает объект песню (в виде строки) куда будет происходить внесение изменений
	 * полученных в переменной object
	 */
	@Override
	public T updateOrInsert(T object) {
        Query query = new Query();
		query.addCriteria(Criteria.where("artist").is(object.getArtist()));
		query.addCriteria(Criteria.where("song").is(object.getSong()));
		query.addCriteria(Criteria.where("verseId").is(object.getVerseId()));

		Update update = new Update();
		update.set("text", object.getText());

		@SuppressWarnings("unchecked")  // TODO: fix
				T result = (T) mongoTemplate.findAndModify(query, update, object.getClass());

        if (result == null) {
			result = mongoTemplate.insert(object);
		}

		return result;
	}

}
