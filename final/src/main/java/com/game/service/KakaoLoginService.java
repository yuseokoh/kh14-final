package com.game.service;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.game.configuration.KakaoLoginProperties;
import com.game.configuration.TokenProperties;
import com.game.dao.KakaoUserDao;
import com.game.dao.MemberDao;
import com.game.dto.KakaoUserDto;
import com.game.dto.MemberDto;
import com.game.vo.KakaoUserClaimVO;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class KakaoLoginService {
	@Autowired
	private TokenProperties tokenProperties;

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private HttpHeaders headers;

	@Autowired
	private KakaoLoginProperties kakaoLoginProperties;

	@Autowired
	private KakaoUserService kakaoUserService;

	@Autowired
	private KakaoUserDao kakaoUserDao;
	@Autowired
	private MemberDao memberDao;
	
	@Autowired
	private TokenService tokenService;
	@Autowired
	private KakaoTokenService kakaTokenService;

	// 외부 설정 파일에서 비밀키 주입
	@Value("${custom.token.secret}")
	private String secretKey;
	
	
    public KakaoUserDto handleKakaoLogin(String accessToken) throws URISyntaxException {
        // 카카오 API를 통해 사용자 정보 가져오기
        KakaoUserDto kakaoUser = getUserInfo(accessToken);
        
        log.info("카카오 사용자 정보: {}", kakaoUser);

        // 가져온 사용자 정보를 DB에 저장하거나 기존 유저 조회
        KakaoUserDto savedUser = kakaoUserService.saveOrUpdateKakaoUser(kakaoUser);
        
        // 멤버 테이블에도 유저를 삽입
        kakaoUserService.insertKakaoUserAndMember(savedUser);
        return savedUser;
    }



    public String createJwtToken(KakaoUserDto savedUser) {
        // 토큰 만료 시간 설정 (예: 1시간)
//        long expirationTime = 1000 * 60 * 60;  // 1시간
//        Date expirationDate = new Date(System.currentTimeMillis() + expirationTime);

    	 // 비밀키를 사용해 서명할 때, 클레임에 사용자 정보 포함
        Map<String, Object> claims = new HashMap<>();
        claims.put("kakaoId", savedUser.getKakaoId());
        claims.put("email", savedUser.getMemberEmail());
        claims.put("nickname", savedUser.getMemberNickname());

        // JWT 토큰 생성
        String token = Jwts.builder()
            .setClaims(claims)
            .setSubject(savedUser.getKakaoId()) // 사용자 ID 설정
            .setIssuedAt(new Date()) // 토큰 발급 시간
            .setExpiration(new Date(System.currentTimeMillis() + 3600000)) // 만료 시간 (1시간)
            .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()), SignatureAlgorithm.HS256) // 서명 알고리즘
            .compact();

        return token;
    }
    
    public KakaoUserDto getUserInfo(String accessToken) throws URISyntaxException {
        URI uri = new URI(kakaoLoginProperties.getUserInfoUrl());

        headers.set("Authorization", "Bearer " + accessToken);
        HttpEntity<Map<String, String>> entity = new HttpEntity<>(null, headers);

        log.info("카카오 사용자 정보 요청 시작 - 액세스 토큰: {}", accessToken);
        
        Map<String, Object> response = restTemplate.postForObject(uri, entity, Map.class);
        if (response == null) {
            throw new IllegalStateException("카카오 API 응답이 null입니다.");
        }
        log.info("카카오 API 응답: {}", response);
        
        String kakaoId = String.valueOf(response.get("id"));
        Map<String, String> kakaoAccount = (Map<String, String>) response.get("kakao_account");
        String email = (kakaoAccount != null) ? kakaoAccount.get("email") : null;

        KakaoUserDto kakaoUser = new KakaoUserDto();
        kakaoUser.setKakaoId(kakaoId);
        kakaoUser.setMemberEmail(email != null ? email : "no-email@example.com");  // 이메일이 없으면 임시 이메일 설정
        kakaoUser.setMemberJoin(new java.sql.Date(System.currentTimeMillis()));

        log.info("추출된 카카오 사용자 정보 - ID: {}, 이메일: {}", kakaoId, email);
        
        // 이메일이 없으면 이메일 입력이 필요한 상태로 표시
        if (email == null) {
            kakaoUser.setEmailRequired(true);
        }

        return kakaoUser;
    }

    
    public String getAccessToken(String code) throws URISyntaxException {
        URI uri = new URI(kakaoLoginProperties.getTokenUrl());

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", kakaoLoginProperties.getClientId());
        body.add("redirect_uri", kakaoLoginProperties.getRedirectUri());
        body.add("code", code);
        
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(uri, entity, Map.class);

        log.info("카카오 액세스 토큰 요청 응답: {}", response.getBody());
        
        Map<String, String> responseBody = response.getBody();
        return responseBody.get("access_token");
    }
    
	
	
	
	
	
	
	
	
	
	
	
	
//
//	public String createJwtToken(KakaoUserDto savedUser) {
//		// 토큰 만료 시간 설정 (예: 1시간)
////        long expirationTime = 1000 * 60 * 60;  // 1시간
////        Date expirationDate = new Date(System.currentTimeMillis() + expirationTime);
//
//		// 비밀키를 사용해 서명할 때, 클레임에 사용자 정보 포함
//		Map<String, Object> claims = new HashMap<>();
//		claims.put("kakaoId", savedUser.getKakaoId());
//		claims.put("email", savedUser.getMemberEmail());
//		claims.put("nickname", savedUser.getMemberNickname());
//
//		// JWT 토큰 생성
//		String token = Jwts.builder().setClaims(claims).setSubject(savedUser.getKakaoId()) // 사용자 ID 설정
//				.setIssuedAt(new Date()) // 토큰 발급 시간
//				.setExpiration(new Date(System.currentTimeMillis() + 3600000)) // 만료 시간 (1시간)
//				.signWith(Keys.hmacShaKeyFor(secretKey.getBytes()), SignatureAlgorithm.HS256) // 서명 알고리즘
//				.compact();
//
//		return token;
//	}
//	
//	public String getKakaoId(String accessToken) throws URISyntaxException {
//		URI uri = new URI(kakaoLoginProperties.getUserInfoUrl());
//		headers.set("Authorization", "Bearer " + accessToken);
//		HttpEntity<Map<String, String>> entity = new HttpEntity<>(null, headers);
//
//		Map<String, Object> response = restTemplate.postForObject(uri, entity, Map.class);
//		if (response == null) {
//			throw new IllegalStateException("카카오 API 응답이 null입니다.");
//		}
//		
//		return String.valueOf(response.get("id"));
//	}
//
//	// KakaoLoginService.java - getUserInfo 메서드
//	public KakaoUserDto getUserInfo(String accessToken) throws URISyntaxException {
//		URI uri = new URI(kakaoLoginProperties.getUserInfoUrl());
//		headers.set("Authorization", "Bearer " + accessToken);
//		HttpEntity<Map<String, String>> entity = new HttpEntity<>(null, headers);
//
//		Map<String, Object> response = restTemplate.postForObject(uri, entity, Map.class);
//		if (response == null) {
//			throw new IllegalStateException("카카오 API 응답이 null입니다.");
//		}
//
//		log.info("카카오 API 응답: {}", response);
//
//		String kakaoId = String.valueOf(response.get("id"));
//		Map<String, String> kakaoAccount = (Map<String, String>) response.get("kakao_account");
//		String email = (kakaoAccount != null) ? kakaoAccount.get("email") : null;
//
//		KakaoUserDto kakaoUser = new KakaoUserDto();
//		kakaoUser.setKakaoId(kakaoId);
//		kakaoUser.setMemberEmail(email != null ? email : "no-email@example.com");
//		kakaoUser.setMemberJoin(new java.sql.Date(System.currentTimeMillis()));
//
//		// 이메일 값 확인을 위한 디버깅 로그
//		log.info("설정된 이메일 값: {}", kakaoUser.getMemberEmail());
//
//		if (email == null) {
//			kakaoUser.setEmailRequired(true);
//		}
//
//		return kakaoUser;
//	}
//	public String getAccessToken(String code) throws URISyntaxException {
//	    log.debug("getAccessToken 메서드가 호출되었습니다. 인가 코드: {}", code); // 메서드 호출 로그
//
//	    URI uri = new URI(kakaoLoginProperties.getTokenUrl());
//
//	    HttpHeaders headers = new HttpHeaders();
//	    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//	    headers.add("Accept", "application/json;charset=UTF-8");
//
//	    MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
//	    body.add("grant_type", "authorization_code");
//	    body.add("client_id", kakaoLoginProperties.getClientId());
//	    body.add("redirect_uri", kakaoLoginProperties.getRedirectUri());
//	    log.info("redirect uri = {}", kakaoLoginProperties.getRedirectUri());
//	    body.add("code", code);
//
//	    HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(body, headers);
//
//	    try {
//	        log.debug("카카오 토큰 요청 전송: URI = {}, Headers = {}, Body = {}", uri, headers, body); // 요청 전 로그
//
//	        ResponseEntity<Map> response = restTemplate.postForEntity(uri, entity, Map.class);
//
//	        // 응답 상태 코드 및 본문 검증
//	        if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
//	            log.error("카카오 토큰 요청 실패 - 상태 코드: {}, 응답 본문: {}", response.getStatusCode(), response.getBody());
//	            throw new IllegalStateException("카카오 토큰 요청에 실패했습니다.");
//	        }
//
//	        log.info("카카오 액세스 토큰 요청 응답: {}", response.getBody());
//	        Map<String, Object> responseBody = response.getBody();
//	        String accessToken = (String) responseBody.get("access_token");
//
//	        if (accessToken == null || accessToken.isEmpty()) {
//	            log.error("액세스 토큰이 null이거나 비어있습니다.");
//	            throw new IllegalStateException("유효한 액세스 토큰을 가져오지 못했습니다.");
//	        }
//
//	        log.debug("발급된 액세스 토큰111: {}", accessToken); 
//	        return accessToken;
//	    } catch (Exception e) {
//	        log.error("카카오 액세스 토큰 요청 중 오류 발생: {}", e.getMessage(), e);
//	        throw new IllegalStateException("카카오 액세스 토큰 요청 실패", e);
//	    }
//	}
//
//
//
//	
//	public void handleKakaoLogin(KakaoUserDto kakaoUser) {
//	    log.debug("handleKakaoLogin 메서드 실행 시작");
//
//	    // 1. 기존 카카오 사용자 확인 (중복 삽입 방지)
//	    Optional<KakaoUserDto> existingKakaoUser = kakaoUserDao.selectOneByKakaoId(kakaoUser.getKakaoId());
//	    if (existingKakaoUser.isPresent()) {
//	        log.info("이미 존재하는 카카오 유저입니다: {}", kakaoUser.getKakaoId());
//	    } else {
//	        // 2. 카카오 테이블에 사용자 정보 삽입
//	        kakaoUserDao.insert(kakaoUser);
//	        log.info("카카오 유저 테이블에 사용자 정보 삽입 완료: {}", kakaoUser);
//	    }
//
//	    // 3. member 테이블과의 연동
//	    String memberEmail = kakaoUser.getMemberEmail();
//	    log.debug("member 테이블에서 이메일 조회: {}", memberEmail);
//
//	    Optional<MemberDto> existingMember = memberDao.selectOneByEmail(memberEmail);
//	    if (existingMember.isPresent()) {
//	        // 일치하는 회원이 있으면 linked_member_id 설정
//	        String memberId = existingMember.get().getMemberId();
//	        log.debug("일치하는 회원 ID: {}", memberId);
//	        kakaoUserDao.updateLinkedMemberId(kakaoUser.getKakaoId(), memberId);
//	        log.info("카카오 유저와 기존 회원 연동 완료: memberId = {}", memberId);
//	    } else {
//	        // 일치하는 회원이 없으면 로그만 출력
//	        log.warn("일치하는 회원이 없습니다. 추가 회원가입 로직을 처리해야 합니다.");
//	    }
//	}
//
//
//
////    public void handleKakaoLogin(KakaoUserDto kakaoUser) {
////        // 1. 카카오 테이블에 사용자 정보 삽입
////        kakaoUserDao.insert(kakaoUser);
////        log.info("카카오 유저 테이블에 사용자 정보 삽입 완료: {}", kakaoUser);
////
////        // 2. 카카오 이메일과 member 테이블의 이메일 일치 여부 확인
////        Optional<MemberDto> existingMember = memberDao.selectOneByEmail(kakaoUser.getMemberEmail());
////
////        if (existingMember.isPresent()) {
////            // 3. 일치하는 회원이 있으면 linked_member_id 설정
////            String memberId = existingMember.get().getMemberId();
////            kakaoUserDao.updateLinkedMemberId(kakaoUser.getKakaoId(), memberId);
////            log.info("카카오 유저와 기존 회원 연동 완료: memberId = {}", memberId);
////        } else {
////            // 4. 일치하는 회원이 없으면 필요한 로직 추가 (예: 회원가입 유도)
////            log.warn("일치하는 회원이 없습니다. 추가 회원가입 로직을 처리해야 합니다.");
////        }
////    }
////
////	public void handleKakaoLogin(KakaoUserDto kakaoUser) {
////	    String memberEmail = kakaoUser.getMemberEmail();
////
////	    // 디버깅 로그: 이메일 값 확인
////	    log.info("handleKakaoLogin - 설정된 이메일 값: {}", memberEmail);
////
////	    if (memberEmail == null || memberEmail.isEmpty() || "no-email@example.com".equals(memberEmail)) {
////	        log.warn("카카오 사용자 이메일이 유효하지 않습니다. 추가 이메일 입력이 필요합니다.");
////	        return;
////	    }
////
////	    // 이메일 값 전달 전 디버깅 로그
////	    log.info("이메일 값 전달 전 - Before linkKakaoAndMemberAccounts 호출: {}", memberEmail);
////	    kakaoUserService.linkKakaoAndMemberAccounts(kakaoUser.getKakaoId(), memberEmail);
////	}
//
////	public void handleKakaoLogin(KakaoUserDto kakaoUser) {
////		kakaoUserService.saveOrUpdateKakaoUser(kakaoUser);
////		kakaoUserService.linkKakaoAndMemberAccounts(kakaoUser.getKakaoId(), kakaoUser.getMemberEmail());
////	}
//
////	public KakaoUserDto handleKakaoLogin(String accessToken) throws URISyntaxException {
////		// 1. 액세스 토큰을 사용해 카카오 사용자 정보 가져오기
////		KakaoUserDto kakaoUser = getUserInfo(accessToken); // 위에서 작성된 getUserInfo 메서드 사용
////		if (kakaoUser == null) {
////			log.error("카카오 사용자 정보 가져오기 실패");
////			throw new RuntimeException("Failed to retrieve Kakao user information.");
////		}
////
////		// 2. 가져온 사용자 정보를 기존 로직에 넘겨 처리
////		handleKakaoLogin(kakaoUser);
////		log.info("handleKakaoLogin - 설정된 이메일: {}", kakaoUser.getMemberEmail());
////		// 3. 사용자 정보를 반환
////		return kakaoUser;
////	}
//
////	public KakaoUserDto handleKakaoLogin(String accessToken) throws URISyntaxException {
////	    // 1. 액세스 토큰을 사용해 카카오 사용자 정보 가져오기
////	    KakaoUserDto kakaoUser = getUserInfo(accessToken); // 위에서 작성된 getUserInfo 메서드 사용
////	    if (kakaoUser == null) {
////	        log.error("카카오 사용자 정보 가져오기 실패");
////	        throw new RuntimeException("Failed to retrieve Kakao user information.");
////	    }
////
////	    
////	    // 로그 추가: 가져온 이메일 값을 출력
////	    log.info("handleKakaoLogin - 가져온 이메일 값: {}", kakaoUser.getMemberEmail());
////
////	    // 이메일이 null이거나 잘못된 형식일 경우 경고 로그를 출력
////	    if (kakaoUser.getMemberEmail() == null || !kakaoUser.getMemberEmail().contains("@")) {
////	        log.warn("handleKakaoLogin - 잘못된 이메일 값: {}", kakaoUser.getMemberEmail());
////	    }
////
////	    // 3. 사용자 정보를 반환
////	    return kakaoUser;
////	}
//
//	public KakaoUserDto handleKakaoLogin(String accessToken) throws URISyntaxException {
//	    // 1. 액세스 토큰을 사용해 카카오 사용자 정보 가져오기
//	    KakaoUserDto kakaoUser = getUserInfo(accessToken); // 위에서 작성된 getUserInfo 메서드 사용
//	    if (kakaoUser == null) {
//	        log.error("카카오 사용자 정보 가져오기 실패");
//	        throw new RuntimeException("Failed to retrieve Kakao user information.");
//	    }
//
//	    // 2. 로그 추가: 가져온 이메일 값을 출력
//	    log.info("handleKakaoLogin - 가져온 이메일 값: {}", kakaoUser.getMemberEmail());
//
//	    // 3. 이메일이 null이거나 잘못된 형식일 경우 경고 로그를 출력
//	    if (kakaoUser.getMemberEmail() == null || !kakaoUser.getMemberEmail().contains("@")) {
//	        log.warn("handleKakaoLogin - 잘못된 이메일 값: {}", kakaoUser.getMemberEmail());
//	    }
//
//	    // 4. 멤버 테이블에서 카카오 사용자와 연결된 멤버 정보를 조회
//	    Optional<MemberDto> existingMember = memberDao.selectOneByKakaoUserId(kakaoUser.getKakaoId());
//	    String memberLevel; // 기본 값 설정
//
//	    // 5. 기존 멤버가 존재하면 memberLevel 값을 가져옴
//	    if (existingMember.isPresent()) {
//	        memberLevel = existingMember.get().getMemberLevel();
//	        log.info("기존 멤버를 찾았습니다: memberLevel = {}", memberLevel);
//	    } else {
//	    	  memberLevel = "기본 레벨"; 
//	        log.info("연결된 멤버를 찾을 수 없습니다. 기본 레벨로 설정합니다.");
//	    }
//	    log.info("카카오 사용자 정보: {}", kakaoUser);
//	    // 6. JWT 토큰 생성 (KakaoUserDto와 memberLevel 포함)
//	    KakaoUserClaimVO kakaoUserClaimVO = new KakaoUserClaimVO();
//	    kakaoUserClaimVO.setKakaoId(kakaoUser.getKakaoId());
//	    kakaoUserClaimVO.setMemberLevel(memberLevel);
//
//	    log.info("createJwtToken 호출 준비 - KakaoUserClaimVO: {}", kakaoUserClaimVO);
//	    String jwtToken = createJwtToken(kakaoUserClaimVO); // createJwtToken 메서드 호출
//	    log.info("생성된 JWT 토큰: {}", jwtToken);
//
//	    
//	    // 7. 사용자 정보를 반환
//	    return kakaoUser;
//	}
//
//	private String createJwtToken(KakaoUserClaimVO kakaoUserClaimVO) {
//	    try {
//	        // SecretKey 생성
//	        SecretKey key = Keys.hmacShaKeyFor(tokenProperties.getSecret().getBytes(StandardCharsets.UTF_8));
//	        Calendar calendar = Calendar.getInstance();
//	        Date now = calendar.getTime();
//	        calendar.add(Calendar.MINUTE, tokenProperties.getExpire()); // 액세스 토큰 만료 시간 설정
//	        Date expiration = calendar.getTime();
//
//	        // 로그 출력
//	        log.info("JWT 토큰 생성123 - 현재 시간: {}, 만료 시간: {}, Kakao ID: {}", now, expiration, kakaoUserClaimVO.getKakaoId());
//
//	        // JWT 토큰 생성
//	        return Jwts.builder()
//	            .signWith(key)
//	            .expiration(expiration)
//	            .issuer(tokenProperties.getIssuer())
//	            .issuedAt(now)
//	            .claim("kakaoId", kakaoUserClaimVO.getKakaoId()) // 클레임에 Kakao ID 추가
//	            .claim("memberLevel", kakaoUserClaimVO.getMemberLevel()) // 클레임에 Member Level 추가
//	            .compact();
//	    } catch (Exception e) {
//	        log.error("JWT 토큰 생성 중 오류 발생", e);
//	        throw new IllegalStateException("JWT 토큰 생성 실패", e);
//	    }
//	}


	
	
	
	
	
}
