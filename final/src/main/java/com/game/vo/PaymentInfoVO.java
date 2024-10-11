package com.game.vo;

import java.util.List;

import com.game.dto.PaymentDetailDto;
import com.game.dto.PaymentDto;
import com.game.vo.pay.KakaoPayOrderResponseVO;

import lombok.Data;

@Data
public class PaymentInfoVO {
	private PaymentDto paymentDto;
	private List<PaymentDetailDto> paymentDetailList;
	private KakaoPayOrderResponseVO responseVO;
}