package com.game.restcontroller;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.game.dao.WishListDao;
import com.game.dto.CartDto;
import com.game.dto.GameDto;
import com.game.dto.WishListDto;
import com.game.error.TargetNotFoundException;
import com.game.service.TokenService;
import com.game.vo.MemberClaimVO;


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
    
    @GetMapping("/column/{column}/keyword/{keyword}")
    public List<WishListDto> search(
    						@PathVariable String column,
    						@PathVariable String keyword
    													){
    	List<WishListDto> list= wishListDao.search(column, keyword);
    	return list;
    }
    
    @GetMapping("/")
    public List<WishListDto> list(@RequestHeader("Authorization") String token){
    	MemberClaimVO claimVO = tokenService.check(tokenService.removeBearer(token));
    	//String memberId="testuser123";
    	System.out.println(claimVO.getMemberId());
    	return wishListDao.selectListByMemberId(claimVO.getMemberId());
    	//return wishListDao.selectListByMemberId(memberId);
    }
//    @GetMapping("/")
//    public List<WishListDto>list(){
//    	return wishListDao.list();
//    }
//    @GetMapping("/")
//    public List<WishListDto> list(@RequestHeader("Authorization") String token){
//        MemberClaimVO claimVO = tokenService.check(tokenService.removeBearer(token));
//        return wishListDao.selectListByMemberId(claimVO.getMemberId());
//    }
    
//    @PostMapping("/add")
//	public WishListDto insert(@RequestBody GameDto gameDto,
//												@RequestBody WishListDto wishListDto) {
//    	String memberId = "testuser123";
//    	int gameNo = gameDto.getGameNo();
//    	
//    	wishListDto.setMemberId(memberId);
//    	gameDto.setGameNo(gameNo);
//    	wishListDao.insert(memberId,gameNo);
//    	
//    	return wishListDto;
//    }
    
    @PostMapping("/add")
    public WishListDto addToWishList(@RequestHeader("Authorization") String token,
    															@RequestBody WishListDto wishListDto) {
    	
        MemberClaimVO claimVO = tokenService.check(tokenService.removeBearer(token));
        String memberId = claimVO.getMemberId();
        int gameNo = wishListDto.getGameNo();
        
        System.out.println("wishmemberId="+memberId);
        System.out.println("wishgameNo="+gameNo);
        // 장바구니에 추가
        wishListDto.setMemberId(memberId);
        wishListDto.setGameNo(gameNo);
        wishListDao.insert(memberId, gameNo);

        return wishListDto;
    }
    
    @DeleteMapping("/{wishListId}")
    public void delete(@PathVariable int wishListId) {
    	boolean result = wishListDao.delete(wishListId);
    	 if(result == false) {
         	throw new TargetNotFoundException("존재하지 않는 게임정보");
         }
    }
}