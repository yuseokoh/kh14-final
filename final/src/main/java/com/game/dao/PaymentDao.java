package com.game.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.game.dto.PaymentDetailDto;
import com.game.dto.PaymentDto;
import com.game.vo.PaymentTotalVO;

@Repository
public class PaymentDao {

    @Autowired
    private SqlSession sqlSession;

    // Payment 관련 메소드
    public int paymentSequence() {
        return sqlSession.selectOne("payment.paymentSequence");
    }
    
public void paymentInsert(PaymentDto paymentDto) {
        sqlSession.insert("payment.paymentInsert", paymentDto);
    }

    public PaymentDto selectPayment(int paymentNo) {
        return sqlSession.selectOne("payment.selectPayment", paymentNo);
    }

    public List<PaymentDto> selectPaymentList(String memberId) {
        return sqlSession.selectList("payment.selectPaymentList", memberId);
    }

    public boolean cancelPayment(int paymentNo) {
        return sqlSession.update("payment.cancelPayment", paymentNo) > 0;
    }

    // PaymentDetail 관련 메소드
    public int paymentDetailSequence() {
        return sqlSession.selectOne("paymentDetail.paymentDetailSequence");
    }

    public void paymentDetailInsert(PaymentDetailDto paymentDetailDto) {
        sqlSession.insert("paymentDetail.paymentDetailInsert", paymentDetailDto);
    }

    public List<PaymentDetailDto> selectPaymentDetailList(int paymentNo) {
        return sqlSession.selectList("paymentDetail.selectPaymentDetailList", paymentNo);
    }

    public PaymentDetailDto selectPaymentDetail(int paymentDetailNo) {
        return sqlSession.selectOne("paymentDetail.selectPaymentDetail", paymentDetailNo);
    }

    public boolean cancelPaymentDetail(int paymentDetailNo) {
        return sqlSession.update("paymentDetail.cancelPaymentDetail", paymentDetailNo) > 0;
    }

    public boolean decreaseItemRemain(int paymentNo, int money) {
        Map<String, Integer> params = new HashMap<>();
        params.put("paymentNo", paymentNo);
        params.put("money", money);
        return sqlSession.update("payment.decreaseItemRemain", params) > 0;
    }
    
    public List<PaymentTotalVO> selectTotalList(String memberId) {
		return sqlSession.selectList("payment.findTotal", memberId);
	}
    
    public List<PaymentDetailDto> selectDetailList(int paymentNo) {
		return sqlSession.selectList("payment.findDetail", paymentNo);
	}
    
    public List<PaymentDto> selectList(String memberId) {
		return sqlSession.selectList("payment.list", memberId);
	}
    
    public PaymentDto selectOne(int paymentNo) {
		return sqlSession.selectOne("payment.find", paymentNo);
	}
}