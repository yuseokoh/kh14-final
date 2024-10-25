package com.game.restcontroller;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.game.dao.WishListDao;
import com.game.dto.WishListDto;
import com.game.service.TokenService;
import com.game.vo.MemberClaimVO;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Slf4j
@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/wishlist")
public class WishListRestController {
    
    private static final Logger logger = LoggerFactory.getLogger(WishListRestController.class);
    
    @Autowired
    private WishListDao wishListDao;
    
    @Autowired
    private MemberClaimVO memberClaimVO;
    
    @Autowired
    private TokenService tokenService;
    
    @GetMapping("/{memberId}")
    public List<WishListDto> list(@RequestHeader("Authorization") String token){
//        logger.info("Received token: {}", token);
        
        MemberClaimVO claimVO = tokenService.check(tokenService.removeBearer(token));
        
        String memberId = claimVO.getMemberId();
//        logger.info("Member Claim: {}", claimVO);
        
        List<WishListDto> wishList = wishListDao.selectListByMemberId(memberId);
//        logger.info("Wish List Size: {}", wishList.size());
        
        return wishList;
    }
    
    @PostMapping("/")
	public void insert(@RequestBody WishListDto wishListDto) {
    	wishListDao.insert(wishListDto);
    }
    
    @DeleteMapping("/")
    public void delete(@PathVariable int wishListID) {
    	wishListDao.delete(wishListID);
    }
}