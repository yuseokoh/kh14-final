package com.game.restcontroller;

import com.game.dao.CartDao;
import com.game.dao.GameDao;
import com.game.dto.CartDto;
import com.game.dto.GameDto;
import com.game.dto.WishListDto;
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

    public List<CartDto> list(@RequestHeader("Authorization") String token) {
    	MemberClaimVO claimVO = tokenService.check(tokenService.removeBearer(token));
    	//String memberId ="testuser123";
    	System.out.println("memberId="+claimVO.getMemberId());
        return cartDao.listByMemberId(claimVO.getMemberId());

    }

//    @GetMapping("/")
//    public List<CartDto> list(){
//    	return cartDao.list();
//    }
    @PostMapping("/")
    public CartDto addToCartFromWishlist(
        @RequestHeader("Authorization") String token,		
        @RequestBody WishListDto wishListDto) {

        MemberClaimVO claimVO = tokenService.check(tokenService.removeBearer(token));
        String memberId = claimVO.getMemberId();
        int gameNo = wishListDto.getGameNo();
        
        // 로그로 데이터 확인
        System.out.println("Member ID: " + memberId);
        System.out.println("Game No: " + gameNo);

        // 장바구니에 추가
        CartDto cartDto = new CartDto();
        cartDto.setMemberId(memberId);
        cartDto.setGameNo(gameNo);
        cartDao.addToCart(memberId, gameNo);

        return cartDto;
    }


    // 장바구니 삭제
    @DeleteMapping("/{cartId}")
    public void delete(@PathVariable int cartId) {
        boolean result = cartDao.delete(cartId);
        if(result == false) {
        	throw new TargetNotFoundException("존재하지 않는 게임정보");
        }
    }
}
