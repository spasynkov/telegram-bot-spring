package com.example.telegrambotspring.entities;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ResponseSet {
	private List<String> strings;

	public ResponseSet(List<String> strings) {
		this.strings = strings;
	}

	public ResponseSet(String... strings) {
		this.strings = Arrays.asList(strings);
	}

	public List<String> getStrings() {
		return strings;
	}

	public void setStrings(List<String> strings) {
		this.strings = strings;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ResponseSet that = (ResponseSet) o;
		return Objects.equals(strings, that.strings);
	}

	@Override
	public int hashCode() {
		return Objects.hash(strings);
	}

	@Override
	public String toString() {
		return String.format("ResponseSet{strings=%s}", strings);
	}
}
