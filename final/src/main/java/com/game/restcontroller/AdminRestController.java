package com.game.restcontroller;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.game.dao.PaymentDao;
import com.game.dto.PaymentDetailDto;
import com.game.dto.PaymentDto;
import com.game.error.TargetNotFoundException;
import com.game.service.KakaoPayService;
import com.game.service.TokenService;
import com.game.vo.MemberClaimVO;
import com.game.vo.PaymentInfoVO;
import com.game.vo.pay.KakaoPayOrderRequestVO;
import com.game.vo.pay.KakaoPayOrderResponseVO;

@CrossOrigin
@RestController
@RequestMapping("/admin")
public class AdminRestController {
    
    @Autowired
    private KakaoPayService kakaoPayService;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private PaymentDao paymentDao;
    
    @GetMapping("/detail/{paymentNo}")
    public PaymentInfoVO detail(@RequestHeader("Authorization") String token,
                                @PathVariable int paymentNo) throws URISyntaxException {
        PaymentDto paymentDto = paymentDao.selectPayment(paymentNo);
        if (paymentDto == null) throw new TargetNotFoundException("존재하지 않는 결제내역");
        MemberClaimVO claimVO = tokenService.check(tokenService.removeBearer(token));
        if (!paymentDto.getPaymentMemberId().equals(claimVO.getMemberId()))
            throw new TargetNotFoundException("결제내역의 소유자가 아닙니다");
        List<PaymentDetailDto> list = paymentDao.selectPaymentDetailList(paymentNo);
        KakaoPayOrderRequestVO requestVO = new KakaoPayOrderRequestVO();
        requestVO.setTid(paymentDto.getPaymentTid());
        KakaoPayOrderResponseVO responseVO = kakaoPayService.order(requestVO);

        PaymentInfoVO infoVO = new PaymentInfoVO();
        infoVO.setPaymentDto(paymentDto);
        infoVO.setPaymentDetailList(list);
        infoVO.setResponseVO(responseVO);
        return infoVO;
    }

 // 매출 전표 조회 기능 수정 - memberId로 검색만 수행
    @GetMapping("/payments")
    public List<PaymentDto> getAllPayments(@RequestHeader("Authorization") String token,
                                           @RequestParam(required = false) String memberId) {
        tokenService.check(tokenService.removeBearer(token));

        Map<String, Object> params = new HashMap<>();
        if (memberId != null && !memberId.isEmpty()) {
            params.put("memberId", memberId);
        }

        return paymentDao.selectAllPayments(params);
    }


    // 총 매출 조회 기능 추가
    @GetMapping("/total-sales")
    public double getTotalSales(@RequestHeader("Authorization") String token,
                                @RequestParam(required = false) String startDate,
                                @RequestParam(required = false) String endDate) {
        tokenService.check(tokenService.removeBearer(token));

        Map<String, Object> params = new HashMap<>();
        if (startDate != null && !startDate.isEmpty()) {
            params.put("startDate", startDate);
        }
        if (endDate != null && !endDate.isEmpty()) {
            params.put("endDate", endDate);
        }

        return paymentDao.getTotalSales(params);
    }
}