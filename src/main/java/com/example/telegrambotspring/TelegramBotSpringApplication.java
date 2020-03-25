package com.example.telegrambotspring;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.example.telegrambotspring.entities.Bot;
import com.example.telegrambotspring.entities.Chat;
import com.example.telegrambotspring.utils.Pair;

@SpringBootApplication
public class TelegramBotSpringApplication {
	public static void main(String[] args) {
		SpringApplication.run(TelegramBotSpringApplication.class, args);
	}

	@Bean
	public Map<Chat, Pair<Long, String>> answersForChats() {
		return new ConcurrentHashMap<>();
	}

	@Bean
	public Bot songsBot(@Value("${telegram.bot.token}") String token, Map<Chat, Pair<Long, String>> answersForChats) {
		return new Bot(token, answersForChats);
	}
}
