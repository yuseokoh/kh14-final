package com.game.controller;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;

import com.game.dao.ChatDao;
import com.game.dto.ChatDto;
import com.game.service.TokenService;
import com.game.vo.ChatRequestVO;
import com.game.vo.ChatResponseVO;
import com.game.vo.MemberClaimVO;

@Controller
public class WebsocketController {
	
	@Autowired
	private SimpMessagingTemplate messagingTemplate;
	@Autowired
	private TokenService tokenService;
	@Autowired
	private ChatDao chatDao;
	
	
	@MessageMapping("/chat/{receiverId}")
	public void chat(@DestinationVariable String receiverId, Message<ChatRequestVO> message) {
		
		StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
		String accessToken = accessor.getFirstNativeHeader("accessToken");
		
		if(accessToken == null) {
			return;
		}
		MemberClaimVO claimVO = tokenService.check(tokenService.removeBearer(accessToken));
		if(claimVO.getMemberId().equals(receiverId)) {
			return;
		}
		
		ChatRequestVO request = message.getPayload();
		
		ChatResponseVO response = new ChatResponseVO();
		response.setContent(request.getContent());
		response.setTime(LocalDateTime.now());
		response.setSenderMemberId(claimVO.getMemberId());
		response.setReceiverMemberId(receiverId);
		
		messagingTemplate.convertAndSend("/public/chat/"+response.getSenderMemberId(), response);
		messagingTemplate.convertAndSend("/public/chat/"+response.getReceiverMemberId(), response);
		
		int chatNo = chatDao.sequence();
		ChatDto chatDto = new ChatDto();
		chatDto.setChatNo(chatNo);
		chatDto.setChatSenderId(claimVO.getMemberId());
		chatDto.setChatReceiverId(receiverId);
		chatDto.setChatContent(request.getContent());
		chatDao.insert(chatDto);
	}
}
