package com.example.telegrambotspring.utils;

public class Utils {
	public static String normalizeString(String s) {
		return s.trim()
				.replaceAll("[^A-Za-z0-9 \u0410-\u042f\u0430-\u044f]", "")
				.replaceAll("\\s+", " ")
				.replaceAll("ё", "е");
	}
}
