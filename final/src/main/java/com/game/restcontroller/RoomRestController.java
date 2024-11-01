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

import com.game.dao.RoomDao;
import com.game.dto.RoomDto;
import com.game.dto.RoomMemberDto;
import com.game.error.TargetNotFoundException;
import com.game.service.TokenService;
import com.game.vo.MemberClaimVO;

@CrossOrigin(origins ="http://localhost:3000")
@RestController
@RequestMapping("/room")
public class RoomRestController {
	
	@Autowired
	private RoomDao roomDao;
	@Autowired
	private TokenService tokenService;
	
	
	@PostMapping("/")
	public RoomDto insert(@RequestBody RoomDto roomDto) {
		int roomNo = roomDao.sequence();
		roomDto.setRoomNo(roomNo);
		roomDao.insert(roomDto);
		return roomDao.selectOne(roomNo);
	}
	
	@GetMapping("/")
	public List<RoomDto> list(){
		return roomDao.selectList();
	}
	
	@DeleteMapping("/{roomNo}")
	public void delete(@PathVariable int roomNo) {
		roomDao.delete(roomNo);
	}
	
	@PostMapping("/enter")
	public void enter(@RequestBody RoomMemberDto roomMemberDto,
								@RequestHeader("Authorization") String token) {
		RoomDto roomDto = roomDao.selectOne(roomMemberDto.getRoomNo());
		if(roomDto == null) throw new TargetNotFoundException("존재하지 않는 방");
		
		MemberClaimVO claimVO = tokenService.check(tokenService.removeBearer(token));
		roomMemberDto.setMemberId(claimVO.getMemberId());
		roomDao.enter(roomMemberDto);
	}
	
	@PostMapping("/leave")
	public void leave(@RequestBody RoomMemberDto roomMemberDto,
			@RequestHeader("Authorization") String token) {
		RoomDto roomDto = roomDao.selectOne(roomMemberDto.getRoomNo());
		if(roomDto == null) throw new TargetNotFoundException("존재하지 않는 방");
		
		MemberClaimVO claimVO = tokenService.check(tokenService.removeBearer(token));
		roomMemberDto.setMemberId(claimVO.getMemberId());
		roomDao.leave(roomMemberDto);
	}
	@GetMapping("/check/{roomNo}")
	public boolean check(@PathVariable int roomNo,
							@RequestHeader("Authorization") String token) {
		MemberClaimVO claimVO = tokenService.check(tokenService.removeBearer(token));
		 
		RoomMemberDto roomMemberDto = new RoomMemberDto();
		roomMemberDto.setMemberId(claimVO.getMemberId());
		roomMemberDto.setRoomNo(roomNo);
		boolean canEnter = roomDao.check(roomMemberDto);
		
		return canEnter;
	}
}
