package com.game.restcontroller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
@CrossOrigin
@RestController
@RequestMapping("/play")
public class PlayRestController {
	
	@Autowired
	private PlayDao playDao;
	@Autowired
	private TokenService tokenService;
	
	@PostMapping("/")//점수 저장
	public PlayDto insert(@RequestBody PlayDto playDto, @RequestHeader("Authorization") String token) {
		MemberClaimVO claimVO = tokenService.check(tokenService.removeBearer(token));
		int playNo = playDao.sequence();
		
		playDto.setGameNo(107);
		playDto.setPlayNo(playNo);
//		playDto.setMemberId(playDto.getMemberId());

		
		playDao.insert(playDto);
		
		return playDto;
	}
	
	//랭킹
	@GetMapping("/level")
	public List<PlayDto> levelRanking(){
		return playDao.levelRanking();
	}
	@GetMapping("/score")
	public List<PlayDto> scoreRanking(){
		return playDao.scoreRanking();
	}
	@GetMapping("/level/{page}/{pageSize}")
	public List<PlayDto> levelRanking(@PathVariable int page, @PathVariable int pageSize){
		return playDao.levelRankingByPaging();
	}
	
	//아이디로 검색
	@GetMapping("/{keyword}")
	public List<PlayDto> idSearch(@PathVariable String keyword){
		List<PlayDto> list = playDao.idSearch(keyword);
		return list;
	}
	
}
