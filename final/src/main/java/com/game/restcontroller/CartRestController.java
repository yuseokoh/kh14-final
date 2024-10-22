package com.game.restcontroller;

import com.game.dao.CartDao;
import com.game.dao.GameDao;
import com.game.dto.CartDto;
import com.game.dto.GameDto;
import com.game.error.TargetNotFoundException;
import com.game.service.TokenService;
import com.game.vo.MemberClaimVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cart")
@CrossOrigin
public class CartRestController {

    @Autowired
    private CartDao cartDao;

    @Autowired
    private GameDao gameDao;
    
    @Autowired
    private TokenService tokenService;

    
    @GetMapping("/")
    @Transactional(readOnly = true)
    public List<CartDto> getCartItems() {
        // 토큰에서 사용자 정보 추출
//        MemberClaimVO claimVO = tokenService.check(tokenService.removeBearer(token));
    	String memberId ="testuser123";

        // 해당 사용자의 장바구니 목록 반환
    	System.out.println("Member ID: " + memberId);
        return cartDao.listByMemberId(memberId);
    }


//    @GetMapping("/")
//    public List<CartDto> list(){
//    	return cartDao.list();
//    }
    // 장바구니 추가
    @PostMapping("/")
    public CartDto addCartItem(@RequestBody CartDto cartDto,@RequestParam String gameTitle, @RequestHeader("Authorization") String token) {
        MemberClaimVO claimVO = tokenService.check(tokenService.removeBearer(token));
        cartDto.setMemberId(claimVO.getMemberId());
        
        int gameNo = cartDao.findGameNo(gameTitle);
        cartDto.setGameNo(gameNo);
        return cartDto;
    }

    // 장바구니 삭제
    @DeleteMapping("/{cartId}")
    public void deleteCartItem(@PathVariable int cartId, @RequestHeader("Authorization") String token) {
        MemberClaimVO claimVO = tokenService.check(tokenService.removeBearer(token));
        boolean result = cartDao.delete(cartId);
    }
}
