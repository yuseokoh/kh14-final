package com.game.restcontroller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.game.dao.WishListDao;
import com.game.dto.WishListDto;
import com.game.service.TokenService;
import com.game.vo.MemberClaimVO;


@CrossOrigin
@RestController
@RequestMapping("/wishlist")
public class WishListRestController {

	@Autowired
	private WishListDao wishListDao;
	
	@Autowired
	private MemberClaimVO memberClaimVO;
	
	@Autowired
	private TokenService tokenService;
	
	@GetMapping("/")
	public List<WishListDto> list(@RequestHeader("Authorization") String token){
		//public List<WishListDto> list(){
		MemberClaimVO claimVO = tokenService.check(tokenService.removeBearer(token));
		return wishListDao.selectList();
	}

}
