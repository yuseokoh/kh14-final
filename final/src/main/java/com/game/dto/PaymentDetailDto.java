package com.game.dto;

import lombok.Data;

@Data
public class PaymentDetailDto {
    private int paymentDetailNo;
    private String paymentDetailName;
    private int paymentDetailPrice;
    private int paymentDetailQty;
    private int paymentDetailItem;
    private int paymentDetailOrigin; // Payment와 연결되는 외래키
    private String paymentDetailStatus; // "승인", "취소" 등 상태 값
}