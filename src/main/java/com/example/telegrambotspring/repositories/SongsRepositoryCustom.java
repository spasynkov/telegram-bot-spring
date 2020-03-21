package com.example.telegrambotspring.repositories;

import com.example.telegrambotspring.entities.SongCouplet;

public interface SongsRepositoryCustom<T extends SongCouplet> {
	T updateOrInsert(T object);
}
