package com.game.restcontroller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.game.dao.FriendDao;
import com.game.dto.FriendDto;
import com.game.dto.MemberDto;
import com.game.error.TargetNotFoundException;
import com.game.service.TokenService;
import com.game.vo.MemberClaimVO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/friend")
public class FriendRestController {
	
	@Autowired
	private FriendDao friendDao;
	@Autowired
	private TokenService tokenService;
	
	@GetMapping("/member") //친구 추가를 위한 회원 검색 시스템
	public List<MemberDto> list(){
		return friendDao.memberList();
	}
	
	@PostMapping("/") //친구신청
	public FriendDto request(@RequestBody FriendDto friendDto, @RequestHeader("Authorization") String token) {
		MemberClaimVO claimVO = tokenService.check(tokenService.removeBearer(token));
		int friendFk = friendDao.sequence();
		friendDto.setFriendFk(friendFk);
		friendDto.setFriendFrom(claimVO.getMemberId());
		friendDao.request(friendDto);
		
		return friendDao.selectOne(friendFk);
	}
	@GetMapping("/request/{memberId}") //보낸 요청
	public List<FriendDto> requestList(@PathVariable String memberId
			, @RequestHeader("Authorization") String token
			){
	   MemberClaimVO claimVO = tokenService.check(tokenService.removeBearer(token));
	   if(!memberId.equals(claimVO.getMemberId())) {
		   throw new TargetNotFoundException();
	   };
	   return friendDao.requestToOther(memberId);
	}
	
	@GetMapping("/getRequest/{memberId}")//받은 요청
	public List<FriendDto> getRequest(@PathVariable String memberId, @RequestHeader("Authorization") String token){
		MemberClaimVO claimVO = tokenService.check(tokenService.removeBearer(token));
		if(!memberId.equals(claimVO.getMemberId())) {
			throw new TargetNotFoundException();
		};
		return friendDao.requestFromOther(memberId);
	}
	
	@DeleteMapping("/{friendFk}")
	public void delete(@PathVariable int friendFk) {
		boolean result = friendDao.requestDelete(friendFk);
		if(result == false) {
			throw new TargetNotFoundException();
		}
	}
	
	@GetMapping("/{memberId}") //친구 목록
	public List<FriendDto> friendList(@PathVariable String memberId, @RequestHeader("Authorization") String token){
		MemberClaimVO claimVO = tokenService.check(tokenService.removeBearer(token));
		if(!memberId.equals(claimVO.getMemberId())) {
			throw new TargetNotFoundException();
		};
		return friendDao.friendList(memberId);
	}
	
	//수락
	@PutMapping("/{friendFk}")
	public void getRequest(@PathVariable int friendFk) {
		boolean result = friendDao.getRequest(friendFk);
		if(result == false) {
			throw new TargetNotFoundException();
		}
	}
	

}
