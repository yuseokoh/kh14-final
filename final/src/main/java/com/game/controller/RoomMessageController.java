package com.game.controller;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;

import com.game.dao.RoomDao;
import com.game.dao.RoomMessageDao;
import com.game.dto.RoomMessageDto;
import com.game.service.TokenService;
import com.game.vo.MemberClaimVO;
import com.game.vo.WebsocketRequestVO;
import com.game.vo.WebsocketResponseVO;

@Controller
public class RoomMessageController {

	@Autowired
	private SimpMessagingTemplate messagingTemplate;
	@Autowired
	private TokenService tokenService;
	@Autowired
	private RoomMessageDao roomMessageDao;
	@Autowired
	private RoomDao roomDao;
	
	@MessageMapping("/room/{roomNo}")
	public void chat(@DestinationVariable int roomNo, Message<WebsocketRequestVO> message) {
		StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
		String accessToken = accessor.getFirstNativeHeader("accessToken");
		if(accessToken == null) return;
		
		MemberClaimVO claimVO = tokenService.check(tokenService.removeBearer(accessToken));
		
		WebsocketRequestVO request = message.getPayload();
		
		WebsocketResponseVO response = new WebsocketResponseVO();
		response.setSenderMemberId(claimVO.getMemberId());
		response.setTime(LocalDateTime.now());
		response.setContent(request.getContent());
		messagingTemplate.convertAndSend("/private/chat/"+roomNo, response);
		
		int roomMessageNo = roomMessageDao.sequence();
		RoomMessageDto roomMessageDto = new RoomMessageDto();
		roomMessageDto.setRoomMessageNo(roomMessageNo);
		roomMessageDto.setRoomMessageSender(claimVO.getMemberId());
		roomMessageDto.setRoomMessageReceiver(null);
		roomMessageDto.setRoomMessageContent(request.getContent());
		roomMessageDto.setRoomMessageTime(Timestamp.valueOf(response.getTime()));
		roomMessageDto.setRoomNo(roomNo);
		roomMessageDao.insert(roomMessageDto);
	}
}
