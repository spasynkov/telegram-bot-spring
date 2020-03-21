package com.example.telegrambotspring.repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.example.telegrambotspring.entities.SongCouplet;

@Repository
public class SongsRepositoryCustomImpl<T extends SongCouplet> implements SongsRepositoryCustom<T> {
	private final MongoTemplate mongoTemplate;

	@Autowired
	public SongsRepositoryCustomImpl(MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}

	@Override
	public T updateOrInsert(T object) {
		Query query = new Query();
		query.addCriteria(Criteria.where("artist").is(object.getArtist()));
		query.addCriteria(Criteria.where("songName").is(object.getSongName()));
		query.addCriteria(Criteria.where("coupletId").is(object.getCoupletId()));

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
