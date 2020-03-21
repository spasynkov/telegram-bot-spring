package com.example.telegrambotspring.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.telegrambotspring.entities.SongCouplet;

public interface SongsRepository extends MongoRepository<SongCouplet, String>, SongsRepositoryCustom<SongCouplet> {
}
