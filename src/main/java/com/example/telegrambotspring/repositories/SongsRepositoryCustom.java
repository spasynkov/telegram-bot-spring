package com.example.telegrambotspring.repositories;

import com.example.telegrambotspring.entities.SongVerse;

public interface SongsRepositoryCustom<T extends SongVerse> {
	T updateOrInsert(T object);
}
