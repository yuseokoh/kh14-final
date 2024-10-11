package com.game.vo.pay;

import lombok.Data;

@Data
public class KakaoPayCancelRequestVO {
	private String tid;
	private int cancelAmount;
	private int cancelTaxFreeAmount = 0;
}