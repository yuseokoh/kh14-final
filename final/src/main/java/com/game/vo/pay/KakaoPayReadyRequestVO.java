package com.game.vo.pay;

import lombok.Data;

//카카오페이 결제 준비 요청 데이터
@Data
public class KakaoPayReadyRequestVO {
	private String partnerOrderId;
	private String partnerUserId;
	private String itemName;
	private int totalAmount;
	private String approvalUrl;
	private String cancelUrl;
	private String failUrl;
}