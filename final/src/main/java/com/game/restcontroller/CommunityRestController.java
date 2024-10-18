package com.game.restcontroller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.game.dto.CommunityDto;
import com.game.service.CommunityService;

@RestController
@RequestMapping("/community")
public class CommunityRestController {

	@Autowired
	private CommunityService communityService;
	
	//게시글 목록
	@GetMapping("/list")
    public List<CommunityDto> CommunityList() {
        return communityService.CommunityList();
    }
	// 게시글 등록
    @PostMapping("/insert")
    public String CommunityInsert(@RequestBody CommunityDto communityDto) {
        communityService.CommunityInsert(communityDto);
        return "CommunitySuccess";
    }
    
    // 게시글 삭제
    @DeleteMapping("/delete")
    public String CommunityDelete(@PathVariable int communityNo) {
        communityService.CommunityDelete(communityNo);
        return "CommunityDelete";
    }

    // 게시글 수정
    @PutMapping("/update")
    public String CommunityUpdate(@RequestBody CommunityDto communityDto) {
        communityService.CommunityUpdate(communityDto);
        return "CommunityUpdate";
    }

    // 게시글 검색
    @GetMapping("/search")
    public List<CommunityDto> CommunitySearch(@RequestParam("column") String column, 
    																	@RequestParam("keyword") String keyword) {
        return communityService.CommunitySearch(column, keyword);
    }

    // 게시글 조회수 증가
    @PutMapping("/views")
    public String CommunityViews(@PathVariable int communityNo) {
        communityService.CommunityViews(communityNo);
        return "CommunityViews";
    }

    // 게시글 좋아요수 증가 
    @PutMapping("/likes")
    public String CommunityLikes(@PathVariable int communityNo) {
        communityService.CommunityLikes(communityNo);
        return "CommunityLikes";
    }
}