package com.example.telegrambotspring;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.example.telegrambotspring.entities.Chat;
import com.example.telegrambotspring.entities.bots.SongsBot;
import com.example.telegrambotspring.utils.Pair;

@SpringBootApplication
public class TelegramBotSpringApplication {
	public static void main(String[] args) {
		Date curTime = new Date();    //DeleteCode
		System.out.println(curTime.getTime()/1000);
		SpringApplication.run(TelegramBotSpringApplication.class, args);
	}

	@Bean
	public Map<Chat, Pair<Long, String>> answersForChats() {
		return new ConcurrentHashMap<>();
	}

	@Bean
	public SongsBot songsBot(@Value("${telegram.bot.token}") String token, Map<Chat, Pair<Long, String>> answersForChats) {
		return new SongsBot(token, answersForChats, SongsBot.UpdatesStrategy.WEBHOOKS);
	}
}
