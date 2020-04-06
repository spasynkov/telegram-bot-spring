package com.example.telegrambotspring.repositories;

import com.example.telegrambotspring.entities.SongVerse;

/**
 * The interface уровня (слоя) Repository
 * предназначен для объявления новых собстенных методов,
 * которым спринг не сможет автоматом создать реализацию по названию
 * и которые будут  добавлены в репозиторий
 * @author  Stas Pasynkov
 * @see     com.example.telegrambotspring.repositories.SongsRepositoryCustomImpl
 * @version 1.0
 */
public interface SongsRepositoryCustom<T extends SongVerse> {
	T updateOrInsert(T object);
}
