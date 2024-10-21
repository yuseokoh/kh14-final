package com.game.dto;

import lombok.Data;

@Data
public class RegisterRequestDto {
	private String token;
	private String memberId;
	private String memberPw;
}
