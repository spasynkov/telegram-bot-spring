package com.example.telegrambotspring.entities;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.springframework.data.annotation.Id;

public class SongCouplet implements Comparable<SongCouplet> {
	@Id
	private String id;

	private String artist;
	private String song;
	private int coupletId;
	private List<String> text;

	public SongCouplet() {
	}

	public SongCouplet(String artist, String song, int coupletId, List<String> text) {
		this.artist = artist;
		this.song = song;
		this.coupletId = coupletId;
		this.text = text;
	}

	public SongCouplet(String artist, String song, int coupletId, String... text) {
		this(artist, song, coupletId, Arrays.asList(text));
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

	public int getCoupletId() {
		return coupletId;
	}

	public void setCoupletId(int coupletId) {
		this.coupletId = coupletId;
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
		SongCouplet that = (SongCouplet) o;
		return coupletId == that.coupletId &&
				Objects.equals(artist, that.artist) &&
				Objects.equals(song, that.song) &&
				Objects.equals(text, that.text);
	}

	@Override
	public int hashCode() {
		return Objects.hash(artist, song, coupletId, text);
	}

	@Override
	public String toString() {
		return String.format("SongCouplet{id='%s', artist='%s', song='%s', coupletId=%d, text=%s}",
				id, artist, song, coupletId, text);
	}

	@Override
	public int compareTo(SongCouplet that) {
		int artistComparision = this.artist.compareTo(that.artist);
		if (artistComparision != 0) return artistComparision;

		int songComparision = this.song.compareTo(that.song);
		if (songComparision != 0) return songComparision;

		return Integer.compare(this.coupletId, that.coupletId);
	}
}
