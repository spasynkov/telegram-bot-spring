package com.example.telegrambotspring.repositories;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.telegrambotspring.entities.SongCouplet;

public interface SongsRepository extends MongoRepository<SongCouplet, String>, SongsRepositoryCustom<SongCouplet> {
	List<SongCouplet> findAllByArtist(String artist);

	List<SongCouplet> findAllByArtistAndSong(String artist, String song);
}
