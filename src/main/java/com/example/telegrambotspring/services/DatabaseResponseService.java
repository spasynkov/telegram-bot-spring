package com.example.telegrambotspring.services;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import com.example.telegrambotspring.entities.ResponseSet;
import com.example.telegrambotspring.entities.SongCouplet;
import com.example.telegrambotspring.repositories.SongsRepository;

@Service
@Primary
public class DatabaseResponseService extends AbstractResponseService implements ResponseService {
	private SongsRepository repository;

	@Autowired
	public DatabaseResponseService(SongsRepository repository) {
		this.repository = repository;
	}

	@Override
	public void updateResponseSets() throws Exception {
		sets = repository.findAll()
				.parallelStream()
				.filter(x -> "Гражданин Топинамбур".equals(x.getArtist()))
				.map(SongCouplet::getText)  // Couplet -> List<String>
				.map(ResponseSet::new)      // List<String> -> ResponseSet
				.collect(Collectors.toList());
	}
}
