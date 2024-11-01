package com.game.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
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

import com.game.dao.ChatDao;
import com.game.dao.RoomMessageDao;
import com.game.vo.ChatMoreVO;
import com.game.vo.MemberClaimVO;
import com.game.vo.WebsocketMessageVO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class WebSocketEventHandler {

	@Autowired
	private TokenService tokenService;
	@Autowired
	private SimpMessagingTemplate messagingTemplate;
	@Autowired
	private ChatDao chatDao;
	@Autowired
	private RoomMessageDao roomMessageDao;
	
	
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
		if("/public/users".equals(accessor.getDestination())) {
			Set<String> values = new TreeSet<>(userList.values());
			messagingTemplate.convertAndSend("/public/users", values);
		}
		else if(accessor.getDestination().startsWith("/public/db")) {
			String memberId = accessor.getDestination().substring("/public/db/".length());
			List<WebsocketMessageVO> messageList = chatDao.selectListMemberComplete(memberId, 1, 100);
			if(messageList.isEmpty()) return;
			List<WebsocketMessageVO> prevMessageList = chatDao.selectListMemberComplete(memberId, 1, 100, messageList.get(0).getNo());
			ChatMoreVO moreVO = new ChatMoreVO();
			moreVO.setMessageList(messageList);
			moreVO.setLast(prevMessageList.isEmpty());
			
			messagingTemplate.convertAndSend("/public/db/"+memberId, moreVO);
		}
		else if(accessor.getDestination().equals("/public/db")) {//커뮤니티 채팅
			List<WebsocketMessageVO> messageList = chatDao.selectListMemberComplete(null, 1, 100);
			if(messageList.isEmpty()) return;
			
			List<WebsocketMessageVO> prevMessageList = chatDao.selectListMemberComplete(null, 1, 100, messageList.get(0).getNo());
			
			ChatMoreVO moreVO = new ChatMoreVO();
			moreVO.setMessageList(messageList);
			moreVO.setLast(prevMessageList.isEmpty());
			
			messagingTemplate.convertAndSend("/public/db", moreVO);
		}
		else if(accessor.getDestination().startsWith("/private/db")) {
			String removeStr = accessor.getDestination().substring("/private/db/".length());
			int slash = removeStr.indexOf("/");
			int roomNo = Integer.parseInt(removeStr.substring(0, slash));
			String memberId = removeStr.substring(slash + 1);
			
			List<WebsocketMessageVO> messageList = roomMessageDao.selectListMemberComplete(memberId, 1, 100, roomNo);
			ChatMoreVO moreVO = new ChatMoreVO();
			moreVO.setMessageList(messageList);
			moreVO.setLast(true);
			if(messageList.size() > 0) {
				List<WebsocketMessageVO> prevMessageList = roomMessageDao.selectListMemberComplete(memberId, 1, 100, roomNo, messageList.get(0).getNo());
				moreVO.setLast(prevMessageList.isEmpty());
			}
			messagingTemplate.convertAndSend("/private/db/"+roomNo+"/"+memberId, moreVO);
			
		}
	}
	
	@EventListener
	public void userLeaved(SessionDisconnectEvent event) {
		StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
		String sessionId = accessor.getSessionId();
		
		userList.remove(sessionId);
		log.info("접속 종료 = {}", sessionId);
		
		Set<String> values = new TreeSet<>(userList.values());
		messagingTemplate.convertAndSend("/public/users", values);
	}
}
