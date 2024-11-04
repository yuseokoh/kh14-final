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
import com.game.dao.RoomMessageDao;
import com.game.dto.RoomDto;
import com.game.dto.RoomMemberDto;
import com.game.error.TargetNotFoundException;
import com.game.service.TokenService;
import com.game.vo.MemberClaimVO;
import com.game.vo.WebsocketMessageMoreVO;
import com.game.vo.WebsocketMessageVO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@CrossOrigin(origins ="http://localhost:3000")
@RestController
@RequestMapping("/room")
public class RoomRestController {
	
	@Autowired
	private RoomDao roomDao;
	@Autowired
	private RoomMessageDao roomMessageDao;
	@Autowired
	private TokenService tokenService;
	
	
	@PostMapping("/")
	public RoomDto insert(@RequestBody RoomDto roomDto) {
		int roomNo = roomDto.getRoomNo();
		roomDao.insert(roomDto);
		return roomDao.selectOne(roomNo);
	}
	
	@GetMapping("/")
	public List<RoomDto> list(){
		return roomDao.selectList();
	}
	
	
	
	
	@GetMapping("/member")
	public List<RoomDto> listByMember(@RequestHeader("Authorization") String token) {
	    MemberClaimVO claimVO = tokenService.check(tokenService.removeBearer(token));
	    return roomDao.selectList(claimVO.getMemberId());
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
		
		boolean isEnter = roomDao.check(roomMemberDto);
	    if (isEnter) {
	        throw new IllegalStateException("이미 방에 입장한 사용자입니다.");
	    }
	    
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
	@GetMapping("/more/{firstMessageNo}")
    public WebsocketMessageMoreVO more(
            @RequestHeader(required = false, value = "Authorization") String token,
            @PathVariable int firstMessageNo) {
        
        String memberId = null; // 처음에는 비회원이라고 가정
        if (token != null) { // 토큰이 있으면 사용자 정보 가져오기
            MemberClaimVO claimVO = tokenService.check(tokenService.removeBearer(token));
            memberId = claimVO.getMemberId();
        }
        
        // 사용자에게 보내줄 메시지 목록 조회
        List<WebsocketMessageVO> messageList = roomMessageDao.selectListMemberComplete(
                memberId, 1, 100, firstMessageNo);
        
        if (messageList.isEmpty()) {
            throw new TargetNotFoundException("보여줄 메시지 없음");
        }
        
        // 남은 메시지가 있는지 확인
        List<WebsocketMessageVO> prevMessageList = roomMessageDao.selectListMemberComplete(
                memberId, 1, 100, messageList.get(0).getNo());
        
        // 반환값 생성
        WebsocketMessageMoreVO moreVO = new WebsocketMessageMoreVO();
        moreVO.setMessageList(messageList);
        
        moreVO.setLast(prevMessageList.isEmpty());
        
        return moreVO;
    }
	
}
