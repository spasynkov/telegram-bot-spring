package com.example.telegrambotspring.entities;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;


/**
 * The class описание чата
 * со свойствами
 * <b>chatId</b> and <b>isGroup</b>
 * @version 1.0.1
 */
@Slf4j
@Getter
@ToString
@EqualsAndHashCode
public class Chat {
	/** идентификатор чата */
	private int chatId;
	/** флаг принадлежности групповой или личный */
	private boolean isGroup;

	/**
	 * Конструктор класса - создание нового объекта с определенными значениями
	 * @param chatId идентификатор чата
	 * @param isGroup флаг принадлежности групповой или личный
	 */
	public Chat(int chatId, boolean isGroup) {
		this.chatId = chatId;
		this.isGroup = isGroup;
	}

	/**
	 * Конструктор класса - создание нового объекта с определенными значениями
	 * @param chatId идентификатор чата
	 * @param type флаг принадлежности групповой или личный полученный в виде строки
	 */
	public Chat(int chatId, String type) {
		this.chatId = chatId;
		this.isGroup = "group".equalsIgnoreCase(type);
	}


}
