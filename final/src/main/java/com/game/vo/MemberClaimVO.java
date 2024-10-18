package com.game.vo;

import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Configuration
@Data
public class MemberClaimVO {
	private String memberId;
	private String memberLevel;
}
