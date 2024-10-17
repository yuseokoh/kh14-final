package com.game.vo;

import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
public class MemberClaimVO {
	private String memberId;
	private String memberLevel;
}
