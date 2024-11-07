package com.game.restcontroller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.game.dao.MemberDao;
import com.game.dao.ReplyDao;
import com.game.dto.MemberDto;
import com.game.dto.ReplyDto;
import com.game.error.TargetNotFoundException;
import com.game.service.TokenService;
import com.game.vo.MemberClaimVO;
import com.game.vo.ReplyComplexRequestVO;
import com.game.vo.ReplyComplexResponseVO;

@CrossOrigin
@RestController
@RequestMapping("/reply")
public class ReplyRestController {
	
	@Autowired
	private MemberDao memberDao;

	@Autowired
	private ReplyDao replyDao;
	
	@Autowired
	private TokenService tokenService;
	
	
	//댓글 목록 무한스크롤
		@PostMapping("/list")
		public ReplyComplexResponseVO list(
				@RequestBody ReplyComplexRequestVO vo){
			int count = replyDao.count(vo);
			//마지막 == 페이징 안쓰는 경우 
			boolean last = vo.getEndRow() == null || count <= vo.getEndRow();
			
			ReplyComplexResponseVO response = new ReplyComplexResponseVO();
			response.setReplyList(replyDao.list(vo));
			response.setCount(count);
			response.setLast(last);
			return response;
		}
	
	
	
	
//	//댓글 목록
//	@GetMapping("/{replyOrigin}")
//	public List<ReplyDto> list(@PathVariable int replyOrigin){
//		return replyDao.list(replyOrigin);
//	}
	
	//댓글 등록
	@PostMapping("/insert/{communityNo}")
	public void add(@PathVariable int communityNo, @RequestBody ReplyDto replyDto,@RequestHeader("Authorization") String accessToken) {
		//step 1 : 시퀀스 번호를 생성한다
		int seq = replyDao.sequence();
		
		//step 2 : 작성자 정보를 불러온다 //clamVo에 있는 코드보고
		//String memberId = "testuser123"; //바꾸는 작업할 때 빼야한다---------------------이 구문만 memberid생겨서
//		 if(!tokenService.isBearerToken(Authorization)) {
//	            throw new TargetNotFoundException("로그인이 필요합니다.");
//	        }
//	        
//	        // 토큰에서 회원 정보 추출
//	        MemberClaimVO claimVO = tokenService.check(tokenService.removeBearer(Authorization));
//	        String memberId = claimVO.getMemberId();
		MemberClaimVO claimVO = tokenService.check(tokenService.removeBearer(accessToken));
		// 유효 검증
		MemberDto memberDto = memberDao.selectOne(claimVO.getMemberId());
		if(memberDto == null) throw new TargetNotFoundException("존재하지 않는 회원");
//		
		//step 3 : 정보를 설정한다
		replyDto.setReplyNo(seq);
		replyDto.setReplyOrigin(communityNo);
		replyDto.setReplyWriter(claimVO.getMemberId());
		//(+추가) 새글, 답글 여부에 따라 그룹, 상위글, 차수를 설정해야한다
		if(replyDto.isNew()) {
			replyDto.setReplyGroup(seq);//그룹번호는 글번호와 동일
//			replyDto.setReplyTarget(null)//상위글번호는 null로 설정(생략가능)
			replyDto.setReplyDepth(0);//차수는 0으로 설정(생략가능)
		}
		else {
			//타겟글의 정보 조회
			ReplyDto targetDto = replyDao.selectOne(replyDto.getReplyTarget());
			replyDto.setReplyGroup(targetDto.getReplyGroup());//그룹번호는 원본글과 동일하게 설정
			//boardDto.setBoardTarget(targetDto.getBoardNo());//상위글번호는 원본글 글번호로 설정(생략)
			replyDto.setReplyDepth(targetDto.getReplyDepth()+1);//차수는 원본글 차수+1로 설정
		}
		
		replyDao.insert(replyDto);
		
		// 게시글에 댓글수 갱신 기능 넣을거면 communityDao에 기능 추가 필요
	}
	
	//댓글 수정---------------------------이게 맞나?
	@PutMapping("/{replyNo}") //, @RequestHeader("Authorization") String accessToken
	public void update(@PathVariable int replyNo, @RequestBody ReplyDto replyDto) {
//		MemberClaimVO claimVO = tokenService.check(tokenService.removeBearer(accessToken));
//		 String memberId = claimVO.getMemberId();

		    // 댓글 작성자 검증
//		    ReplyDto originalReply = replyDao.selectOne(replyNo); // 수정할 댓글 정보 가져오기
//		    if (!originalReply.getReplyWriter().equals(memberId)) {
//		        throw new TargetNotFoundException("수정 권한이 없습니다.");
//		    }
		
			replyDto.setReplyNo(replyNo);
			replyDao.update(replyDto);
	}
	
	//댓글 삭제--------------이게 맞나?
	@DeleteMapping("/{replyNo}")
	public void delete(@PathVariable int replyNo) {
//		MemberClaimVO claimVO = tokenService.check(tokenService.removeBearer(accessToken ));
//		String memberId = claimVO.getMemberId();
		
//		ReplyDto originalReply = replyDao.selectOne(replyNo); // 삭제할 댓글 정보 가져오기
//	    if (!originalReply.getReplyWriter().equals(memberId)) {
//	        throw new TargetNotFoundException("삭제 권한이 없습니다.");
//	    }
		replyDao.delete(replyNo);
	}
	
	
}