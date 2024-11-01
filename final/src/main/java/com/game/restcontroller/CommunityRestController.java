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
import com.game.dto.GameDto;
import com.game.dto.GameImageDto;
import com.game.dto.ReplyDto;
import com.game.error.TargetNotFoundException;
import com.game.service.AttachmentService;
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
	 @GetMapping("/list")
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
	//파일첨부 생긴거때문에 --------------------------------------------------
//	//등록
//		@Transactional
//		@PostMapping(value = "/insert", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//		public void insert(@ModelAttribute CommunityDto noticeDto,
//				@RequestHeader("Authorization") String token,
//				@ModelAttribute CommunityInsertImageRequestVO requestVO) 
//						throws IllegalStateException, IOException {
//		// 토큰 변환
//		MemberClaimVO claimVO = tokenService.check(tokenService.removeBearer(token));
//		// 유효 검증
//		MemberDto memberDto = memberDao.selectOne(claimVO.getMemberId());
//		if(memberDto == null) throw new TargetNotFoundException("존재하지 않는 회원");
//		
//			int noticeNo = communityDao.sequence();
//		
//			noticeDto.setNoticeWriter(claimVO.getMemberId());
//			noticeDto.setCommunityNo(noticeNo);
////			System.out.println(noticeDto);
////			System.out.println(noticeNo);
//			communityDao.insert(noticeDto);
//			
//			//파일 등록
//			if(requestVO.getAttachList() != null) {
//				for(MultipartFile attach : requestVO.getAttachList()) {
//				if(attach.isEmpty()) continue; // 파일이 없다면 스킵
//						
//				int attachmentNo = attachmentService.save(attach);
//				communityDao.connect(communityNo, attachmentNo);
//				}			
//			}
//		
//		}
//		@GetMapping("/detail/{noticeNo}")//상세
//		public CommunityDetailResponseVO detail(
//				@Parameter(required = true, description = "글 번호(PK)")
//				@PathVariable int communityNo) {
//			
//			CommunityDto communityDto = communityDao.selectOne(communityNo);
//			if(communityDto == null) throw new TargetNotFoundException();
//			
//			// 해당 게시글의 이미지 번호들을 조회하여 전달
//			List<Integer> images = communityDao.findImages(communityNo);
//			
//			CommunityDetailResponseVO responseVO = new CommunityDetailResponseVO();
//			responseVO.setCommunityDto(communityDto);
//			responseVO.setImages(images);
//			return responseVO;
////			return noticeDto;
//		}
//		
////		@PostMapping("/list")//목록 + 페이징 + 검색
////		public NoticeListResponseVO list(@RequestBody NoticeListRequestVO vo){
////			int count = noticeDao.countWithPaging(vo);
////			boolean last = vo.getEndRow() == null  || count <= vo.getEndRow();
////			NoticeListResponseVO response = new NoticeListResponseVO();
////			response.setNoticeList(noticeDao.selectListByPaging(vo));
////			response.setCount(count);
////			response.setLast(last);
////			return response;
////		}	
//		
//		@GetMapping("/list")
//		public List<CommunityDto> list(){
//			return communityDao.selectList();
//		}
//		
//		@GetMapping("/list/column/{column}/keyword/{keyword}")
//		public List<CommunityDto> list(@PathVariable String column, @PathVariable String keyword) {
//			return communityDao.selectList(column, keyword);
//		}
//		
//		//수정
//		@Transactional
//		@PostMapping(value = "/edit", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//		public void update(
//				@RequestHeader("Authorization") String token,
//				@ModelAttribute CommunityEditRequestVO requestVO) throws IllegalStateException, IOException {
//			// 토큰 변환
//		    MemberClaimVO claimVO = tokenService.check(tokenService.removeBearer(token));
//			//공지사항 업데이트
////			boolean result = noticeDao.update(noticeDto);
////			if(result == false) {
////				throw new TargetNotFoundException();
////			}
//			CommunityDto originDto = communityDao.selectOne(requestVO.getCommunityNo());
//			if(originDto == null) throw new TargetNotFoundException("존재하지 않는 게시글");
//			
//			//작성자 확인
//					boolean isOwner = originDto.getCommunityWriter().equals(claimVO.getMemberId());
//					if(!isOwner) {
//						throw new IllegalStateException("본인의 글만 수정할 수 있습니다.");
//					}
//			
//			// 이미지 처리 수정 전
//			Set<Integer> before = new HashSet<>();
//			List<Integer> beforeList = communityDao.findImages(originDto.getCommunityNo());
//			for(int i=0; i<beforeList.size();i++){
//				before.add(beforeList.get(i));
//			}
//			
//			//이미지 처리 수정 후
////			List<Integer> beforeImages = noticeDao.findImages(originDto.getNoticeNo()); // 기존 이미지 목록
//			Set<Integer> after = new HashSet<>(); // 기존 이미지 세트
//			communityDao.deleteImage(requestVO.getCommunityNo());
//			int afterSize = requestVO.getOriginList().size();
//			for(int i=0; i<afterSize;i++) {
//				int attachmentNo = requestVO.getOriginList().get(i);
//				communityDao.connect(requestVO.getCommunityNo(), attachmentNo);
//				after.add(attachmentNo);
//			}
//
//			//수정전 - 수정후 계산
//			before.removeAll(after);
//			
//			//before에 남아있는 번호에 해당하는 파일 모두 삭제
//			for(int attachmentNo : before) {
//				attachmentService.delete(attachmentNo);
//			}
//			//attachList 신규첨부
//			if(requestVO.getAttachList() != null) {
//				int attachListSize = requestVO.getAttachList().size();
//				for(int i=0; i<attachListSize; i++) {
//					int attachmentNo = attachmentService.save(requestVO.getAttachList().get(i));
//					communityDao.connect(requestVO.getCommunityNo(), attachmentNo);
//					after.add(attachmentNo);
//				}
//			}
//			//게시글 정보 수정
//			CommunityDto communityDto = new CommunityDto();
//			communityDto.setCommunityTitle(requestVO.getCommunityTitle());
//			communityDto.setCommunityContent(requestVO.getCommunityContent());
//			communityDto.setCommunityCategory(requestVO.getCommunityType());
//			communityDto.setCommunityNo(requestVO.getCommunityNo());
//			
//			communityDao.update(communityDto);	
//		}
//		
//		@DeleteMapping("/delete/{communityNo}")//삭제
//		public void delete(
//				@RequestHeader("Authorization") String token,
//				@PathVariable int communityNo) throws IllegalStateException, IOException {
//			
//			// 토큰 변환
//		    MemberClaimVO claimVO = tokenService.check(tokenService.removeBearer(token));
//		    
//		    if (!claimVO.getMemberId().equals(claimVO.getMemberId())) {
//	            throw new IllegalStateException("본인의 글만 삭제할 수 있습니다.");
//	        }
//		    
//			CommunityDto communityDto = communityDao.selectOne(communityNo);
//			if(communityDto == null)
//				throw new TargetNotFoundException("존재하지 않는 게시글");
//			
//			List<Integer> list = communityDao.findImages(communityNo);
//			for(int i=0;i<list.size();i++) {
//				attachmentService.delete(list.get(i));
//			}
//			boolean isOwner =  communityDto.getCommunityWriter().equals(claimVO.getMemberId());
//		    if(isOwner) {
//		    	communityDao.delete(communityNo);
//		    	
//			communityDao.delete(communityNo);// 게시글 삭제
//			communityDao.deleteImage(communityNo);    
//		}
//	}
	    
//	 // 이미지 첨부 기능이 포함된 게시글 등록 (POST 요청)
//	    @PostMapping("/with-images")
//	    public void insert(
//	            @RequestPart("community") CommunityDto communityDto,
//	            @RequestPart(value = "files", required = false) List<MultipartFile> files) 
//	            throws IllegalStateException, IOException {
//
//	        // 1. 게시글 정보 등록
//	        communityDao.insert(communityDto);
//	        
//	        // 2. 이미지가 있다면 자동으로 연결
//	        if (files != null && !files.isEmpty()) {
//	            for (MultipartFile file : files) {
//	                // 첨부파일 저장
//	                int attachmentNo = attachmentService.save(file);
//	                
//	                // 게시글-이미지 연결정보 저장
//	                CommunityImageDto communityImageDto = new CommunityImageDto();
//	                communityImageDto.setAttachmentNo(attachmentNo);
//	                communityImageDto.setCommunityNo(communityDto.getCommunityNo());
//	                communityImageDao.insert(communityImageDto);
//	            }
//	        }
//	    }
//	    
//	    @PutMapping("/") // 수정
//	    public void update(
//	    		@RequestPart("community") CommunityDto communityDto,
//	    		@RequestPart(value = "files", required = false)
//	    		List<MultipartFile> files) 
//	    		throws IllegalStateException, IOException{
//	    	//1.게임 정보 수정
//	        boolean result = communityDao.update(communityDto);
//	        if (!result) {
//	            throw new TargetNotFoundException("존재하지 않는 게임정보");
//	        }
//	        
//	        //2. 새로운 이미지가 있다면 자동으로 연결
//	        if(files != null && !files.isEmpty()) {
//	        	for(MultipartFile file : files) {
//	        		int attachmentNo = attachmentService.save(file);
//	        		
//	        		CommunityImageDto communityImageDto = new CommunityImageDto();
//	        		communityImageDto.setAttachmentNo(attachmentNo);
//	        		communityImageDto.setCommunityNo(communityDto.getCommunityNo());
//	        		communityImageDao.insert(communityImageDto);
//	        	}
//	        }
//	    }
	    
	    
	    
	    
	    
	    
//	  //게임의 이미지 목록을 조회하는 엔드포인트
//	    @GetMapping("/image/{communityNo}")
//	    public List<CommunityImageDto> getCommunityImages(@PathVariable int communityNo){
//	    	return communityImageDao.selectList(communityNo);
//	    }
//	    
//	    //첨부파일을 하나씩 업로드하는 엔드포인트
//	    @PostMapping("/upload/{communityNo}")
//	    public int uploadCommunityImage(
//	    		@PathVariable int communityNo,
//	    		@RequestParam("file") MultipartFile file)
//	    		throws IllegalStateException, IOException{
//	    	//1. 첨부파일 저장
//	    	int attachmentNo = attachmentService.save(file);
//	    	
//	    	//2. 게임 이미지 정보 저장
//	    	CommunityImageDto communityImageDto = new CommunityImageDto();
//	    	communityImageDto.setAttachmentNo(attachmentNo);
//	    	communityImageDto.setCommunityNo(communityNo);
//	    	communityImageDao.insert(communityImageDto);
//	    	
//	    	return attachmentNo;
//	    }
//	    
//	    //여러 첨부파일을 한번에 업로드하는 엔드포인트
//	    @PostMapping("/upload/multiple/{communityNo}")
//	    public void uploadCommunityImages(
//	            @PathVariable int communityNo, 
//	            @RequestParam("files") List<MultipartFile> files) throws IllegalStateException, IOException {
//	        
//	        for (MultipartFile file : files) {
//	            int attachmentNo = attachmentService.save(file);
//	            System.out.println("Saved file with attachmentNo: " + attachmentNo);
//	            
//	            CommunityImageDto communityImageDto = new CommunityImageDto();
//	            communityImageDto.setAttachmentNo(attachmentNo);
//	            communityImageDto.setCommunityNo(communityNo);
//	            
//	            communityImageDao.insert(communityImageDto);
//	            System.out.println("Inserted community image with communityNo: " + communityNo + ", attachmentNo: " + attachmentNo);
//	        }
//	    }
	    
	    
	    
}