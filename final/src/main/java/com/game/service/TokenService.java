package com.game.service;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
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

    // Access Token 생성
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

    // Refresh Token 생성
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

    // 토큰 검증 및 Claims 확인
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

    // Bearer Token 확인
    public boolean isBearerToken(String token) {
        return token != null && token.startsWith(BEARER_PREFIX);
    }

    // Bearer 부분 제거
    public String removeBearer(String token) {
        return token.substring(BEARER_PREFIX.length());
    }

//    // Refresh Token을 이용해 새로운 Access Token 발급
//    public String refreshAccessToken(String refreshToken) {
//        try {
//            SecretKey key = Keys.hmacShaKeyFor(tokenProperties.getSecret().getBytes(StandardCharsets.UTF_8));
//            Claims claims = (Claims) Jwts.parser()
//                    .verifyWith(key)
//                    .requireIssuer(tokenProperties.getIssuer())
//                    .build()
//                    .parse(refreshToken)
//                    .getPayload();
//            
//            // memberId 확인
//            String memberId = (String) claims.get("memberId");
//
//            // 새로운 Access Token 발급
//            MemberClaimVO vo = new MemberClaimVO();
//            vo.setMemberId(memberId);
//            vo.setMemberLevel((String) claims.get("memberLevel"));
//            
//            return createAccessToken(vo);  // 새로운 Access Token 반환
//        } catch (Exception e) {
//            throw new IllegalArgumentException("Invalid refresh token", e);
//        }
//    }

 // 매일 자정마다 만료된 토큰 제거
    @Scheduled(cron = "0 0 0 * * *")
    public void clearToken() {
        System.out.println("clearToken 메서드 실행됨: " + LocalDateTime.now());
        int deletedCount = memberTokenDao.clear();
        System.out.println("삭제된 토큰 개수: " + deletedCount);
    }
}