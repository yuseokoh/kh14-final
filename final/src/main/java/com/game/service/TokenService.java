package com.game.service;

import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.game.configuration.TokenProperties;
import com.game.dao.MemberDao;
import com.game.dao.MemberTokenDao;
import com.game.dto.KakaoUserDto;
import com.game.dto.MemberDto;
import com.game.dto.MemberTokenDto;
import com.game.vo.KakaoUserClaimVO;
import com.game.vo.MemberClaimVO;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;

@Service
public class TokenService {

	public static final String BEARER_PREFIX = "Bearer ";

	@Autowired
	private TokenProperties tokenProperties;

	@Autowired
	private MemberTokenDao memberTokenDao;
	@Autowired
	private MemberDao memberDao;

	// Access Token 생성
	public String createAccessToken(MemberClaimVO vo) {
		SecretKey key = Keys.hmacShaKeyFor(tokenProperties.getSecret().getBytes(StandardCharsets.UTF_8));
		Calendar c = Calendar.getInstance();
		Date now = c.getTime();
		c.add(Calendar.MINUTE, tokenProperties.getExpire());
		Date limit = c.getTime();
		return Jwts.builder().signWith(key).expiration(limit).issuer(tokenProperties.getIssuer()).issuedAt(now)
				.claim("memberId", vo.getMemberId()).claim("memberLevel", vo.getMemberLevel()).compact();
	}

	// Refresh Token 생성
	public String createRefreshToken(MemberClaimVO vo) {

		// member_id가 존재하는지 확인
		MemberDto member = memberDao.selectByMemberId(vo.getMemberId());
		if (member == null) {
			throw new IllegalArgumentException("존재하지 않는 member_id: " + vo.getMemberId());
		}

		SecretKey key = Keys.hmacShaKeyFor(tokenProperties.getSecret().getBytes(StandardCharsets.UTF_8));
		Calendar c = Calendar.getInstance();
		Date now = c.getTime();
		c.add(Calendar.MONTH, 1);
		Date limit = c.getTime();
		String token = Jwts.builder().signWith(key).expiration(limit).issuer(tokenProperties.getIssuer()).issuedAt(now)
				.claim("memberId", vo.getMemberId()).claim("memberLevel", vo.getMemberLevel()).compact();

		MemberTokenDto memberTokenDto = new MemberTokenDto();
		memberTokenDto.setTokenTarget(vo.getMemberId());
		memberTokenDto.setTokenValue(token);
		memberTokenDao.insert(memberTokenDto);

		return token;
	}

	// 토큰 검증 및 Claims 확인
	public MemberClaimVO check(String token) {
		SecretKey key = Keys.hmacShaKeyFor(tokenProperties.getSecret().getBytes(StandardCharsets.UTF_8));
		Claims claims = (Claims) Jwts.parser().verifyWith(key).requireIssuer(tokenProperties.getIssuer()).build()
				.parse(token).getPayload();
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

	// 일정 시간마다 만료된 토큰 제거
	@Scheduled(cron = "0 0 0 * * *")
	public void clearToken() {
		memberTokenDao.clear();
	}

	// KakaoUserClaimVO를 통해 액세스 토큰 생성
	public String createKakaoAccessToken(KakaoUserClaimVO vo) {
		SecretKey key = Keys.hmacShaKeyFor(tokenProperties.getSecret().getBytes(StandardCharsets.UTF_8));
		Calendar c = Calendar.getInstance();
		Date now = c.getTime();
		c.add(Calendar.MINUTE, tokenProperties.getExpire()); // 액세스 토큰 만료 시간 설정
		Date limit = c.getTime();

		return Jwts.builder().signWith(key).expiration(limit).issuer(tokenProperties.getIssuer()).issuedAt(now)
				.claim("kakaoId", vo.getKakaoId()) // 카카오 사용자 ID 클레임
				.claim("memberLevel", vo.getMemberLevel()) // 사용자 권한 레벨
				.compact(); // 토큰 생성
	}

	// KakaoUserDto를 통해 리프레시 토큰 생성
	public String createRefreshToken(KakaoUserDto kakaoUser) {
		SecretKey key = Keys.hmacShaKeyFor(tokenProperties.getSecret().getBytes(StandardCharsets.UTF_8));
		Calendar c = Calendar.getInstance();
		Date now = c.getTime();
		c.add(Calendar.MONTH, 1); // 리프레시 토큰 만료 시간을 한 달로 설정
		Date limit = c.getTime();

		// Refresh Token 생성
		String token = Jwts.builder().signWith(key).expiration(limit).issuer(tokenProperties.getIssuer()).issuedAt(now)
				.claim("kakaoId", kakaoUser.getKakaoId()) // 카카오 사용자 ID 추가
				.claim("nickname", kakaoUser.getMemberNickname()) // 닉네임 추가
				.compact();

		// DB에 리프레시 토큰 저장
		MemberTokenDto memberTokenDto = new MemberTokenDto();
		memberTokenDto.setTokenTarget(kakaoUser.getKakaoId()); // 토큰을 카카오 ID에 연결
		memberTokenDto.setTokenValue(token);
		memberTokenDao.insert(memberTokenDto); // 토큰 DB에 저장

		return token;
	}

	private String resolveToken(HttpServletRequest request) {
		String bearerToken = request.getHeader("Authorization");
		if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
			return bearerToken.substring(7); // Bearer 부분 제거
		}
		return null;
	}

	public void invalidateToken(String strippedToken) {
		try {
			// 1. 해당 토큰을 DB에서 삭제
			memberTokenDao.deleteByTokenValue(strippedToken);
			System.out.println("토큰이 무효화되었습니다: " + strippedToken);
		} catch (Exception e) {
			// 오류 처리
			System.err.println("토큰 무효화 중 오류 발생: " + e.getMessage());
			throw new IllegalStateException("토큰 무효화 실패", e);
		}
	}

}
