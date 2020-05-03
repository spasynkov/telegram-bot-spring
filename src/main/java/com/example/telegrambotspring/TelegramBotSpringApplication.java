package com.example.telegrambotspring;

import com.example.telegrambotspring.entities.Received;
import com.example.telegrambotspring.entities.bots.SongsBot;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class TelegramBotSpringApplication {
	public static void main(String[] args) {
		SpringApplication.run(TelegramBotSpringApplication.class, args);
	}

	@Bean
	public SongsBot songsBot(@Value("${telegram.bot.token}") String token, Received received) {
		return new SongsBot(token, received, SongsBot.UpdatesStrategy.WEBHOOKS);
	}
}
