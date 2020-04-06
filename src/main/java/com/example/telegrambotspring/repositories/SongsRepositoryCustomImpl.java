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
 * предназначен для создания методов
 * <b>mongoTemplate</b>
 * @author  Stas Pasynkov
 * @see     com.example.telegrambotspring.repositories.SongsRepositoryCustom
 * @version 1.0
 */
@Repository
public class SongsRepositoryCustomImpl<T extends SongVerse> implements SongsRepositoryCustom<T> {
	private final MongoTemplate mongoTemplate;
    private static final Logger MYLOGGER = LoggerFactory.getLogger(SongsRepositoryCustomImpl.class);


    @Autowired
	public SongsRepositoryCustomImpl(MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}



	@Override
	public T updateOrInsert(T object) {
        MYLOGGER.debug("MYLOGGER : SongsRepositoryCustomImpl -> updateOrInsert");
        MYLOGGER.debug("MYLOGGER : SongsRepositoryCustomImpl -> updateOrInsert" + object.toString());

        Query query = new Query();
		query.addCriteria(Criteria.where("artist").is(object.getArtist()));
		query.addCriteria(Criteria.where("song").is(object.getSong()));
		query.addCriteria(Criteria.where("verseId").is(object.getVerseId()));

		Update update = new Update();
		update.set("text", object.getText());

		@SuppressWarnings("unchecked")  // TODO: fix
				T result = (T) mongoTemplate.findAndModify(query, update, object.getClass());
        MYLOGGER.debug("MYLOGGER : SongsRepositoryCustomImpl -> updateOrInsert" + result.toString());


        if (result == null) {
			result = mongoTemplate.insert(object);
		}

		return result;
	}

}
