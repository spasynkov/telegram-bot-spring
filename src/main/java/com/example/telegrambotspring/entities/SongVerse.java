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


/**
 * The class описывает структуру формата хранения данных песни
 * в этой структуре хранится только один куплет песни со свойствами
 * <b>id</b> and <b>artist</b> and <b>song</b> and <b>verseId</b> and <b>text</b>
 * @author  Stas Pasynkov
 *
 * @see     com.example.telegrambotspring.entities.Chat
 * @version 1.0.1
 */
@Slf4j
@Getter
@Setter
@ToString
@EqualsAndHashCode(exclude = { "id" })
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
