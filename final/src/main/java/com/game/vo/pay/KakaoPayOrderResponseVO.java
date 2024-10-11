package com.game.vo.pay;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class KakaoPayOrderResponseVO {
	private String tid;//거래번호
	private String cid;//가맹점번호
	private String status;//결제 상태
	private String partnerOrderId;//가맹점 내 주문번호
	private String partnerUserId;//가맹점 내 회원ID
	private String paymentMethodType;//결제 수단(CARD/MONEY)
	private KakaoPayAmountVO amount;//결제 금액 정보
	private KakaoPayAmountVO canceledAmount;//취소된 금액 정보
	private KakaoPayAmountVO cancelAvailableAmount;//취소 가능 금액 정보
	private String itemName;//상품 이름
	private String itemCode;//상품 코드
	private int quantity;//수량(1로 고정)
	private LocalDateTime createdAt;//준비 시각
	private LocalDateTime approvedAt;//승인 시각
	private LocalDateTime canceledAt;//취소 시각
	private KakaoPaySelectedCardInfoVO selectedCardInfo;//결제 카드 정보
	private List<KakaoPayPaymentActionDetailVO> paymentActionDetails;//결제 혹은 취소 상세 내역
}
