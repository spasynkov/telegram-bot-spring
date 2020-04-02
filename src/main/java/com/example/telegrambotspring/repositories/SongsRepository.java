package com.example.telegrambotspring.repositories;

import com.example.telegrambotspring.entities.SongVerse;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface SongsRepository extends MongoRepository<SongVerse, String>, SongsRepositoryCustom<SongVerse> {
	List<SongVerse> findAllByArtist(String artist);

	List<SongVerse> findAllByArtistAndSong(String artist, String song);

}
