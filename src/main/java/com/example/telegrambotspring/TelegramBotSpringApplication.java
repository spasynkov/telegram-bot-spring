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


/**
 * The class уровня (слоя) Business Service - слоя бизнес-логики
 * содержат точку запуска программы
 * @author  Stas Pasynkov
 * @version 1.0.1
 */
@SpringBootApplication
public class TelegramBotSpringApplication {
	/**
	 * точка запуска программы
	 */
	public static void main(String[] args) {
		Date curTime = new Date();    //DeleteCode
		System.out.println(curTime.getTime());
		SpringApplication.run(TelegramBotSpringApplication.class, args);
	}

	/**
	 * метод создает новую потокобезопасную мапу
	 * @return возвращает мапу запроса телеграмм-бота как потокобезопасную мапу
	 */
	@Bean
	public Map<Chat, Pair<Long, String>> answersForChats() {
		return new ConcurrentHashMap<>();
	}

	/**
	 * метод
	 * @param token идентификатор бота
	 * @param answersForChats  мапа ответа в чат
	 * @return возвращает - ???
	 */
	@Bean
	public SongsBot songsBot(@Value("${telegram.bot.token}") String token, Map<Chat, Pair<Long, String>> answersForChats) {
		return new SongsBot(token, answersForChats, SongsBot.UpdatesStrategy.WEBHOOKS);
	}
}
