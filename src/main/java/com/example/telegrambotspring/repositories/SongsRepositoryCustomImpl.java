package com.example.telegrambotspring.repositories;

import com.example.telegrambotspring.entities.SongVerse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

/**
 * The class уровня (слоя) Repository
 * компонент, который предназначен для хранения, извлечения и поиска. Как правило, используется для работы с базами данных.
 * <b>webhooksService</b>
 * @author  Stas Pasynkov
 * @see     com.example.telegrambotspring.controllers.TelegramApiListenerController
 * @version 1.0
 */
@Repository
public class SongsRepositoryCustomImpl<T extends SongVerse> implements SongsRepositoryCustom<T> {
	private final MongoTemplate mongoTemplate;

	@Autowired
	public SongsRepositoryCustomImpl(MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}

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
