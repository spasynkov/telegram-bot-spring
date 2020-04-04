package com.example.telegrambotspring.entities;

import org.springframework.data.annotation.Id;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;


/**
 * The class описывает структуру формата хранения данных песни
 * <b>id</b> and <b>artist</b> and <b>song</b> and <b>verseId</b> and <b>text</b>
 * @author  Stas Pasynkov
 *
 * @see     com.example.telegrambotspring.entities.Chat
 * @version 1.0.1
 */
public class SongVerse implements Comparable<SongVerse> {

	/** идентификатор песни */
	@Id
	private String id;

	/** имя исполнителя */
	private String artist;
	/** название песни */
	private String song;
	/** номер версии песни */
	private int verseId;
	/** текс песни */
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

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getArtist() {
		return artist;
	}

	public void setArtist(String artist) {
		this.artist = artist;
	}

	public String getSong() {
		return song;
	}

	public void setSong(String song) {
		this.song = song;
	}

	public int getVerseId() {
		return verseId;
	}

	public void setVerseId(int verseId) {
		this.verseId = verseId;
	}

	public List<String> getText() {
		return text;
	}

	public void setText(List<String> text) {
		this.text = text;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		SongVerse that = (SongVerse) o;
		return verseId == that.verseId &&
				Objects.equals(artist, that.artist) &&
				Objects.equals(song, that.song) &&
				Objects.equals(text, that.text);
	}

	@Override
	public int hashCode() {
		return Objects.hash(artist, song, verseId, text);
	}

	@Override
	public String toString() {
		return String.format("SongVerse{id='%s', artist='%s', song='%s', verseId=%d, text=%s}",
				id, artist, song, verseId, text);
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
