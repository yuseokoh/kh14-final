package com.game.vo;

import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Data
@Configuration
public class MemberClaimVO {
	private String memberId;
	private String memberLevel;
    private String kakaoId;
}

