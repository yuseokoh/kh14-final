package com.game.service;

import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.game.configuration.TokenProperties;
import com.game.dao.MemberDao;
import com.game.dao.MemberTokenDao;
import com.game.dto.MemberDto;
import com.game.dto.MemberTokenDto;
import com.game.vo.MemberClaimVO;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

@Slf4j
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
	    try {
	        // 키 생성
	        SecretKey key = Keys.hmacShaKeyFor(tokenProperties.getSecret().getBytes(StandardCharsets.UTF_8));
	        Calendar c = Calendar.getInstance();
	        Date now = c.getTime();
	        c.add(Calendar.MINUTE, tokenProperties.getExpire());
	        Date limit = c.getTime();

	        // 로그 출력
	        System.out.println("Access Token 생성 시작");
	        System.out.println("현재 시간: " + now);
	        System.out.println("만료 시간: " + limit);
	        System.out.println("Member ID: " + vo.getMemberId());
	        System.out.println("Member Level: " + vo.getMemberLevel());
	        System.out.println("카카오id: {}"+ vo.getKakaoId());

	        log.info("Access Token 생성 시작");
	        log.info("현재 시간: {}", now);
	        log.info("만료 시간: {}", limit);
	        log.info("Member ID: {}", vo.getMemberId());
	        log.info("Member Level: {}", vo.getMemberLevel());
	        log.info("카카오id: {}", vo.getKakaoId());

	        // 토큰 생성
	        String token = Jwts.builder()
	                .signWith(key)
	                .expiration(limit)  // 만료 시간 설정
	                .issuer(tokenProperties.getIssuer())
	                .issuedAt(now)
	                .claim("memberId", vo.getMemberId())
	                .claim("memberLevel", vo.getMemberLevel())
	                .claim("kakaoId", vo.getKakaoId())
	                .compact();

	        // 생성된 토큰 로그 출력
	        log.info("생성된 Access Token: {}", token);
	        log.info("생성된 Access Token: {}", token);

	        return token;
	    } catch (Exception e) {
	        System.err.println("Access Token 생성 중 오류 발생: " + e.getMessage());
	        log.error("Access Token 생성 중 오류 발생", e);
	        throw new IllegalStateException("Access Token 생성 실패", e);
	    }
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
				.claim("memberId", vo.getMemberId()).claim("memberLevel", vo.getMemberLevel())
				.claim("kakaoId", vo.getKakaoId())
				.compact();
		

        // 생성된 토큰 로그 출력
        log.info("생성된 Refresh Token: {}", token);


		MemberTokenDto memberTokenDto = new MemberTokenDto();
		memberTokenDto.setTokenTarget(vo.getMemberId());
		memberTokenDto.setTokenValue(token);
		memberTokenDao.insert(memberTokenDto);

		return token;
	}

	// 토큰 검증 및 Claims 확인
	public MemberClaimVO check(String token) {
		SecretKey key = Keys.hmacShaKeyFor(tokenProperties.getSecret().getBytes(StandardCharsets.UTF_8));
		Claims claims = (Claims) Jwts.parser()
				.verifyWith(key)
				.requireIssuer(tokenProperties.getIssuer())
				.build()
				.parse(token).getPayload();
		MemberClaimVO vo = new MemberClaimVO();
		vo.setMemberId((String) claims.get("memberId"));
		vo.setMemberLevel((String) claims.get("memberLevel"));
		 vo.setKakaoId((String) claims.get("kakaoId"));
		   log.info("토큰 검증 성공: {}", token);
           log.info("Claims - Member ID: {}, Member Level: {}, Kakao ID: {}", vo.getMemberId(), vo.getMemberLevel(), vo.getKakaoId());

		 
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


	public void invalidateToken(String strippedToken) {
	    try {
	        // 토큰이 데이터베이스에 저장된 경우 해당 토큰을 삭제
	        memberTokenDao.deleteByTokenValue(strippedToken);
	        log.info("토큰이 무효화되었습니다: {}", strippedToken);
	    } catch (Exception e) {
	        log.error("토큰 무효화 중 오류 발생: {}", e.getMessage());
	        throw new IllegalStateException("토큰 무효화 실패", e);
	    }
	}


////    // Refresh Token을 이용해 새로운 Access Token 발급
////    public String refreshAccessToken(String refreshToken) {
////        try {
////            SecretKey key = Keys.hmacShaKeyFor(tokenProperties.getSecret().getBytes(StandardCharsets.UTF_8));
////            Claims claims = (Claims) Jwts.parser()
////                    .verifyWith(key)
////                    .requireIssuer(tokenProperties.getIssuer())
////                    .build()
////                    .parse(refreshToken)
////                    .getPayload();
////            
////            // memberId 확인
////            String memberId = (String) claims.get("memberId");
////
////            // 새로운 Access Token 발급
////            MemberClaimVO vo = new MemberClaimVO();
////            vo.setMemberId(memberId);
////            vo.setMemberLevel((String) claims.get("memberLevel"));
////            
////            return createAccessToken(vo);  // 새로운 Access Token 반환
////        } catch (Exception e) {
////            throw new IllegalArgumentException("Invalid refresh token", e);
////        }
////    }
//
//
// // 매일 자정마다 만료된 토큰 제거
//    @Scheduled(cron = "0 0 0 * * *")
//    public void clearToken() {
//        System.out.println("clearToken 메서드 실행됨: " + LocalDateTime.now());
//        int deletedCount = memberTokenDao.clear();
//        System.out.println("삭제된 토큰 개수: " + deletedCount);
//    }
//    
//    public KakaoUserClaimVO getUserFromToken(String token) {
//        try {
//            // SecretKey를 생성하여 서명을 검증
//            SecretKey key = Keys.hmacShaKeyFor(tokenProperties.getSecret().getBytes(StandardCharsets.UTF_8));
//
//            // JWT 토큰에서 클레임을 파싱하여 추출
//            Claims claims = (Claims) Jwts.parser()
//    				.verifyWith(key)
//    				.requireIssuer(tokenProperties.getIssuer())
//    				.build()
//    				.parse(token).getPayload();
//
//            // KakaoUserClaimVO 객체에 클레임 정보를 설정
//            KakaoUserClaimVO vo = new KakaoUserClaimVO();
//            vo.setKakaoId((String) claims.get("kakaoId"));  // 클레임에서 kakaoId 추출
//            vo.setMemberLevel((String) claims.get("memberLevel"));  // 클레임에서 memberLevel 추출
//
//            return vo;  // 사용자 정보 객체 반환
//        } catch (Exception e) {
//            // 예외 발생 시 로그 출력 및 예외 던지기
//            log.error("JWT 토큰에서 사용자 정보 추출 중 오류 발생", e);
//            throw new IllegalStateException("토큰에서 사용자 정보 추출 실패", e);
//        }
//    }
//
//
//
//
// // KakaoUserClaimVO를 통해 액세스 토큰 생성
//    public String createKakaoAccessToken(KakaoUserClaimVO vo) {
//    	   log.info("createKakaoAccessToken 메서드 호출됨");
//        try {
////        	  log.info("JWT 토큰 생성 시작 - kakaoId: {}, memberLevel: {}", vo.getKakaoId(), vo.getMemberLevel());
//            // 함수 호출 로그
//            log.info("createKakaoAccessToken 호출됨");
//
//            // SecretKey 생성
//            SecretKey key = Keys.hmacShaKeyFor(tokenProperties.getSecret().getBytes(StandardCharsets.UTF_8));
//            Calendar c = Calendar.getInstance();
//            Date now = c.getTime();
//            c.add(Calendar.MINUTE, tokenProperties.getExpire()); // 액세스 토큰 만료 시간 설정
//            Date limit = c.getTime();
//
//            // 로그 출력
//            log.info("현재 시간: {}", now);
//            log.info("만료 시간: {}", limit);
//            log.info("Kakao ID: {}", vo.getKakaoId());
////            log.info("Member Level: {}", vo.getMemberLevel());
//
//            // 토큰 생성
//            String token = Jwts.builder()
//                .signWith(key)
//                .expiration(limit)
//                .issuer(tokenProperties.getIssuer())
//                .issuedAt(now)
//                .claim("kakaoId", vo.getKakaoId()) // 카카오 사용자 ID 클레임
////                .claim("memberLevel", vo.getMemberLevel()) // 사용자 권한 레벨
//                .compact();
//
//            // 생성된 토큰 로그 출력
//            log.info("생성된 Access JWT 토큰: {}", token);
//            return token;
//        } catch (Exception e) {
//            log.error("Access Token 생성 중 오류 발생", e);
//            return null; // 예외 발생 시 null 반환
//        }
//    }
//
//    // KakaoUserDto를 통해 리프레시 토큰 생성
//    public String createRefreshToken(KakaoUserDto kakaoUser) {
//        try {
//            // 함수 호출 로그
//            log.info("createRefreshToken 호출됨");
//
//            // SecretKey 생성
//            SecretKey key = Keys.hmacShaKeyFor(tokenProperties.getSecret().getBytes(StandardCharsets.UTF_8));
//            Calendar c = Calendar.getInstance();
//            Date now = c.getTime();
//            c.add(Calendar.MONTH, 1); // 리프레시 토큰 만료 시간을 한 달로 설정
//            Date limit = c.getTime();
//
//            // 로그 출력
//            log.info("현재 시간: {}", now);
//            log.info("만료 시간: {}", limit);
//            log.info("Kakao ID: {}", kakaoUser.getKakaoId());
////            log.info("Nickname: {}", kakaoUser.getMemberNickname());
//
//            // 토큰 생성
//            String token = Jwts.builder()
//                .signWith(key)
//                .expiration(limit)
//                .issuer(tokenProperties.getIssuer())
//                .issuedAt(now)
//                .claim("kakaoId", kakaoUser.getKakaoId()) // 카카오 사용자 ID 추가
////                .claim("nickname", kakaoUser.getMemberNickname()) // 닉네임 추가
//                .compact();
//
//            // 생성된 토큰 로그 출력
//            log.info("생성된 Refresh JWT 토큰: {}", token);
//
//            // DB에 리프레시 토큰 저장
//            MemberTokenDto memberTokenDto = new MemberTokenDto();
//            memberTokenDto.setTokenTarget(kakaoUser.getKakaoId()); // 토큰을 카카오 ID에 연결
//            memberTokenDto.setTokenValue(token);
//            memberTokenDao.insert(memberTokenDto); // 토큰 DB에 저장
//
//            return token;
//        } catch (Exception e) {
//            log.error("Refresh Token 생성 중 오류 발생", e);
//            return null; // 예외 발생 시 null 반환
//        }
//    }
//
//
////	// KakaoUserClaimVO를 통해 액세스 토큰 생성
////	public String createKakaoAccessToken(KakaoUserClaimVO vo) {
////		SecretKey key = Keys.hmacShaKeyFor(tokenProperties.getSecret().getBytes(StandardCharsets.UTF_8));
////		Calendar c = Calendar.getInstance();
////		Date now = c.getTime();
////		c.add(Calendar.MINUTE, tokenProperties.getExpire()); // 액세스 토큰 만료 시간 설정
////		Date limit = c.getTime();
////		
////
////        // 로그 출력
////        System.out.println("Access Token 생성 시작");
////        System.out.println("현재 시간: " + now);
////        System.out.println("만료 시간: " + limit);
////        System.out.println("Member ID: " + vo.getKakaoId());
////        System.out.println("Member Level: " + vo.getMemberLevel());
////
////        log.info("Access Token 생성 시작");
////        log.info("현재 시간: {}", now);
////        log.info("만료 시간: {}", limit);
////        log.info("Member ID: {}", vo.getKakaoId());
////        log.info("Member Level: {}", vo.getMemberLevel());
////
////		return Jwts.builder().signWith(key).expiration(limit).issuer(tokenProperties.getIssuer()).issuedAt(now)
////				.claim("kakaoId", vo.getKakaoId()) // 카카오 사용자 ID 클레임
////				.claim("memberLevel", vo.getMemberLevel()) // 사용자 권한 레벨
////				.compact(); // 토큰 생성
////	}
////
////	// KakaoUserDto를 통해 리프레시 토큰 생성
////	public String createRefreshToken(KakaoUserDto kakaoUser) {
////		SecretKey key = Keys.hmacShaKeyFor(tokenProperties.getSecret().getBytes(StandardCharsets.UTF_8));
////		Calendar c = Calendar.getInstance();
////		Date now = c.getTime();
////		c.add(Calendar.MONTH, 1); // 리프레시 토큰 만료 시간을 한 달로 설정
////		Date limit = c.getTime();
////
////		// Refresh Token 생성
////		String token = Jwts.builder().signWith(key).expiration(limit).issuer(tokenProperties.getIssuer()).issuedAt(now)
////				.claim("kakaoId", kakaoUser.getKakaoId()) // 카카오 사용자 ID 추가
////				.claim("nickname", kakaoUser.getMemberNickname()) // 닉네임 추가
////				.compact();
////
////		// DB에 리프레시 토큰 저장
////		MemberTokenDto memberTokenDto = new MemberTokenDto();
////		memberTokenDto.setTokenTarget(kakaoUser.getKakaoId()); // 토큰을 카카오 ID에 연결
////		memberTokenDto.setTokenValue(token);
////		memberTokenDao.insert(memberTokenDto); // 토큰 DB에 저장
////
////		return token;
////	}
//
//	private String resolveToken(HttpServletRequest request) {
//		String bearerToken = request.getHeader("Authorization");
//		if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
//			return bearerToken.substring(7); // Bearer 부분 제거
//		}
//		return null;
//	}
//
//	public void invalidateToken(String strippedToken) {
//		try {
//			// 1. 해당 토큰을 DB에서 삭제
//			memberTokenDao.deleteByTokenValue(strippedToken);
//			System.out.println("토큰이 무효화되었습니다: " + strippedToken);
//		} catch (Exception e) {
//			// 오류 처리
//			System.err.println("토큰 무효화 중 오류 발생: " + e.getMessage());
//			throw new IllegalStateException("토큰 무효화 실패", e);
//		}
//	}

}

