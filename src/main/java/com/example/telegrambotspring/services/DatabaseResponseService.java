package com.example.telegrambotspring.services;

import com.example.telegrambotspring.repositories.SongsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@Primary
public class DatabaseResponseService extends AbstractResponseService implements ResponseService {
	private SongsRepository repository;

	@Autowired
	public DatabaseResponseService(SongsRepository repository) {
		this.repository = repository;
	}

	@Override
	public void updateSongVerses(String artist) throws Exception {
		sets = artist == null
				? repository.findAll()
				: repository.findAllByArtist(artist);
	}
}
