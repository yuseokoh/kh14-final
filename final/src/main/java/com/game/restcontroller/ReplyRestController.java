package com.game.restcontroller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.game.dao.CommunityDao;
import com.game.dao.ReplyDao;
import com.game.dto.ReplyDto;
import com.game.error.TargetNotFoundException;
import com.game.vo.ReplyListVO;

import jakarta.servlet.http.HttpSession;

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
		replyDao.insert(replyDto);
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