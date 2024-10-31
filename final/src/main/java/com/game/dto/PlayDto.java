package com.game.dto;

import java.sql.Date;

import lombok.Data;

@Data
public class PlayDto {
	private int playNo;
	private int gameNo;
	private String memberId;
	private int playScore;
	private int playLevel;
	private int playTime;
	private Date playLastplayed;
}
