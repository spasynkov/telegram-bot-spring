package com.example.telegrambotspring.utils;

import java.util.Objects;


/**
 * The class содержащит данные время запроса и текст ответа
 * со свойствами
 * <b>first</b> and <b>second</b>
 * @version 1.0.1
 */
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

	/**
	 * метод - чтение времени запроса
	 * @return возвращает время запроса
	 */
	public F getFirst() {
		return first;
	}

	/**
	 * метод - запись времени запроса
	 */
	public void setFirst(F first) {
		this.first = first;
	}

	/**
	 * метод - чтение текста ответа
	 * @return возвращает текст ответа
	 */
	public S getSecond() {
		return second;
	}

	/**
	 * метод - запись текста ответа
	 */
	public void setSecond(S second) {
		this.second = second;
	}

	/**
	 * переопределяем метод equals
	 * @return возвращает boolean результат сравнения чатов по следующим полям
	 * first, second
	 */
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Pair<?, ?> pair = (Pair<?, ?>) o;
		return Objects.equals(first, pair.first) &&
				Objects.equals(second, pair.second);
	}

	/**
	 * переопределяем метод hashCode
	 * @return возвращает hashCode Objects
	 */
	@Override
	public int hashCode() {
		return Objects.hash(first, second);
	}

	/**
	 * переопределяем метод toString
	 * @return возвращает отформатированную строку
	 */
	@Override
	public String toString() {
		return String.format("Pair{first=%s, second=%s}", first, second);
	}
}