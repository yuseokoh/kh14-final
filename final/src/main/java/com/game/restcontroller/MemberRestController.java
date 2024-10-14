package com.game.restcontroller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.game.dao.MemberDao;
import com.game.dao.MemberTokenDao;
import com.game.dto.MemberDto;
import com.game.dto.MemberTokenDto;
import com.game.error.TargetNotFoundException;
import com.game.service.TokenService;
import com.game.vo.MemberClaimVO;
import com.game.vo.MemberComplexRequestVO;
import com.game.vo.MemberComplexResponseVO;
import com.game.vo.MemberLoginRequestVO;
import com.game.vo.MemberLoginResponseVO;

@CrossOrigin(origins = { "http://localhost:3000" })
@RestController
@RequestMapping("/member")
public class MemberRestController {

	@Autowired
	private MemberDao memberDao;
	@Autowired
	private TokenService tokenService;
	@Autowired
	private MemberTokenDao memberTokenDao;

	@PostMapping("/search")
	public MemberComplexResponseVO search(@RequestBody MemberComplexRequestVO vo) {
		int count = memberDao.complexSearchCount(vo);
		boolean last = vo.getEndRow() == null || count <= vo.getEndRow();

		MemberComplexResponseVO response = new MemberComplexResponseVO();
		response.setMemberList(memberDao.complexSearch(vo));
		response.setCount(count);
		response.setLast(last);
		return response;
	}

	@PostMapping("/login")
	public MemberLoginResponseVO login(@RequestBody MemberLoginRequestVO vo) {

		MemberDto memberDto = memberDao.selectOne(vo.getMemberId());
		if (memberDto == null) {
			throw new TargetNotFoundException("아이디 없음");
		}
		boolean isValid = vo.getMemberPw().equals(memberDto.getMemberPw());

		if (isValid) {
			MemberLoginResponseVO response = new MemberLoginResponseVO();

			response.setMemberId(memberDto.getMemberId());
			response.setMemberLevel(memberDto.getMemberLevel());
			MemberClaimVO claimVO = new MemberClaimVO();
			claimVO.setMemberId(memberDto.getMemberId());
			claimVO.setMemberLevel(memberDto.getMemberLevel());
			response.setAccessToken(tokenService.createAccessToken(claimVO));
			response.setRefreshToken(tokenService.createRefreshToken(claimVO));

			return response;
		} else {
			throw new TargetNotFoundException("비밀번호 불일치");
		}
	}

	@PostMapping("/refresh")
	public MemberLoginResponseVO refresh(
			@RequestHeader("Authorization") String refreshToken) {
		//[1] refreshToken이 없거나 Bearer로 시작하지 않으면 안됨
		if(refreshToken == null) 
			throw new TargetNotFoundException("토큰 없음");
		if(tokenService.isBearerToken(refreshToken) == false)
			throw new TargetNotFoundException("Bearer 토큰 아님");
		
		//[2] 토큰에서 정보를 추출
		MemberClaimVO claimVO = 
				tokenService.check(tokenService.removeBearer(refreshToken));
		if(claimVO.getMemberId() == null)
			throw new TargetNotFoundException("아이디 없음");
		if(claimVO.getMemberLevel() == null)
			throw new TargetNotFoundException("등급 없음");
		
		//[3] 토큰 발급 내역을 조회
		MemberTokenDto memberTokenDto = new MemberTokenDto();
		memberTokenDto.setTokenTarget(claimVO.getMemberId());
		memberTokenDto.setTokenValue(tokenService.removeBearer(refreshToken));
		MemberTokenDto resultDto = memberTokenDao.selectOne(memberTokenDto);
		if(resultDto == null)//발급내역이 없음 
			throw new TargetNotFoundException("발급 내역이 없음");
		
		//[4] 기존의 리프시 토큰 삭제
		memberTokenDao.delete(memberTokenDto);
		
		//[5] 로그인 정보 재발급
		MemberLoginResponseVO response = new MemberLoginResponseVO();
		response.setMemberId(claimVO.getMemberId());
		response.setMemberLevel(claimVO.getMemberLevel());
		response.setAccessToken(tokenService.createAccessToken(claimVO));//재발급
		response.setRefreshToken(tokenService.createRefreshToken(claimVO));//재발급
		return response;
	}

	@GetMapping("/find")
	public MemberDto find(@RequestHeader("Authorization") String accessToken) {
		if (tokenService.isBearerToken(accessToken) == false)
			throw new TargetNotFoundException("유효하지 않은 토큰");
		MemberClaimVO claimVO = tokenService.check(tokenService.removeBearer(accessToken));

		MemberDto memberDto = memberDao.selectOne(claimVO.getMemberId());
		if (memberDto == null)
			throw new TargetNotFoundException("존재하지 않는 회원");

		memberDto.setMemberPw(null);
		return memberDto;

	}

}
