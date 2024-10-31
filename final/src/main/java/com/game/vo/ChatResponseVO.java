package com.game.vo;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class ChatResponseVO {
	private String senderMemberId;
	private String receiverMemberId;
	private String content;
	private LocalDateTime time;
}
