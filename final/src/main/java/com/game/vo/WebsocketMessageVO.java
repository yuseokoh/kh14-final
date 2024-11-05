package com.game.vo;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class WebsocketMessageVO {
	private int no;
	private String senderMemberId;
	private String receiverMemberId;
	private String content;
	private LocalDateTime time;
}
