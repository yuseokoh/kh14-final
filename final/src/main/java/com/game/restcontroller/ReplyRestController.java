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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.game.dao.ReplyDao;
import com.game.dto.ReplyDto;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/reply")
public class ReplyRestController {

	@Autowired
	private ReplyDao replyDao;
	
	//댓글 목록
	@GetMapping("/")
	public List<ReplyDto>list(){
		return replyDao.list();
	}
	
	//댓글 등록
	@PostMapping("/")
	public void add(@RequestBody ReplyDto replyDto) {
		//step 1 : 시퀀스 번호를 생성한다
		int seq = replyDao.sequence();
		
		//step 2 : 작성자 정보를 불러온다
		String memberId = "testuser123";
		
		//step 3 : 정보를 설정한다
		replyDto.setReplyNo(seq);
		replyDto.setReplyWriter(memberId);
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
	
	//댓글 수정
	@PutMapping("/{replyNo}")
	public void update(@PathVariable int replyNo, @RequestBody ReplyDto replyDto) {
		replyDto.setReplyNo(replyNo);
		replyDao.update(replyDto);
	}
	
	//댓글 삭제
	@DeleteMapping("/{replyNo}")
	public void delete(@PathVariable int replyNo) {
		replyDao.delete(replyNo);
	}
	
	
}