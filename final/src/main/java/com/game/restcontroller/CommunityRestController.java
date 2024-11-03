package com.game.restcontroller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.game.dao.CommunityDao;
import com.game.dao.CommunityImageDao;
import com.game.dao.ReplyDao;
import com.game.dto.CommunityDto;
import com.game.dto.CommunityImageDto;
import com.game.dto.ReplyDto;
import com.game.error.TargetNotFoundException;
import com.game.service.AttachmentService;
import com.game.service.TokenService;
import com.game.vo.CommunityComplexRequestVO;
import com.game.vo.CommunityComplexResponseVO;

import io.swagger.v3.oas.annotations.Parameter;
@CrossOrigin(origins = "http://localhost:3000") 
@RestController
@RequestMapping("/community")
public class CommunityRestController {

	@Autowired
	private CommunityDao communityDao;
	@Autowired
	private TokenService tokenService;
	@Autowired
	private CommunityImageDao communityImageDao;
	@Autowired
	private ReplyDao replyDao;
	@Autowired
	private AttachmentService attachmentService;
	
	
	
	//파일첨부 들어가면서 전체 주석-----------------------------------------------------
	
	//검색 무한스크롤
	@PostMapping("/list") 
	public CommunityComplexResponseVO search(
			@RequestBody CommunityComplexRequestVO vo){
		
		int count = communityDao.CommunityCount(vo);
		//마지막 == 페이징 안쓰는 경우 
		boolean last = vo.getEndRow() == null || count <= vo.getEndRow();
		
		CommunityComplexResponseVO response = new CommunityComplexResponseVO();
		response.setCommunityList(communityDao.CommunitySearch(vo));
		response.setCount(count);
		response.setLast(last);
		return response;
	}
	
	// 게시글 목록 조회
//	 @GetMapping("/list")
//	    public List<CommunityDto> getCommunityList() {
//	        return communityDao.CommunityList();
//	    }

	    // 게시글 등록 (POST 요청)
//	    @PostMapping("/")
//	    public void createCommunity(@RequestBody CommunityDto communityDto) {
//	        communityDao.CommunityInsert(communityDto);
//	    }

	    // 게시글 수정 (PUT 요청)
//	    @PutMapping("/{communityNo}")
//	    public void updateCommunity(@PathVariable int communityNo, @RequestBody CommunityDto communityDto) {
//	    	communityDto.setCommunityNo(communityNo);
//	        communityDao.CommunityUpdate(communityDto);
//	    }

	    // 게시글 삭제 (DELETE 요청)
//	    @DeleteMapping("/{communityNo}")
//	    public void deleteCommunity(@PathVariable int communityNo) {
//	        communityDao.CommunityDelete(communityNo);
//	    }
	    
	    //검색기능
	    @GetMapping("/search/title/{keyword}")
	    public List<CommunityDto> searchCommunityByTitle(@PathVariable String keyword) {
	        return communityDao.SearchByTitle(keyword);
	    }

	    
	    
	 // 게시글 상세 조회 (GET 요청)
//	    @GetMapping("/{communityNo}")
//	    public CommunityDto getCommunityDetail(@PathVariable int communityNo) {
//	        return communityDao.CommunityDetail(communityNo);
//	    }
	    
	    
//	    //명호형이만든거
////	    @PostMapping("/list") // 회원가입과 구분하기 위해 주소 규칙을 깬다
////		public CommunityComplexResponseVO search(@RequestBody CommunityComplexRequestVO vo){
////			
////			int count = communityDao.countWithPaging(vo);
////			boolean last = vo.getEndRow() == null || count <= vo.getEndRow();
////			CommunityComplexResponseVO response = new CommunityComplexResponseVO();
////			response.setCommunityList(communityDao.selectListByPaging(vo));
////			response.setCount(count);
////			response.setLast(last);
////			return response;
////		}
//	    
//	    //댓글수 나오게 하는거 
	    @PostMapping("/insert/{communityNo}")
	    public void add(@PathVariable int communityNo, @RequestBody ReplyDto replyDto) {
	        int seq = replyDao.sequence();
	        replyDto.setReplyNo(seq);
	        replyDto.setReplyOrigin(communityNo);
	        replyDao.insert(replyDto);
	        communityDao.updateReplyCount(communityNo);  // 댓글 수 업데이트
	    }

	    @DeleteMapping("/reply/{replyNo}")
	    public void deleteReply(@PathVariable int replyNo) {
	        replyDao.delete(replyNo);
	    }
//	    
	
	    
//이미지 때문에 등록,수정,삭제,상세조회 바꾼 코드------------------------
	    
//	 // 게시글 등록 (파일 첨부 포함)
//	    @PostMapping("/")
//	    public void createCommunity(
//	            @RequestPart("community") CommunityDto communityDto,
//	            @RequestPart(value = "files", required = false) List<MultipartFile> files) throws IllegalStateException, IOException {
//	        communityDao.CommunityInsert(communityDto);
//	        
//	        if (files != null && !files.isEmpty()) {
//	            for (MultipartFile file : files) {
//	                int attachmentNo = AttachmentService.save(file);
//	                CommunityImageDto communityImageDto = new CommunityImageDto();
//	                communityImageDto.setCommunityNo(communityDto.getCommunityNo());
//	                communityImageDto.setAttachmentNo(attachmentNo);
//	                communityImageDao.insertCommunityImage(communityImageDto);
//	            }
//	        }
//	    }
//
//	    // 게시글 수정 (파일 첨부 업데이트 포함)
//	    @PutMapping("/{communityNo}")
//	    public void updateCommunity(
//	            @PathVariable int communityNo,
//	            @RequestPart("community") CommunityDto communityDto,
//	            @RequestPart(value = "files", required = false) List<MultipartFile> files) throws IllegalStateException, IOException {
//	        communityDto.setCommunityNo(communityNo);
//	        communityDao.CommunityUpdate(communityDto);
//
//	        // 기존 첨부 파일 삭제
//	        communityImageDao.deleteCommunityImages(communityNo);
//
//	        // 새로운 첨부 파일 추가
//	        if (files != null && !files.isEmpty()) {
//	            for (MultipartFile file : files) {
//	                int attachmentNo = AttachmentService.save(file);
//	                CommunityImageDto communityImageDto = new CommunityImageDto();
//	                communityImageDto.setCommunityNo(communityNo);
//	                communityImageDto.setAttachmentNo(attachmentNo);
//	                communityImageDao.insertCommunityImage(communityImageDto);
//	            }
//	        }
//	    }
//
//	    // 게시글 삭제 (첨부 파일 삭제 포함)
//	    @DeleteMapping("/{communityNo}")
//	    public void deleteCommunity(@PathVariable int communityNo) {
//	        // 커뮤니티 게시글 삭제
//	        communityDao.CommunityDelete(communityNo);
//
//	        // 첨부 파일 삭제
//	        List<CommunityImageDto> attachments = communityImageDao.selectByCommunityNo(communityNo);
//	        if (attachments != null && !attachments.isEmpty()) {
//	            for (CommunityImageDto attachment : attachments) {
//	                AttachmentService.delete(attachment.getAttachmentNo());
//	            }
//	        }
//
//	        // 첨부 파일 관계 삭제
//	        communityImageDao.deleteCommunityImages(communityNo);
//	    }
//	 // 특정 커뮤니티 글의 이미지 목록 조회
//	    @GetMapping("/images/{communityNo}")
//	    public List<CommunityImageDto> getCommunityImages(@PathVariable int communityNo) {
//	        return communityImageDao.selectByCommunityNo(communityNo);
//	    }
//	
//	 // 특정 게시글 상세 조회와 이미지 함께 가져오기
//	    @GetMapping("/{communityNo}")
//	    public CommunityComplexResponseVO getCommunityDetail(@PathVariable int communityNo) {
//	        CommunityDto community = communityDao.CommunityDetail(communityNo);
//	        List<CommunityImageDto> images = communityImageDao.selectByCommunityNo(communityNo);
//
//	        CommunityComplexResponseVO response = new CommunityComplexResponseVO();
//	        response.setCommunityDto(community);
//	        response.setImages(images);
//
//	        return response;
//	    }
//----------------------------------------------------------------------------DB에 들어가게 한 코드
	    
	    
	    
	    
	    
	    
	    //game이미지보고 만든거
	    @PostMapping("/") // 게시글 등록 및 이미지 첨부
	    public void insert(
	            @RequestPart("community") CommunityDto communityDto,
	            @RequestPart(value = "files", required = false) List<MultipartFile> files) 
	            throws IllegalStateException, IOException {
	        
	        // 1. 게시글 등록
	        communityDao.CommunityInsert(communityDto);
	        
	        // 2. 게시글 삽입 후 communityNo 값 설정 확인
	        if (communityDto.getCommunityNo() == 0) {
	            int generatedCommunityNo = communityDao.getLastInsertId(); // communityDao에 getLastInsertId 메서드 추가 필요
	            communityDto.setCommunityNo(generatedCommunityNo);
	        }
	        
	        // 3. 첨부 파일 저장 및 community_image 테이블에 추가
	        if (files != null && !files.isEmpty()) {
	            for (MultipartFile file : files) {
	                int attachmentNo = attachmentService.save(file); // 파일을 저장하고 attachmentNo 반환
	                
	                // 커뮤니티 이미지 정보 생성 및 삽입
	                CommunityImageDto communityImageDto = new CommunityImageDto();
	                communityImageDto.setAttachmentNo(attachmentNo);
	                communityImageDto.setCommunityNo(communityDto.getCommunityNo());
	                communityImageDao.insert(communityImageDto);
	            }
	        }
	    }
	    
	    @PutMapping("/") // 수정
	    public void update(
	    		@RequestPart("community") CommunityDto communityDto,
	    		@RequestPart(value = "files", required = false)
	    		List<MultipartFile> files) 
	    		throws IllegalStateException, IOException{
	    	//1.게임 정보 수정
	        boolean result = communityDao.update(communityDto);
	        if (!result) {
	            throw new TargetNotFoundException("존재하지 않는 게임정보");
	        }
	        
	        //2. 새로운 이미지가 있다면 자동으로 연결
	        if(files != null && !files.isEmpty()) {
	        	for(MultipartFile file : files) {
	        		int attachmentNo = attachmentService.save(file);
	        		
	        		CommunityImageDto communityImageDto = new CommunityImageDto();
	        		communityImageDto.setAttachmentNo(attachmentNo);
	        		communityImageDto.setCommunityNo(communityDto.getCommunityNo());
	        		communityImageDao.insert(communityImageDto);
	        	}
	        }
	    }
	    
	    @DeleteMapping("/{communityNo}") // 삭제
	    public void delete(@PathVariable int communityNo) {
	        boolean result = communityDao.delete(communityNo);
	        if (!result) {
	            throw new TargetNotFoundException("존재하지 않는 게임정보");
	        }
	    }
	    
	    
	    
	    	
	    @GetMapping("/{communityNo}")//상세
	    public CommunityDto detail(
	    		@Parameter(required = true, description = "도서번호(pk)")
	    		@PathVariable int communityNo) {
	        CommunityDto communityDto = communityDao.selectOne(communityNo);
	        if (communityDto == null) {
	            throw new TargetNotFoundException("존재하지 않는 게임입니다");
	        }
	        return communityDto;
	    }
	    
	
	  //게임의 이미지 목록을 조회하는 엔드포인트
	    @GetMapping("/image/{communityNo}")
	    public List<CommunityImageDto> getCommunityImages(@PathVariable int communityNo){
	    	return communityImageDao.selectList(communityNo);
	    }
	    
	    //이미지 다운로드를 처리하는 엔드포인트
	    @GetMapping("/download/{attachmentNo}")
	    public ResponseEntity<ByteArrayResource> downloadImage(@PathVariable int attachmentNo) throws IOException {
	        return attachmentService.find(attachmentNo); // 파일을 읽고 ByteArrayResource로 반환
	    }

	    
	    //첨부파일을 하나씩 업로드하는 엔드포인트
	    @PostMapping("/upload/{communityNo}")
	    public int uploadCommunityImage(
	    		@PathVariable int communityNo,
	    		@RequestParam("file") MultipartFile file)
	    		throws IllegalStateException, IOException{
	    	//1. 첨부파일 저장
	    	int attachmentNo = attachmentService.save(file);
	    	
	    	//2. 게임 이미지 정보 저장
	    	CommunityImageDto communityImageDto = new CommunityImageDto();
	    	communityImageDto.setAttachmentNo(attachmentNo);
	    	communityImageDto.setCommunityNo(communityNo);
	    	communityImageDao.insert(communityImageDto);
	    	
	    	return attachmentNo;
	    }
	    
	    //여러 첨부파일을 한번에 업로드하는 엔드포인트
	    @PostMapping("/upload/multiple/{communityNo}")
	    public void uploadCommunityImages(
	    		@PathVariable int communityNo, 
	    		@RequestParam("files") List<MultipartFile> files)
	    		throws IllegalStateException, IOException{
		    	//1. 게임 이미지 정보 저장
		    	for(MultipartFile file : files) {
		    		int attachmentNo = attachmentService.save(file);
	    		
	    		//2. 게임 이미지 정보 저장
	        	CommunityImageDto communityImageDto = new CommunityImageDto();
	        	communityImageDto.setAttachmentNo(attachmentNo);
	        	communityImageDto.setCommunityNo(communityNo);
	        	communityImageDao.insert(communityImageDto);
	    	}
	    }
	    
	    @PutMapping("/community/{communityNo}")
	    public ResponseEntity<?> updateCommunity(
	            @PathVariable int communityNo,
	            @RequestParam("communityTitle") String communityTitle,
	            @RequestParam("communityContent") String communityContent,
	            @RequestParam("communityState") String communityState,
	            @RequestParam("communityCategory") String communityCategory,
	            @RequestPart(name = "file", required = false) MultipartFile file) {
	        
	        // Community 업데이트 로직 작성 (예: communityService.update(communityNo, communityTitle, ...))

	        return ResponseEntity.ok().build();
	    }
	    
	    
	    
	    
	    
	
	    
	    
}