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
import com.game.vo.KakaoUserClaimVO;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class KakaoTokenService {

    public static final String BEARER_PREFIX = "Bearer ";

    @Autowired
    private TokenProperties tokenProperties;

    @Autowired
    private MemberTokenDao memberTokenDao;

    // Kakao 로그인용 Access Token 생성 메서드
    public String createKakaoAccessToken(KakaoUserClaimVO vo) {
        // 비밀키 생성
    	SecretKey key = Keys.hmacShaKeyFor(
				tokenProperties.getSecret().getBytes(StandardCharsets.UTF_8));
		//만료시간 계산
		Calendar c = Calendar.getInstance();
		Date now = c.getTime();
		c.add(Calendar.MINUTE, tokenProperties.getExpire());
		Date limit = c.getTime();
		//토큰
		return Jwts.builder()
					.signWith(key)
					.expiration(limit)
					.issuer(tokenProperties.getIssuer())
					.issuedAt(now)
            .claim("kakaoId", vo.getKakaoId())
            .compact();
    }

    // Refresh Token 생성 메서드
    public String createRefreshToken(KakaoUserClaimVO vo) {
        // 비밀키 생성
    	SecretKey key = Keys.hmacShaKeyFor(
				tokenProperties.getSecret().getBytes(StandardCharsets.UTF_8));
		//만료시간 계산
		Calendar c = Calendar.getInstance();
		Date now = c.getTime();
		c.add(Calendar.MONTH, 1);
		Date limit = c.getTime();
        // 토큰 생성
    	String token = Jwts.builder()
				.signWith(key)
				.expiration(limit)
				.issuer(tokenProperties.getIssuer())
				.issuedAt(now)
            .claim("kakaoId", vo.getKakaoId())
            .compact();

        // 생성된 토큰을 DB에 저장
        MemberTokenDto memberTokenDto = new MemberTokenDto();
        memberTokenDto.setTokenTarget(vo.getKakaoId());
        memberTokenDto.setTokenValue(token);
        memberTokenDao.insert(memberTokenDto);

        return token;
    }

    // 토큰 검증 메서드
    public KakaoUserClaimVO check(String token) {
        try {
            // 비밀키 생성
            SecretKey key = Keys.hmacShaKeyFor(
                tokenProperties.getSecret().getBytes(StandardCharsets.UTF_8)
            );

          //토큰 해석
    		Claims claims = (Claims) Jwts.parser()
    				.verifyWith(key)
    				.requireIssuer(tokenProperties.getIssuer())
    			.build()
    			.parse(token)
				.getPayload();

            // 클레임에서 정보 추출하여 VO 생성
            KakaoUserClaimVO vo = new KakaoUserClaimVO();
            vo.setKakaoId(claims.get("kakaoId", String.class));
            return vo;

        } catch (Exception e) {
            log.error("토큰 검증 중 오류 발생", e);
            return null;
        }
    }

    // Bearer 토큰인지 확인하는 메서드
    public boolean isBearerToken(String token) {
        return token != null && token.startsWith(BEARER_PREFIX);
    }

    // Bearer 접두사를 제거하는 메서드
    public String removeBearer(String token) {
        return token.substring(BEARER_PREFIX.length());
    }

    // 매일 자정에 만료된 토큰 삭제 (스케줄러)
    @Scheduled(cron = "0 0 0 * * *")
    public void clearToken() {
        memberTokenDao.clear();
    }
}
