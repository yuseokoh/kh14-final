package com.game.vo;

import lombok.Data;

@Data
public class MemberLoginResponseVO {
	private String MemberId;
	private String memberLevel;
	private String accessToken;
	private String refreshToken;
	private String kakaoId;
	  private boolean emailRequired;
}
