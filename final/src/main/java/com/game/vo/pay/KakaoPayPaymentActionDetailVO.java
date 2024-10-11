package com.game.vo.pay;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class KakaoPayPaymentActionDetailVO {
	private String aid;//요청 고유번호
	private LocalDateTime approvedAt;//거래 시각
	private int amount;//결제 혹은 취소 총액
	private int pointAmount;//결제 혹은 취소 포인트 금액
	private int discountAmount;//할인 금액
	private int greenDeposit;//컵 보증금
	private String paymentActionType;//PAYMENT/CANCEL/ISSUED_SID 중 하나
	private String payload;//요청에 전달한 데이터
}