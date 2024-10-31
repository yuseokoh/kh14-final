package com.game.vo;

import lombok.Data;

@Data
public class ReplyComplexRequestVO {
	private int replyOrigin; //커뮤니티 게시글
	private Integer beginRow, endRow;
}
