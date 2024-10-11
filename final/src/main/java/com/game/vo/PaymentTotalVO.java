package com.game.vo;

import java.util.List;

import com.game.dto.PaymentDetailDto;
import com.game.dto.PaymentDto;

import lombok.Data;

@Data
public class PaymentTotalVO {
	private PaymentDto paymentDto;
	private List<PaymentDetailDto> paymentDetailList;
}
