package com.game.vo.pay;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.Data;

//카카오페이 결제 승인 응답 데이터
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class KakaoPayApproveResponseVO {
	private String aid;//요청 번호
	private String tid;//거래 번호
	private String cid;//가맹점 번호
	private String sid;//정기결제 번호
	private String partnerOrderId;//가맹점 내 거래번호
	private String partnerUserId;//가맹점 내 구매자ID
	private String paymentMethodType;//결제유형(CARD/MONEY)
	private KakaoPayAmountVO amount;//금액 정보
	private KakaoPayCardInfoVO cardInfo;//결제 카드 정보
	private String itemName;//상품명
	private String itemCode;//상품코드
	private int quantity;//상품수량
	private LocalDateTime createdAt;//결제 준비 시작시각
	private LocalDateTime approvedAt;//결제 승인시각
	private String payload;//결제 요청시 전달한 값(옵션)
}
