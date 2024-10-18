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
import org.springframework.web.bind.annotation.CrossOrigin;

import com.game.dao.FriendDao;
import com.game.dto.FriendDto;
import com.game.service.TokenService;
import com.game.vo.MemberClaimVO;

@CrossOrigin
@Controller
public class FriendWebsocketController {

	@Autowired
	private SimpMessagingTemplate messagingTemplate;
	@Autowired
	private TokenService tokenService;
	
	@Autowired
	private FriendDao friendDao;
	
	@MessageMapping("/friend/request/{friendTo}")
	public void request(@DestinationVariable String friendTo, Message<FriendDto> request) {
		StompHeaderAccessor accessor = StompHeaderAccessor.wrap(request);
		String accessToken = accessor.getFirstNativeHeader("accessToken");
		
		if(accessToken == null) {
			return;
		}
		MemberClaimVO claimVO = tokenService.check(tokenService.removeBearer(accessToken));
		if(claimVO.getMemberId().equals(friendTo)) {
			return;
		}
		
		FriendDto req = request.getPayload();
		FriendDto response = new FriendDto();
		response.setFriendFrom(claimVO.getMemberId());
		response.setFriendTo(friendTo);
		messagingTemplate.convertAndSend("/friend/request/"+response.getFriendFrom());
		messagingTemplate.convertAndSend("/friend/request/"+response.getFriendTo());
		
		//DB에 등록
		int friendFk = friendDao.sequence();
		FriendDto friendDto = new FriendDto();
		friendDto.setFriendFk(friendFk);
		friendDto.setFriendFrom(response.getFriendFrom());
		friendDto.setFriendTo(response.getFriendTo());
		
		friendDao.request(friendDto);
	}
	
}
