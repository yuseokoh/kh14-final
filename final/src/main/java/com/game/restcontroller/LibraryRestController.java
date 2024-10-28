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
import org.springframework.web.bind.annotation.RestController;

import com.game.dao.LibraryDao;
import com.game.dto.LibraryDto;
import com.game.error.TargetNotFoundException;
import com.game.service.TokenService;
import com.game.vo.MemberClaimVO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/library")
public class LibraryRestController {

	@Autowired
	private LibraryDao libraryDao;
	
	@Autowired
	private MemberClaimVO claimVO;
	
	@Autowired
	private TokenService tokenService;
	
	@GetMapping("/")
	public List<LibraryDto> list(@RequestHeader("Authorization") String token){
		MemberClaimVO claimVO = tokenService.check(tokenService.removeBearer(token));
		return libraryDao.selectListByMemberId(claimVO.getMemberId());
	}
	
	@PostMapping("/add")
	public LibraryDto addToLibrary(@RequestHeader("Authorization") String token,
														@RequestBody LibraryDto libraryDto) {
		MemberClaimVO claimVO = tokenService.check(tokenService.removeBearer(token));
        String memberId = claimVO.getMemberId();
        int gameNo = libraryDto.getGameNo();
        
        libraryDto.setMemberId(memberId);
        libraryDto.setGameNo(gameNo);
        
        return libraryDto;
	}
	
	@DeleteMapping("/{libraryId}")
	public void delete(@PathVariable int libraryId) {
		boolean result = libraryDao.delete(libraryId);
		if(result == false) {
			throw new TargetNotFoundException("존재하지않는 게임정보");
		}
	}
}
