package com.example.telegrambotspring.entities;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.Id;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Slf4j
@Getter
@Setter
@ToString
@EqualsAndHashCode(exclude = { "id" })
public class SongVerse implements Comparable<SongVerse> {
	@Id
	private String id;

	private String artist;
	private String song;
	private int verseId;
	private List<String> text;

	public SongVerse() {
	}

	public SongVerse(String artist, String song, int verseId, List<String> text) {
		this.artist = artist;
		this.song = song;
		this.verseId = verseId;
		this.text = text;
	}

	public SongVerse(String artist, String song, int verseId, String... text) {
		this(artist, song, verseId, Arrays.asList(text));
	}

	@Override
	public int compareTo(SongVerse that) {
		int artistComparision = this.artist.compareTo(that.artist);
		if (artistComparision != 0) return artistComparision;

		int songComparision = this.song.compareTo(that.song);
		if (songComparision != 0) return songComparision;

		return Integer.compare(this.verseId, that.verseId);
	}
}
