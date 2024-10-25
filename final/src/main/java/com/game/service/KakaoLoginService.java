package com.game.service;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.game.configuration.KakaoLoginProperties;
import com.game.dto.KakaoUserDto;

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
    
    
    public KakaoUserDto handleKakaoLogin(String accessToken) throws URISyntaxException {
        // 카카오 API를 통해 사용자 정보 가져오기
        KakaoUserDto kakaoUser = getUserInfo(accessToken);
        
        // 가져온 사용자 정보를 DB에 저장하거나 기존 유저 조회
        KakaoUserDto savedUser = kakaoUserService.saveOrUpdateKakaoUser(kakaoUser);
        
        // 로그인 처리 (JWT 토큰 발급 등 추가 작업)
        return savedUser;
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
        kakaoUser.setMemberEmail(email);
        kakaoUser.setMemberJoin(new java.sql.Date(System.currentTimeMillis()));

        // 이메일이 null이라면 이메일 입력 페이지로 이동
        if (email == null) {
            // 프론트엔드에서 이메일 입력 페이지로 이동시키도록 플래그를 반환할 수 있음
            kakaoUser.setEmailRequired(true); // 이메일 입력이 필요한 상태 표시
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


//    public KakaoUserDto getUserInfo(String accessToken) throws URISyntaxException {
//        URI uri = new URI(kakaoLoginProperties.getUserInfoUrl());
//
//        headers.set("Authorization", "Bearer " + accessToken);
//
//        HttpEntity<Map<String, String>> entity = new HttpEntity<>(null, headers);
//
//        Map<String, Object> response = restTemplate.postForObject(uri, entity, Map.class);
//
//        if (response == null) {
//            throw new IllegalStateException("카카오 API 응답이 null입니다.");
//        }
//
//        String kakaoId = String.valueOf(response.get("id"));
//        Map<String, String> properties = (Map<String, String>) response.get("properties");
//        String nickname = (properties != null) ? properties.get("nickname") : null;
//        Map<String, String> kakaoAccount = (Map<String, String>) response.get("kakao_account");
//        String email = (kakaoAccount != null) ? kakaoAccount.get("email") : null;
//
//        KakaoUserDto kakaoUser = new KakaoUserDto();
//        kakaoUser.setKakaoId(kakaoId);
//        kakaoUser.setMemberNickname(nickname);
//        kakaoUser.setMemberEmail(email);
//        kakaoUser.setMemberJoin(new java.sql.Date(System.currentTimeMillis()));
//
//        return kakaoUser;
//    }


}
