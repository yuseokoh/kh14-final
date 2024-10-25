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
	    public String createCommunity(@RequestBody CommunityDto communityDto) {
	        communityDao.CommunityInsert(communityDto);
	        return "게시글이 등록되었습니다.";
	    }

	    // 게시글 수정 (PUT 요청)
	    @PutMapping("/")
	    public String updateCommunity(@RequestBody CommunityDto communityDto) {
	        communityDao.CommunityUpdate(communityDto);
	        return "게시글이 수정되었습니다.";
	    }

	    // 게시글 삭제 (DELETE 요청)
	    @DeleteMapping("/{communityNo}")
	    public String deleteCommunity(@PathVariable int communityNo) {
	        communityDao.CommunityDelete(communityNo);
	        return "게시글이 삭제되었습니다.";
	    }

	    // 검색 기능 (GET 요청)
	    @GetMapping("/column/{column}/keyword/{keyword}")
	    public List<CommunityDto> searchCommunityList(@PathVariable String column, @PathVariable String keyword) {
	        return communityDao.CommunitySearch(column, keyword);
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
}