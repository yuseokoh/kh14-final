package com.game.dto;

import java.sql.Date;

import lombok.Data;

@Data
public class KakaoUserDto {
	private Integer kakaoUserId;
	private String kakaoId;
	private String memberNickname;
	private String memberEmail;
	private Date memberJoin;
	private boolean emailRequired;
}
