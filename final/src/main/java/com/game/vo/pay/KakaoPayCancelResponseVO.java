package com.game.vo.pay;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class KakaoPayCancelResponseVO {
	private String aid;//요청 번호
	private String tid;//거래 번호
	private String cid;//가맹점 코드
	private String status;//결제 상태
	private String partnerOrderId;//가맹점 내 주문번호
	private String partnerUserId;//가맹점 내 회원ID
	private String paymentMethodType;//결제 수단(CARD/MONEY)
	private KakaoPayAmountVO amount;//결제 금액
	private KakaoPayAmountVO approvedCancelAmount;//이번에 취소된 금액 정보
	private KakaoPayAmountVO canceledAmount;//취소된 금액 정보
	private KakaoPayAmountVO cancelAvailableAmount;//취소 가능 금액 정보
	private String itemName;//상품 이름
	private String itemCode;//상품 코드(사용하지 않음)
	private int quantity;//상품 수량(1로 고정)
	private LocalDateTime createdAt;//결제 준비 요청 시각
	private LocalDateTime approvedAt;//결제 승인 시각
	private LocalDateTime canceledAt;//결제 취소 시각
	private String payload;//취소 시 추가로 전달한 값
}