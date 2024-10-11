package com.game.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.game.dao.PaymentDao;
import com.game.dto.PaymentDetailDto;
import com.game.dto.PaymentDto;

@Service
public class PaymentService {

    @Autowired
    private PaymentDao paymentDao;

    // 결제 생성
    @Transactional
    public void createPayment(PaymentDto paymentDto, List<PaymentDetailDto> paymentDetailList) {
        // 결제 번호 생성
        int paymentNo = paymentDao.paymentSequence();
        paymentDto.setPaymentNo(paymentNo);
        paymentDao.paymentInsert(paymentDto);

        // 결제 상세 생성
        for (PaymentDetailDto detailDto : paymentDetailList) {
            int paymentDetailNo = paymentDao.paymentDetailSequence();
            detailDto.setPaymentDetailNo(paymentDetailNo);
            detailDto.setPaymentDetailOrigin(paymentNo);
            paymentDao.paymentDetailInsert(detailDto);
        }
    }

// 결제 조회
    public PaymentDto getPayment(int paymentNo) {
        return paymentDao.selectPayment(paymentNo);
    }

    // 결제 상세 조회
    public List<PaymentDetailDto> getPaymentDetails(int paymentNo) {
        return paymentDao.selectPaymentDetailList(paymentNo);
    }

    // 특정 회원의 결제 목록 조회
    public List<PaymentDto> getPaymentList(String memberId) {
        return paymentDao.selectPaymentList(memberId);
    }

    // 결제 취소
    @Transactional
    public boolean cancelPayment(int paymentNo) {
        // 결제 취소 업데이트
        boolean paymentCanceled = paymentDao.cancelPayment(paymentNo);
        if (!paymentCanceled) {
            return false;
        }

        // 결제 상세 취소 업데이트
        return paymentDao.cancelPaymentDetail(paymentNo);
    }

    // 결제 항목 개별 취소
    @Transactional
    public boolean cancelPaymentDetail(int paymentDetailNo) {
        // 결제 상세 항목 취소
        boolean detailCanceled = paymentDao.cancelPaymentDetail(paymentDetailNo);
        if (!detailCanceled) {
            return false;
        }

        // 결제 잔액 업데이트
        PaymentDetailDto detailDto = paymentDao.selectPaymentDetail(paymentDetailNo);
        return paymentDao.decreaseItemRemain(detailDto.getPaymentDetailOrigin(), detailDto.getPaymentDetailPrice() * detailDto.getPaymentDetailQty());
    }
}