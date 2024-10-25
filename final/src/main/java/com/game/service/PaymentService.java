package com.game.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.game.dao.PaymentDao;

@Service
public class PaymentService {
    
    @Autowired
    private PaymentDao paymentDao;
    
    // 공통 결제번호 생성 메서드
    public int generatePaymentSeq() {
        return paymentDao.paymentSequence();
    }
    public int generatePaymentDetailSeq() {
        return paymentDao.paymentDetailSequence();
    }
}