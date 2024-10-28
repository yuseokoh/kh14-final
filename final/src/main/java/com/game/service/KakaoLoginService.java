package com.game.service;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.game.configuration.KakaoLoginProperties;
import com.game.dto.KakaoUserDto;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Service
public class KakaoLoginService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private HttpHeaders headers;

    @Autowired
    private KakaoLoginProperties kakaoLoginProperties;

    @Autowired
    private KakaoUserService kakaoUserService;

    // 외부 설정 파일에서 비밀키 주입
    @Value("${custom.token.secret}")
    private String secretKey;


    public KakaoUserDto handleKakaoLogin(String accessToken) throws URISyntaxException {
        // 카카오 API를 통해 사용자 정보 가져오기
        KakaoUserDto kakaoUser = getUserInfo(accessToken);
        
        // 가져온 사용자 정보를 DB에 저장하거나 기존 유저 조회
        KakaoUserDto savedUser = kakaoUserService.saveOrUpdateKakaoUser(kakaoUser);
        
        // 멤버 테이블에도 유저를 삽입
        kakaoUserService.insertKakaoUserAndMember(savedUser);  // 새로 추가된 로직

        return savedUser;
    }



    public String createJwtToken(KakaoUserDto savedUser) {
        // 토큰 만료 시간 설정 (예: 1시간)
        long expirationTime = 1000 * 60 * 60;  // 1시간
        Date expirationDate = new Date(System.currentTimeMillis() + expirationTime);

        // 사용자 정보 클레임에 포함
        Map<String, Object> claims = new HashMap<>();
        claims.put("kakaoId", savedUser.getKakaoId());
        claims.put("email", savedUser.getMemberEmail());
        claims.put("nickname", savedUser.getMemberNickname());

        // JWT 토큰 생성
     // JWT 토큰 생성
        String token = Jwts.builder()
            .setClaims(claims)
            .setSubject(savedUser.getKakaoId())  // 사용자 ID (sub 클레임)
            .setIssuedAt(new Date())             // 토큰 발급 시간
            .setExpiration(expirationDate)       // 토큰 만료 시간
            .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()))  // 서명 알고리즘과 키 설정
            .compact();


        return token;
    }
    
    public KakaoUserDto getUserInfo(String accessToken) throws URISyntaxException {
        URI uri = new URI(kakaoLoginProperties.getUserInfoUrl());

        headers.set("Authorization", "Bearer " + accessToken);
        HttpEntity<Map<String, String>> entity = new HttpEntity<>(null, headers);

        Map<String, Object> response = restTemplate.postForObject(uri, entity, Map.class);
        if (response == null) {
            throw new IllegalStateException("카카오 API 응답이 null입니다.");
        }

        String kakaoId = String.valueOf(response.get("id"));
        Map<String, String> kakaoAccount = (Map<String, String>) response.get("kakao_account");
        String email = (kakaoAccount != null) ? kakaoAccount.get("email") : null;

        KakaoUserDto kakaoUser = new KakaoUserDto();
        kakaoUser.setKakaoId(kakaoId);
        kakaoUser.setMemberEmail(email != null ? email : "no-email@example.com");  // 이메일이 없으면 임시 이메일 설정
        kakaoUser.setMemberJoin(new java.sql.Date(System.currentTimeMillis()));

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

        Map<String, String> responseBody = response.getBody();
        return responseBody.get("access_token");
    }
    
    
}
