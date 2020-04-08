package com.example.telegrambotspring.services;

import com.example.telegrambotspring.repositories.SongsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

/**
 * The class уровня (слоя) Service
 * переопределяем абстрактные методы интерфейса ResponseService
 * используя переменные из абстрактного класса AbstractResponseService
 * со свойствами
 * <b>repository</b>
 * @see     com.example.telegrambotspring.services.AbstractResponseService
 * @see     com.example.telegrambotspring.services.ResponseService
 * @version 1.0.1
 */
@Service
@Primary
public class DatabaseResponseService extends AbstractResponseService implements ResponseService {
	private SongsRepository repository;

	/**
	 * Конструктор абстрактного класса - создание нового объекта с определенными значениями
	 * @param repository инициализация репозитория - работа напрямую с БД
	 */
	@Autowired
	public DatabaseResponseService(SongsRepository repository) {
		this.repository = repository;
	}

	/**
	 * наполнение репозитория данными из БД
	 * @param artist имя артиста
	 */
	@Override
	public void updateSongVerses(String artist) throws Exception {
		sets = artist == null
				? repository.findAll()
				: repository.findAllByArtist(artist);
	}
}
