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
import com.game.dao.MemberDao;
import com.game.dto.CommunityDto;
import com.game.service.TokenService;
import com.game.vo.CommunityComplexRequestVO;
import com.game.vo.CommunityComplexResponseVO;
@CrossOrigin(origins = "http://localhost:3000") 
@RestController
@RequestMapping("/community")
public class CommunityRestController {

	@Autowired
	private CommunityDao communityDao;
	@Autowired
	private TokenService tokenService;
	@Autowired
	private MemberDao memberDao;
	
	
	
//	//검색 무한스크롤
//	@PostMapping("/search")
//	public CommunityComplexResponseVO search(
//			@RequestBody CommunityComplexRequestVO vo){
//		
//		int count = communityDao.complexSearchCount(vo);
//		//마지막 == 페이징 안쓰는 경우 
//		boolean last = vo.getEndRow() == null || count <= vo.getEndRow();
//		
//		CommunityComplexResponseVO response = new CommunityComplexResponseVO();
//		response.setCommunityList(communityDao.complexSearch(vo));
//		response.setCount(count);
//		response.setLast(last);
//		return response;
//	}
//	
	
	
	
	
	
	
	
	
	
	
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
	    
	    //검색기능
	    @GetMapping("/search/title/{keyword}")
	    public List<CommunityDto> searchCommunityByTitle(@PathVariable String keyword) {
	        return communityDao.SearchByTitle(keyword);
	    }

	    
	    
	 // 게시글 상세 조회 (GET 요청)
	    @GetMapping("/{communityNo}")
	    public CommunityDto getCommunityDetail(@PathVariable int communityNo) {
	        return communityDao.CommunityDetail(communityNo);
	    }
	    
	    
	    //명호형이만든거
//	    @PostMapping("/list") // 회원가입과 구분하기 위해 주소 규칙을 깬다
//		public CommunityComplexResponseVO search(@RequestBody CommunityComplexRequestVO vo){
//			
//			int count = communityDao.countWithPaging(vo);
//			boolean last = vo.getEndRow() == null || count <= vo.getEndRow();
//			CommunityComplexResponseVO response = new CommunityComplexResponseVO();
//			response.setCommunityList(communityDao.selectListByPaging(vo));
//			response.setCount(count);
//			response.setLast(last);
//			return response;
//		}
	    

	    
	    
}