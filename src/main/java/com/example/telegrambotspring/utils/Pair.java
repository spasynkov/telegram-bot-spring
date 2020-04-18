package com.example.telegrambotspring.utils;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;


/**
 * The class содержащит данные время запроса и текст ответа
 * со свойствами
 * <b>first</b> and <b>second</b>
 * @version 1.0.1
 */
@Slf4j
@Data
public class Pair<F, S> {
	/** время запроса */
	private F first;
	/** текст ответа */
	private S second;

	/**
	 * Конструктор абстрактного класса - создание нового объекта с определенными значениями
	 * @param first время запроса
	 * @param second текст куплета песни для ответа
	 */
	public Pair(F first, S second) {
		this.first = first;
		this.second = second;
	}
}