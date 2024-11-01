package com.game.vo;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class WebsocketResponseVO {
	private String senderMemberId;
	private String content;
	private LocalDateTime time;
}
