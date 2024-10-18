package com.game.dto;

import java.sql.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class GameDto {
	private int gameNo;
	private String gameTitle;
	private int gamePrice;
	private String gameDeveloper;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
	private Date gamePublicationDate;//가입일
	private int gameDiscount;
	private String gameCategory;
	private String gameGrade;
	private String gameTheme;
	private String gameDescription;
	private String gameShortDescription;
	private int gameUserScore;
	private int gameReviewCount;
	private String gamePlatforms;
	private String gameSystemRequirement;
	
}
