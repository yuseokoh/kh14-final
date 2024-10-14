package com.game.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.HandlerInterceptor;

import com.game.service.TokenService;
import com.game.vo.MemberClaimVO;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MemberInterceptor implements HandlerInterceptor {
	
	@Autowired
	private TokenService tokenService;
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		
		String method = request.getMethod();
		if(method.toLowerCase().equals("options")) {
			return true;
		}
		try {
			String token = request.getHeader("Authorization");
			if(token == null) throw new Exception("헤더없음");
			
			if(tokenService.isBearerToken(token) == false)
				throw new Exception("Bearer 토큰이 아님");
			
			String realToken = tokenService.removeBearer(token);
			
			MemberClaimVO claimVO = tokenService.check(realToken);
			log.info("아이디 = {}, 등급 = {}", claimVO.getMemberId(), claimVO.getMemberLevel());
			return true;
		}
		catch(Exception e) {
			response.sendError(401);
			return false;
		}
		
		
	}
	

}
