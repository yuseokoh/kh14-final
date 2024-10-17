package com.game.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import com.game.vo.MemberClaimVO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class WebSocketEventHandler {

	@Autowired
	private TokenService tokenService;
	@Autowired
	private SimpMessagingTemplate messagingTemplate;
	
	private Map<String, String> userList = Collections.synchronizedMap(new HashMap<>());
	@EventListener
	public void userEnter(SessionConnectEvent event) {
		StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
		String sessionId = accessor.getSessionId();
		String accessToken = accessor.getFirstNativeHeader("accessToken");
		if(accessToken == null) return;
		
		MemberClaimVO claimVO = tokenService.check(tokenService.removeBearer(accessToken));
		
		userList.put(sessionId, claimVO.getMemberId());
		log.info("접속 인원수 ={} , 세션 = {} , 아이디 = {}", userList.size(), sessionId, claimVO.getMemberId());
		
	}
	
	@EventListener
	public void userSubscribe(SessionSubscribeEvent event) {
		StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
		if("public/users".equals(accessor.getDestination())) {
			Set<String> values = new TreeSet<>(userList.values());
			messagingTemplate.convertAndSend("/public/users", values);
		}
		else if(accessor.getDestination().startsWith("/public/db")) {
			String memberId = accessor.getDestination().substring("/public/db/".length());
		}
		else if(accessor.getDestination().equals("/public/db")) {//커뮤니티 채팅
			
		}
	}
	
	@EventListener
	public void userLeaved(SessionDisconnectEvent event) {
		StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
		String sessionId = accessor.getSessionId();
		
		userList.remove(sessionId);
		log.info("접속 종료 = {}", sessionId);
	}
}
