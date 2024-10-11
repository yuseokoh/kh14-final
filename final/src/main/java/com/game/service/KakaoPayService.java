package com.game.service;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.game.configuration.KakaoPayProperties;
import com.game.vo.pay.KakaoPayApproveRequestVO;
import com.game.vo.pay.KakaoPayApproveResponseVO;
import com.game.vo.pay.KakaoPayCancelRequestVO;
import com.game.vo.pay.KakaoPayCancelResponseVO;
import com.game.vo.pay.KakaoPayOrderRequestVO;
import com.game.vo.pay.KakaoPayOrderResponseVO;
import com.game.vo.pay.KakaoPayReadyRequestVO;
import com.game.vo.pay.KakaoPayReadyResponseVO;
@Service
public class KakaoPayService {
    
    @Autowired
    private KakaoPayProperties kakaoPayProperties; // cid, secret
    @Autowired
    private RestTemplate template; // 전송 도구
    @Autowired
    private HttpHeaders headers; // 전송 헤더
    
    // 결제 준비 (ready)
    public KakaoPayReadyResponseVO ready(KakaoPayReadyRequestVO request) throws URISyntaxException {
        URI uri = new URI("https://kapi.kakao.com/v1/payment/ready");
        
        Map<String, String> body = new HashMap<>();
        body.put("cid", kakaoPayProperties.getCid());
        body.put("partner_order_id", request.getPartnerOrderId());
        body.put("partner_user_id", request.getPartnerUserId());
        body.put("item_name", request.getItemName());
        body.put("quantity", "1");
        body.put("total_amount", String.valueOf(request.getTotalAmount()));
        body.put("tax_free_amount", "0");
        body.put("approval_url", request.getApprovalUrl() + "/" + request.getPartnerOrderId());
        body.put("cancel_url", request.getCancelUrl());
        body.put("fail_url", request.getFailUrl());
        
        HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, headers);
        
        return template.postForObject(uri, entity, KakaoPayReadyResponseVO.class);
    }
// 결제 승인 (approve)
    public KakaoPayApproveResponseVO approve(KakaoPayApproveRequestVO request) throws URISyntaxException {
        URI uri = new URI("https://kapi.kakao.com/v1/payment/approve");
        
        Map<String, String> body = new HashMap<>();
        body.put("cid", kakaoPayProperties.getCid());
        body.put("partner_order_id", request.getPartnerOrderId());
        body.put("partner_user_id", request.getPartnerUserId());
        body.put("tid", request.getTid());
        body.put("pg_token", request.getPgToken());
        
        HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, headers);
        
        return template.postForObject(uri, entity, KakaoPayApproveResponseVO.class);
    }
    
    // 결제 조회 (order)
    public KakaoPayOrderResponseVO order(KakaoPayOrderRequestVO request) throws URISyntaxException {
        URI uri = new URI("https://kapi.kakao.com/v1/payment/order");
        
        Map<String, String> body = new HashMap<>();
        body.put("cid", kakaoPayProperties.getCid());
        body.put("tid", request.getTid());
        
        HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, headers);
        
        return template.postForObject(uri, entity, KakaoPayOrderResponseVO.class);
    }
    
    // 결제 취소 (cancel)
    public KakaoPayCancelResponseVO cancel(KakaoPayCancelRequestVO request) throws URISyntaxException {
        URI uri = new URI("https://kapi.kakao.com/v1/payment/cancel");
        
        Map<String, String> body = new HashMap<>();
        body.put("cid", kakaoPayProperties.getCid());
        body.put("tid", request.getTid());
        body.put("cancel_amount", String.valueOf(request.getCancelAmount()));
        body.put("cancel_tax_free_amount", String.valueOf(request.getCancelTaxFreeAmount()));
        
        HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, headers);
        
        return template.postForObject(uri, entity, KakaoPayCancelResponseVO.class);
    }
}