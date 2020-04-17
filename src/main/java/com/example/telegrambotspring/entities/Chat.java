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

	/**
	 * метод - чтение идентификатора чата
	 * @return возвращает строку - идентификатор чата

	public int getChatId() {
		return chatId;
	}

	/**
	 * метод - чтение флага принадлежности чата
	 * @return возвращает флага принадлежности чата групповой или личный

	public boolean isGroup() {
		return isGroup;
	}


	/**
	 * переопределяем метод equals
	 * @return возвращает boolean результат сравнения чатов по следующим полям
	 * chatId, isGroup

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Chat chat = (Chat) o;
		return chatId == chat.chatId &&
				isGroup == chat.isGroup;
	}

	/**
	 * переопределяем метод hashCode
	 * @return возвращает hashCode Objects

	@Override
	public int hashCode() {
		return Objects.hash(chatId, isGroup);
	}

	/**
	 * переопределяем метод toString
	 * @return возвращает отформатированную строку

	@Override
	public String toString() {
		return (isGroup ? "Group" : "Chat") + "{chatId=" + chatId + '}';
	}
	*/
}
