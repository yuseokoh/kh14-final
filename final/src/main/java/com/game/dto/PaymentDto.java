package com.game.dto;

import java.util.Date;
import lombok.Data;

@Data
public class PaymentDto {
    private int paymentNo;
    private String paymentTid;
    private String paymentName;
    private int paymentTotal;
    private int paymentRemain;
    private String paymentMemberId; // Member ID와 연결
    private Date paymentTime;
}
