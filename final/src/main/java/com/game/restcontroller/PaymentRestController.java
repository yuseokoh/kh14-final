package com.game.restcontroller;

import java.net.URISyntaxException;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.game.dao.PaymentDao;
import com.game.dto.PaymentDetailDto;
import com.game.dto.PaymentDto;
import com.game.error.TargetNotFoundException;
import com.game.service.KakaoPayService;
import com.game.service.TokenService;
import com.game.vo.MemberClaimVO;
import com.game.vo.PaymentInfoVO;
import com.game.vo.pay.KakaoPayApproveRequestVO;
import com.game.vo.pay.KakaoPayApproveResponseVO;
import com.game.vo.pay.KakaoPayCancelRequestVO;
import com.game.vo.pay.KakaoPayCancelResponseVO;
import com.game.vo.pay.KakaoPayOrderRequestVO;
import com.game.vo.pay.KakaoPayOrderResponseVO;
import com.game.vo.pay.KakaoPayReadyRequestVO;
import com.game.vo.pay.KakaoPayReadyResponseVO;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/kakaopay")
public class PaymentRestController {
	@Autowired
    private KakaoPayService kakaoPayService;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private PaymentDao paymentDao;
    
    

    @PostMapping("/ready")
    public KakaoPayReadyResponseVO ready(@RequestBody KakaoPayReadyRequestVO request,
                                         @RequestHeader("Authorization") String token) throws URISyntaxException {
        request.setPartnerOrderId(UUID.randomUUID().toString());
        MemberClaimVO claimVO = tokenService.check(tokenService.removeBearer(token));
        request.setPartnerUserId(claimVO.getMemberId());

       
        KakaoPayReadyResponseVO response = kakaoPayService.ready(request);
        
        return response;
    }

    @PostMapping("/approve")
    public KakaoPayApproveResponseVO approve(@RequestHeader("Authorization") String token,
                                             @RequestBody KakaoPayApproveRequestVO request) throws URISyntaxException {
        MemberClaimVO claimVO = tokenService.check(tokenService.removeBearer(token));
        request.setPartnerUserId(claimVO.getMemberId());
        KakaoPayApproveResponseVO response = kakaoPayService.approve(request);
        return response;
    }

    @GetMapping("/order/{tid}")
    public KakaoPayOrderResponseVO order(@PathVariable String tid) throws URISyntaxException {
        KakaoPayOrderRequestVO request = new KakaoPayOrderRequestVO();
        request.setTid(tid);
        return kakaoPayService.order(request);
    }
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
    @Transactional
    @DeleteMapping("/cancelAll/{paymentNo}")
    public KakaoPayCancelResponseVO cancelAll(@PathVariable int paymentNo,
                                              @RequestHeader("Authorization") String token) throws URISyntaxException {
        PaymentDto paymentDto = paymentDao.selectPayment(paymentNo);
        if (paymentDto == null) throw new TargetNotFoundException("존재하지 않는 결제정보");
        MemberClaimVO claimVO = tokenService.check(tokenService.removeBearer(token));
        if (!paymentDto.getPaymentMemberId().equals(claimVO.getMemberId()))
            throw new TargetNotFoundException("소유자 불일치");
        if (paymentDto.getPaymentRemain() == 0)
            throw new TargetNotFoundException("이미 취소된 결제");

        KakaoPayCancelRequestVO request = new KakaoPayCancelRequestVO();
        request.setTid(paymentDto.getPaymentTid());
        request.setCancelAmount(paymentDto.getPaymentRemain());
        KakaoPayCancelResponseVO response = kakaoPayService.cancel(request);

        paymentDao.cancelPayment(paymentNo);
        paymentDao.cancelPaymentDetail(paymentNo);

        return response;
    }
    @DeleteMapping("/cancelItem/{paymentDetailNo}")
    public KakaoPayCancelResponseVO cancelItem(@RequestHeader("Authorization") String token,
                                               @PathVariable int paymentDetailNo) throws URISyntaxException {
        PaymentDetailDto paymentDetailDto = paymentDao.selectPaymentDetail(paymentDetailNo);
        if (paymentDetailDto == null) throw new TargetNotFoundException("존재하지 않는 결제정보");

        PaymentDto paymentDto = paymentDao.selectPayment(paymentDetailDto.getPaymentDetailOrigin());
        if (paymentDto == null) throw new TargetNotFoundException("존재하지 않는 결제정보");

        MemberClaimVO claimVO = tokenService.check(tokenService.removeBearer(token));
        if (!paymentDto.getPaymentMemberId().equals(claimVO.getMemberId()))
            throw new TargetNotFoundException("소유자 불일치");

        int money = paymentDetailDto.getPaymentDetailPrice() * paymentDetailDto.getPaymentDetailQty();
        KakaoPayCancelRequestVO request = new KakaoPayCancelRequestVO();
        request.setTid(paymentDto.getPaymentTid());
        request.setCancelAmount(money);
        KakaoPayCancelResponseVO response = kakaoPayService.cancel(request);

        paymentDao.cancelPaymentDetail(paymentDetailNo);
        paymentDao.decreaseItemRemain(paymentDto.getPaymentNo(), money);

        return response;
    }
}