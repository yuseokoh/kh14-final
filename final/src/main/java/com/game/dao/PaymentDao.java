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
        return sqlSession.selectOne("payment.getNextPaymentSeq");
    }
    
public void paymentInsert(PaymentDto paymentDto) {
        sqlSession.insert("payment.insertPayment", paymentDto);
    }

    public PaymentDto selectPayment(int paymentNo) {
        return sqlSession.selectOne("payment.selectPaymentByNo", paymentNo);
    }

    public List<PaymentDto> selectPaymentList(String memberId) {
        return sqlSession.selectList("payment.selectPaymentsByMemberId", memberId);
    }

    public boolean cancelAll(int paymentNo) {
		return sqlSession.update("payment.cancelAll", paymentNo) > 0;
	}
	public boolean cancelAllItem(int paymentNo) {
		return sqlSession.update("payment.cancelAllItem", paymentNo) > 0;
	}
	public List<PaymentDto> selectAllPayments(Map<String, Object> params) {
	    return sqlSession.selectList("payment.selectAllPayments", params);
	}
	public Double getTotalSales(Map<String, Object> params) {
	    return sqlSession.selectOne("payment.getTotalSales", params);
	}

	


    // PaymentDetail 관련 메소드
    
    public int paymentDetailSequence() {
        return sqlSession.selectOne("paymentDetail.paymentDetailSequence");
    }

    public void paymentDetailInsert(PaymentDetailDto paymentDetailDto) {
        sqlSession.insert("paymentDetail.paymentDetailInsert", paymentDetailDto);
    }

    public List<PaymentDetailDto> selectPaymentDetailList(int paymentNo) {
        return sqlSession.selectList("paymentDetail.selectPaymentDetailsByPaymentNo", paymentNo);
    }

    public PaymentDetailDto selectPaymentDetail(int paymentDetailNo) {
        return sqlSession.selectOne("paymentDetail.selectPaymentDetailByNo", paymentDetailNo);
    }

    public boolean cancelItem(int paymentDetailNo) {
		return sqlSession.update("paymentDetail.cancelItem", paymentDetailNo) > 0;
	}

    public boolean decreaseItemRemain(int paymentNo, int money) {
        Map<String, Integer> params = new HashMap<>();
        params.put("paymentNo", paymentNo);
        params.put("money", money);
        return sqlSession.update("paymentDetail.decreaseItemRemain", params) > 0;
    }
    
   
}