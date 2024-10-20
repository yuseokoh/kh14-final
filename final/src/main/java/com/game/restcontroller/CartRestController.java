package com.game.restcontroller;

import com.game.dao.CartDao;
import com.game.dao.GameDao;
import com.game.dto.CartDto;
import com.game.dto.GameDto;
import com.game.error.TargetNotFoundException;
import com.game.service.TokenService;
import com.game.vo.MemberClaimVO;
import org.springframework.beans.factory.annotation.Autowired;
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

    //장바구니 조회
    @GetMapping("/{memberId}")
    public List<CartDto> getCartItems(@PathVariable String memberId, @RequestHeader("Authorization") String token) {
        MemberClaimVO claimVO = tokenService.check(tokenService.removeBearer(token));
        if (!memberId.equals(claimVO.getMemberId())) {
            throw new TargetNotFoundException();
        }
        return cartDao.listByMemberId(memberId);
    }

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
