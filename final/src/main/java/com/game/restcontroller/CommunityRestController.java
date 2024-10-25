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

import com.game.dao.CommunityDao;
import com.game.dto.CommunityDto;
@CrossOrigin(origins = "http://localhost:3000") 
@RestController
@RequestMapping("/community")
public class CommunityRestController {

	@Autowired
	private CommunityDao communityDao;
	
	// 게시글 목록 조회
	 @GetMapping("/")
	    public List<CommunityDto> getCommunityList() {
	        return communityDao.CommunityList();
	    }

	    // 게시글 등록 (POST 요청)
	    @PostMapping("/")
	    public void createCommunity(@RequestBody CommunityDto communityDto) {
	        communityDao.CommunityInsert(communityDto);
	    }

	    // 게시글 수정 (PUT 요청)
	    @PutMapping("/{communityNo}")
	    public void updateCommunity(@PathVariable int communityNo, @RequestBody CommunityDto communityDto) {
	    	communityDto.setCommunityNo(communityNo);
	        communityDao.CommunityUpdate(communityDto);
	    }

	    // 게시글 삭제 (DELETE 요청)
	    @DeleteMapping("/{communityNo}")
	    public void deleteCommunity(@PathVariable int communityNo) {
	        communityDao.CommunityDelete(communityNo);
	    }

//	    // 검색 기능 (GET 요청)
//	    @GetMapping("/column/{column}/keyword/{keyword}")
//	    public List<CommunityDto> searchCommunityList(@PathVariable String column, @PathVariable String keyword) {
//	        return communityDao.CommunitySearch(column, keyword);
//	    }
	    
	    @GetMapping("/search/title/{keyword}")
	    public List<CommunityDto> searchCommunityByTitle(@PathVariable String keyword) {
	        return communityDao.SearchByTitle(keyword);
	    }

	    
	    
	 // 게시글 상세 조회 (GET 요청)
	    @GetMapping("/{communityNo}")
	    public CommunityDto getCommunityDetail(@PathVariable int communityNo) {
	        return communityDao.CommunityDetail(communityNo);
	    }

//    // 게시글 조회수 증가
//    @PutMapping("/{communityNo}")
//    public String CommunityViews(@PathVariable int communityNo) {
//        communityService.CommunityViews(communityNo);
//        return "CommunityViews";
//    }
//
//    // 게시글 좋아요수 증가 
//    @PutMapping("/{communityNo}")
//    public String CommunityLikes(@PathVariable int communityNo) {
//        communityService.CommunityLikes(communityNo);
//        return "CommunityLikes";
//    }
//	 // 댓글 작성
//	    @PostMapping("/{communityNo}/comments")
//	    public String createComment(@PathVariable int communityNo, @RequestBody CommunityDto commentDto) {
//	        commentDto.setCommunityGroup(communityNo);  // 댓글 그룹 설정 (해당 게시글 번호로)
//	        communityDao.insertComment(commentDto);
//	        return "댓글이 등록되었습니다.";
//	    }
//
//	    // 댓글 수정
//	    @PutMapping("/comments/{communityNo}")
//	    public String updateComment(@PathVariable int communityNo, @RequestBody CommunityDto commentDto) {
//	        commentDto.setCommunityNo(communityNo);
//	        communityDao.updateComment(commentDto);
//	        return "댓글이 수정되었습니다.";
//	    }
//
//	    // 댓글 삭제
//	    @DeleteMapping("/comments/{communityNo}")
//	    public String deleteComment(@PathVariable int communityNo) {
//	        communityDao.deleteComment(communityNo);
//	        return "댓글이 삭제되었습니다.";
//	    }
//
//	    // 특정 게시글의 댓글 목록 조회
//	    @GetMapping("/{communityNo}/comments")
//	    public List<CommunityDto> getComments(@PathVariable int communityNo) {
//	        return communityDao.getCommentsByPostId(communityNo);
//	    }

	    
	    
}