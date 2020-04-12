package com.example.telegrambotspring;

import com.example.telegrambotspring.entities.Chat;
import com.example.telegrambotspring.entities.Message;
import com.example.telegrambotspring.entities.Received;
import com.example.telegrambotspring.entities.bots.SongsBot;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@SpringBootApplication
public class TelegramBotSpringApplication {
	public static void main(String[] args) {
		SpringApplication.run(TelegramBotSpringApplication.class, args);
	}

	@Bean
	public Map<Chat, List<Message>> incomingMessage() {
		return new ConcurrentHashMap<>();
	}

	@Bean
	public SongsBot songsBot(@Value("${telegram.bot.token}") String token, Received received) {
		return new SongsBot(token, received, SongsBot.UpdatesStrategy.WEBHOOKS);
	}
}
