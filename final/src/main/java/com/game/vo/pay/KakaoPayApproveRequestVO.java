package com.game.vo.pay;

import lombok.Data;

//카카오페이 결제 승인 요청 데이터
@Data
public class KakaoPayApproveRequestVO {
	private String partnerOrderId;
	private String partnerUserId;
	private String tid;
	private String pgToken;
}