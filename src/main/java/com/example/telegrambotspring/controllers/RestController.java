package com.example.telegrambotspring.controllers;

import com.example.telegrambotspring.entities.SongVerse;
import com.example.telegrambotspring.services.DatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@org.springframework.web.bind.annotation.RestController
@RequestMapping(value = "/rest", produces = "application/json")
public class RestController {
	private DatabaseService service;

	@Autowired
	public RestController(DatabaseService service) {
		this.service = service;
	}

	@GetMapping("/all")
	public String getAll() {
		return service.getAll().toString();
	}

	@GetMapping("/all/artist/{artistName}")
	public String getAllByArtist(@PathVariable String artistName) {
		return service.getAllByArtist(artistName).toString();
	}

	@GetMapping("/all/artist/{artistName}/song/{songName}")
	public String getAllByArtist(@PathVariable String artistName, @PathVariable String songName) {
		return service.getAllByArtistAndSong(artistName, songName).toString();
	}

	@PutMapping("/all/artist/{artistName}/song/{songName}")
	public String addSong(@RequestBody SongVerse verse, @PathVariable String artistName, @PathVariable String songName) {
		return service.addSong(verse, artistName, songName).toString();
	}

	@PostMapping("/all/artist/{artistName}/song/{songName}")
	public String editSong(@RequestBody SongVerse verse, @PathVariable String artistName, @PathVariable String songName) {
		return service.editSong(verse, artistName, songName).toString();
	}

	@DeleteMapping("/all/artist/{artistName}/song/{songName}")
	public String deleteSong(@RequestBody SongVerse verset, @PathVariable String artistName, @PathVariable String songName) {
		return service.deleteSong(verset, artistName, songName).toString();
	}
}
