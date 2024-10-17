package com.game.dto;

import java.sql.Timestamp;

import lombok.Data;

@Data
public class ChatDto {
	private int chatNo;
	private String chatSenderId;
	private String chatReceiverId;
	private String chatContent;
	private Timestamp chatTime;
}
