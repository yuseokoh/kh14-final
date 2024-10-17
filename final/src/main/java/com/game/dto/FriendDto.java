package com.game.dto;

import java.sql.Timestamp;

import lombok.Data;

@Data
public class FriendDto {
	private int friendFk;//식별자
	private String friendFrom; //발신자
	private String friendTo; //수신자
	private Timestamp sendTime; //발신 시간
	private String accept; //수락여부
	
}
