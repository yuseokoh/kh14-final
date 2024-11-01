package com.game.dto;

import lombok.Data;

@Data
public class CommunityReactionDto {
	private String memberId;
    private int communityNo;
    private String reactionType; // "L" for Like, "U" for Unlike  좋아요, 싫어요 선택 gpt코드
}
