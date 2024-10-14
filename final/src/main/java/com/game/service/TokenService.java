package com.game.service;

import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.game.configuration.TokenProperties;
import com.game.dao.MemberTokenDao;
import com.game.dto.MemberTokenDto;
import com.game.vo.MemberClaimVO;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
@Service
public class TokenService {
	
		public static final String BEARER_PREFIX = "Bearer ";
		
		@Autowired
		private TokenProperties tokenProperties;
		
		@Autowired
		private MemberTokenDao memberTokenDao;
		
		public String createAccessToken(MemberClaimVO vo) {
			SecretKey key = Keys.hmacShaKeyFor(
					tokenProperties.getSecret().getBytes(StandardCharsets.UTF_8));
			Calendar c = Calendar.getInstance();
			Date now = c.getTime();
			c.add(Calendar.MINUTE, tokenProperties.getExpire());
			Date limit = c.getTime();
			return Jwts.builder()
						.signWith(key)
						.expiration(limit)
						.issuer(tokenProperties.getIssuer())
						.issuedAt(now)
						.claim("memberId", vo.getMemberId())
						.claim("memberLevel", vo.getMemberLevel())
					.compact();
		}
		
		public String createRefreshToken(MemberClaimVO vo) {
			SecretKey key = Keys.hmacShaKeyFor(
					tokenProperties.getSecret().getBytes(StandardCharsets.UTF_8));
			Calendar c = Calendar.getInstance();
			Date now = c.getTime();
			c.add(Calendar.MONTH, 1);
			Date limit = c.getTime();
			String token = Jwts.builder()
						.signWith(key)
						.expiration(limit)
						.issuer(tokenProperties.getIssuer())
						.issuedAt(now)
						.claim("memberId", vo.getMemberId())
						.claim("memberLevel", vo.getMemberLevel())
					.compact();
			
			MemberTokenDto memberTokenDto = new MemberTokenDto();
			memberTokenDto.setTokenTarget(vo.getMemberId());
			memberTokenDto.setTokenValue(token);
			memberTokenDao.insert(memberTokenDto);
			
			return token;
		}
		
		public MemberClaimVO check(String token) {
			SecretKey key = Keys.hmacShaKeyFor(
					tokenProperties.getSecret().getBytes(StandardCharsets.UTF_8));
			Claims claims = (Claims) Jwts.parser()
					.verifyWith(key)
					.requireIssuer(tokenProperties.getIssuer())
				.build()
					.parse(token)
					.getPayload();
			MemberClaimVO vo = new MemberClaimVO();
			vo.setMemberId((String) claims.get("memberId"));
			vo.setMemberLevel((String) claims.get("memberLevel"));
			return vo;
		}

		public boolean isBearerToken(String token) {
			return token != null && token.startsWith(BEARER_PREFIX);
		}
		
		public String removeBearer(String token) {
			return token.substring(BEARER_PREFIX.length());
		}
		
		@Scheduled(cron = "0 0 0 * * *")
		public void clearToken() {
			memberTokenDao.clear();
		}
		
}
