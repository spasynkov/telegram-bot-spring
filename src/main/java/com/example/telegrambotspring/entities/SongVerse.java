package com.example.telegrambotspring.entities;

import org.springframework.data.annotation.Id;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;


/**
 * The class описывает структуру формата хранения данных песни
 * в этой структуре хранится только один куплет песни со свойствами
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
	/** текс песни - тут только одни куплет*/
	private List<String> text;

	/**
	 * Конструктор по умолчанию - создание нового объекта
	 */
	public SongVerse() {
	}

	/**
	 * Конструктор - создание нового объекта с определенными значениями
	 * @param artist - имя исполнителя
	 * @param song - название песни
	 * @param verseId - номер версии песни
	 * @param text - текс песни - куплет - передается в виде списка строк
	 */
	public SongVerse(String artist, String song, int verseId, List<String> text) {
		this.artist = artist;
		this.song = song;
		this.verseId = verseId;
		this.text = text;
	}

	/**
	 * Конструктор - создание нового объекта с определенными значениями
	 * @param artist - имя исполнителя
	 * @param song - название песни
	 * @param verseId - номер версии песни
	 * @param text - текс песни - куплет - передается в виде массива строк
	 */
	public SongVerse(String artist, String song, int verseId, String... text) {
		this(artist, song, verseId, Arrays.asList(text));
	}

	/**
	 * метод - чтение идентификатора песни
	 * @return возвращает строку - идентификатор песни
	 */
	public String getId() {
		return id;
	}

	/**
	 * метод - запись идентификатора песни
	 * @param id - идентификатор песни
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * метод - чтение имени исполнителя
	 * @return возвращает строку - имя исполнителя
	 */
	public String getArtist() {
		return artist;
	}

	/**
	 * метод - запись имени исполнителя
	 * @param artist - имя исполнителя
	 */
	public void setArtist(String artist) {
		this.artist = artist;
	}

	/**
	 * метод - чтение названия песни
	 * @return возвращает строку - название песни
	 */
	public String getSong() {
		return song;
	}

	/**
	 * метод - запись названия песни
	 * @param song - название песни
	 */
	public void setSong(String song) {
		this.song = song;
	}


	/**
	 * метод - чтение номера версии песни
	 * @return возвращает целое число - номер версии песни
	 */
	public int getVerseId() {
		return verseId;
	}

	/**
	 * метод - запись номера версии песни
	 * @param verseId - номер версии песни
	 */
	public void setVerseId(int verseId) {
		this.verseId = verseId;
	}


	/**
	 * метод - чтение текста песни - одного куплета
	 * @return возвращает список строк - текс песни - один куплет
	 */
	public List<String> getText() {
		return text;
	}

	/**
	 * метод - запись текста песни - одного куплета
	 * @param text - текс песни
	 */
	public void setText(List<String> text) {
		this.text = text;
	}


	/**
	 * переопределяем метод equals
	 * @return возвращает boolean результат сравнения песен по следующим полям
	 * verseId, artist, song, text
	 */
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


	/**
	 * переопределяем метод hashCode
	 * @return возвращает hashCode Objects
	 */
	@Override
	public int hashCode() {
		return Objects.hash(artist, song, verseId, text);
	}

	/**
	 * переопределяем метод toString
	 * @return возвращает отформатированную строку
	 */
	@Override
	public String toString() {
		return String.format("SongVerse{id='%s', artist='%s', song='%s', verseId=%d, text=%s}",
				id, artist, song, verseId, text);
	}

	/**
	 * переопределяем метод compareTo
	 * @return возвращает результат сравнения: 0 объекты равны <>0 - не равны
	 * сравнение происходит по полю исполнителя, названию песни и версии
	 */
	@Override
	public int compareTo(SongVerse that) {
		int artistComparision = this.artist.compareTo(that.artist);
		if (artistComparision != 0) return artistComparision;

		int songComparision = this.song.compareTo(that.song);
		if (songComparision != 0) return songComparision;

		return Integer.compare(this.verseId, that.verseId);
	}
}
