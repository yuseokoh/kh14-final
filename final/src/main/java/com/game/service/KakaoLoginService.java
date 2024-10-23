package com.game.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.game.configuration.KakaoLoginConfiguration;
import com.game.dto.MemberDto;

@Service
public class KakaoLoginService {

    @Autowired
    private KakaoLoginConfiguration kakaoLoginConfiguration;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private HttpHeaders kakaoHeaders;

    public String getAccessToken(String code) {
        // 액세스 토큰 요청 시 필요한 파라미터 설정
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", kakaoLoginConfiguration.getClientId());
        params.add("redirect_uri", kakaoLoginConfiguration.getRedirectUri());
        params.add("code", code);

        // HttpHeaders에 Content-Type 설정
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(params, headers);
        try {
            // 카카오에 POST 요청을 보내서 액세스 토큰을 얻습니다.
            ResponseEntity<String> response = restTemplate.postForEntity(kakaoLoginConfiguration.getTokenUrl(), entity, String.class);
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readTree(response.getBody()).get("access_token").asText();
        } catch (Exception e) {
            throw new RuntimeException("카카오 액세스 토큰 획득 중 오류 발생: " + e.getMessage(), e);
        }
    }

    public MemberDto getUserInfo(String accessToken) {
        // 사용자 정보 요청을 위한 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        try {
            // 카카오에 GET 요청을 보내서 사용자 정보를 얻습니다.
            ResponseEntity<String> response = restTemplate.exchange(kakaoLoginConfiguration.getUserInfoUrl(), HttpMethod.GET, entity, String.class);
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(response.getBody(), MemberDto.class);
        } catch (Exception e) {
            throw new RuntimeException("카카오 사용자 정보 조회 중 오류 발생: " + e.getMessage(), e);
        }
    }
}