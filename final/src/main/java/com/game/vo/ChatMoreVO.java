package com.game.vo;

import java.util.List;

import lombok.Data;

@Data
public class ChatMoreVO {
	private boolean last;
	private List<WebsocketMessageVO> messageList;
}
