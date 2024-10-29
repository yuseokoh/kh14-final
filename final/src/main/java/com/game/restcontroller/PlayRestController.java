package com.game.restcontroller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.game.dao.PlayDao;
import com.game.dto.PlayDto;
import com.game.service.TokenService;
import com.game.vo.MemberClaimVO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/play")
public class PlayRestController {
	
	@Autowired
	private PlayDao playDao;
	@Autowired
	private TokenService tokenService;
	
	@PostMapping//점수 저장
	public PlayDto insert(@RequestBody PlayDto playDto, @RequestHeader("Authorization") String token) {
		String tokenValue = tokenService.removeBearer(token);
        MemberClaimVO claimVO = tokenService.check(tokenValue);
		int playNo = playDao.sequence();
		playDto.setPlayNo(playNo);
		playDto.setMemberId(claimVO.getMemberId());
		playDao.insert(playDto);
		
		return playDto;
	}
}
