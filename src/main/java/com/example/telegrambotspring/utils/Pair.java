package com.example.telegrambotspring.utils;

import java.util.Objects;


/**
 * The class
 * со свойствами
 * <b>first</b> and <b>second</b>
 * @version 1.0.1
 */
public class Pair<F, S> {
	private F first;
	private S second;

	public Pair(F first, S second) {
		this.first = first;
		this.second = second;
	}

	public F getFirst() {
		return first;
	}

	public void setFirst(F first) {
		this.first = first;
	}

	public S getSecond() {
		return second;
	}

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