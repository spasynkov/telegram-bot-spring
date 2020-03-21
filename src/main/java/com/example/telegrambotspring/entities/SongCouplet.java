package com.example.telegrambotspring.entities;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.springframework.data.annotation.Id;

public class SongCouplet {
	@Id
	private String id;

	private String artist;
	private String songName;
	private int coupletId;
	private List<String> text;

	public SongCouplet() {
	}

	public SongCouplet(String artist, String songName, int coupletId, List<String> text) {
		this.artist = artist;
		this.songName = songName;
		this.coupletId = coupletId;
		this.text = text;
	}

	public SongCouplet(String artist, String songName, int coupletId, String... text) {
		this(artist, songName, coupletId, Arrays.asList(text));
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

	public String getSongName() {
		return songName;
	}

	public void setSongName(String songName) {
		this.songName = songName;
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
				Objects.equals(songName, that.songName) &&
				Objects.equals(text, that.text);
	}

	@Override
	public int hashCode() {
		return Objects.hash(artist, songName, coupletId, text);
	}

	@Override
	public String toString() {
		return String.format("SongCouplet{id='%s', artist='%s', songName='%s', coupletId=%d, text=%s}",
				id, artist, songName, coupletId, text);
	}
}
