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
    
    @PostMapping("/")
	public void insert(@RequestBody WishListDto wishListDto) {
    	wishListDao.insert(wishListDto);
    }
    
    @DeleteMapping("/{wishListId}")
    public void delete(@PathVariable int wishListId) {
    	wishListDao.delete(wishListId);
    }
}