package com.example.telegrambotspring.utils;

import java.util.Objects;

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

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Pair<?, ?> pair = (Pair<?, ?>) o;
		return Objects.equals(first, pair.first) &&
				Objects.equals(second, pair.second);
	}

	@Override
	public int hashCode() {
		return Objects.hash(first, second);
	}

	@Override
	public String toString() {
		return String.format("Pair{first=%s, second=%s}", first, second);
	}
}