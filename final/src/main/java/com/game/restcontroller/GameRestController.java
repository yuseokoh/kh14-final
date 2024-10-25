package com.game.restcontroller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.game.dao.GameDao;
import com.game.dao.GameImageDao;
import com.game.dao.PaymentDao;
import com.game.dto.GameDto;
import com.game.dto.GameImageDto;
import com.game.dto.PaymentDetailDto;
import com.game.dto.PaymentDto;
import com.game.error.TargetNotFoundException;
import com.game.service.AttachmentService;
import com.game.service.KakaoPayService;
import com.game.service.PaymentService;
import com.game.service.TokenService;
import com.game.vo.GameApproveRequestVO;
import com.game.vo.GamePurchaseRequestVO;
import com.game.vo.GameQtyVO;
import com.game.vo.MemberClaimVO;
import com.game.vo.pay.KakaoPayApproveRequestVO;
import com.game.vo.pay.KakaoPayApproveResponseVO;
import com.game.vo.pay.KakaoPayReadyRequestVO;
import com.game.vo.pay.KakaoPayReadyResponseVO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.mail.Multipart;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/game")
public class GameRestController {

    @Autowired 
    private GameDao gameDao;
    
    @Autowired
    private PaymentDao paymentDao;
    
    @Autowired
    private KakaoPayService kakaoPayService;
    
    @Autowired
    private TokenService tokenService;
    
    @Autowired
    private PaymentService paymentService;  // 공통 서비스 사용
    
    @Autowired
    private GameImageDao gameImageDao;
    
    @Autowired
    private AttachmentService attachmentService;
    
    // 조회매핑 API 문서 설명 추가
    @Operation(
        deprecated = false, // 비추천 여부(사용 중지 예정인 경우 true로 설정)
        description = "게임 정보에 대한 조회를 처리합니다", // 설명
        responses = { // 예상 가능한 응답코드에 대한 안내
            @ApiResponse(
                responseCode = "200", // 상태코드
                description = "조회 완료", // 설명
                content = @Content( // 결과 메세지의 형태와 샘플
                    mediaType = "application/json", // 결과 메세지의 MIME 타입
                    array = @ArraySchema( // 결과가 배열일 경우에 대한 안내
                        schema = @Schema(implementation = GameDto.class) // 내용물
                    )
                )
            ),
            @ApiResponse(
                responseCode = "500", // 상태코드
                description = "서버 오류", // 설명
                content = @Content( // 결과 메세지의 형태와 샘플
                    mediaType = "text/plain", // 결과 메세지의 MIME 타입
                    schema = @Schema(implementation = String.class), // 자바의 자료형
                    examples = @ExampleObject("server error") // 예시 데이터
                )
            )
        }
    )
    
    @GetMapping("/") // 목록
    public List<GameDto> list() {
        return gameDao.selectList();
    }
    
    @GetMapping("/column/{column}/keyword/{keyword}") // 검색(조회)
    public List<GameDto> search(
        @Parameter(description = "검색할 항목")
        @PathVariable String column, 
        @Parameter(description = "검색 키워드")
        @PathVariable String keyword) {
        return gameDao.selectList(column, keyword);
    }
    
    @PostMapping("/") // 등록
    public void insert(
    		@RequestPart("game") GameDto gameDto,
    		@RequestPart(value = "files", required = false) 
    		List<MultipartFile> files) 
    		throws IllegalStateException, IOException{
    	//1. 게임 정보 등록
        gameDao.insert(gameDto);
        
        //2. 이미지가 있다면 자동으로 연결
        if (files != null && !files.isEmpty()){
        	for(MultipartFile file : files) {
        		//첨부파일 저장
        		int attachmentNo = attachmentService.save(file);
        		
        		//게임-이미지 연결정보 저장
        		GameImageDto gameImageDto = new GameImageDto();
        		gameImageDto.setAttachmentNo(attachmentNo);
        		gameImageDto.setGameNo(gameDto.getGameNo());
        		gameImageDao.insert(gameImageDto);
        	}
        }
    }
    
    @PutMapping("/") // 수정
    public void update(
    		@RequestPart("game") GameDto gameDto,
    		@RequestPart(value = "files", required = false)
    		List<MultipartFile> files) 
    		throws IllegalStateException, IOException{
    	//1.게임 정보 수정
        boolean result = gameDao.update(gameDto);
        if (!result) {
            throw new TargetNotFoundException("존재하지 않는 게임정보");
        }
        
        //2. 새로운 이미지가 있다면 자동으로 연결
        if(files != null && !files.isEmpty()) {
        	for(MultipartFile file : files) {
        		int attachmentNo = attachmentService.save(file);
        		
        		GameImageDto gameImageDto = new GameImageDto();
        		gameImageDto.setAttachmentNo(attachmentNo);
        		gameImageDto.setGameNo(gameDto.getGameNo());
        		gameImageDao.insert(gameImageDto);
        	}
        }
    }
    
    @DeleteMapping("/{gameNo}") // 삭제
    public void delete(@PathVariable int gameNo) {
        boolean result = gameDao.delete(gameNo);
        if (!result) {
            throw new TargetNotFoundException("존재하지 않는 게임정보");
        }
    }
    
    
    @Operation(
    		description = "도서 정보 상세 조회"
    		,responses = {
    			@ApiResponse(//정상
    				responseCode = "200"//상태코드
    				,description = "조회 완료"//설명
    				,content = @Content(//결과 메세지의 형태와 샘플
    					mediaType = "application/json"//결과 메세지의 MIME 타입
    					,schema = @Schema(implementation = GameDto.class)//내용물
    				)
    			),
    			@ApiResponse(//서버 오류
    				responseCode = "404"//상태코드	
    				,description = "대상을 찾을 수 없음"//설명
    				,content = @Content(//결과 메세지의 형태와 샘플
    					mediaType = "text/plain"//결과 메세지의 MIME 타입
    					,schema = @Schema(implementation = String.class)//자바의 자료형
    					,examples = @ExampleObject("target not found")//예시 데이터
    				)
    			),
    			@ApiResponse(//서버 오류
    				responseCode = "500"//상태코드	
    				,description = "서버 오류"//설명
    				,content = @Content(//결과 메세지의 형태와 샘플
    					mediaType = "text/plain"//결과 메세지의 MIME 타입
    					,schema = @Schema(implementation = String.class)//자바의 자료형
    					,examples = @ExampleObject("server error")//예시 데이터
    				)
    			)
    		}
    	)
    @GetMapping("/{gameNo}")//상세
    public GameDto detail(
    		@Parameter(required = true, description = "도서번호(pk)")
    		@PathVariable int gameNo) {
        GameDto gameDto = gameDao.selectOne(gameNo);
        if (gameDto == null) {
            throw new TargetNotFoundException("존재하지 않는 게임입니다");
        }
        return gameDto;
    }
    
    // 구매
    @PostMapping("/purchase")
    public KakaoPayReadyResponseVO purchase(
        @RequestHeader("Authorization") String token,
        @RequestBody GamePurchaseRequestVO request) throws URISyntaxException {
        // 카카오페이에 보낼 최종 결제 정보를 생성하고 결제 준비 요청을 보낸다.
        MemberClaimVO claimVO = tokenService.check(tokenService.removeBearer(token));
        
        // 상품명, 금액을 계산
        StringBuffer buffer = new StringBuffer(); 
        int total = 0;
        for (GameQtyVO vo : request.getGameList()) {
            GameDto gameDto = gameDao.selectOne(vo.getGameNo()); // 게임정보 조회
            if (gameDto == null) throw new TargetNotFoundException("결제 대상 게임이 존재하지 않습니다");
            total += gameDto.getGamePrice() * vo.getQty(); // 게임가격 * 구매수량
            
            if (buffer.isEmpty()) { // 버퍼가 비어 있을 경우
                buffer.append(gameDto.getGameTitle()); // 제목 추가
            }
        }
        if (request.getGameList().size() >= 2) { // 구매하는 게임이 2건 이상이라면
            buffer.append(" 외 " + (request.getGameList().size() - 1) + "건");
        }
        
        // 결제번호 생성 공통 메서드 호출
        int paymentNo = paymentService.generatePaymentSeq();
        
        KakaoPayReadyRequestVO requestVO = new KakaoPayReadyRequestVO();
        requestVO.setPartnerOrderId(String.valueOf(paymentNo));
        requestVO.setPartnerUserId(claimVO.getMemberId());
        requestVO.setItemName(buffer.toString());
        requestVO.setTotalAmount(total);
        requestVO.setApprovalUrl(request.getApprovalUrl());
        requestVO.setCancelUrl(request.getCancelUrl());
        requestVO.setFailUrl(request.getFailUrl());
        
        KakaoPayReadyResponseVO responseVO = kakaoPayService.ready(requestVO);
        
        return responseVO;
    }
    
    @Transactional
    @PostMapping("/approve")
    public KakaoPayApproveResponseVO approve(
        @RequestHeader("Authorization") String token,
        @RequestBody GameApproveRequestVO request) throws URISyntaxException {
        MemberClaimVO claimVO = tokenService.check(tokenService.removeBearer(token));
        
        KakaoPayApproveRequestVO requestVO = new KakaoPayApproveRequestVO();
        requestVO.setPartnerOrderId(request.getPartnerOrderId());
        requestVO.setPartnerUserId(claimVO.getMemberId());
        requestVO.setTid(request.getTid());
        requestVO.setPgToken(request.getPgToken());
        
        KakaoPayApproveResponseVO responseVO = kakaoPayService.approve(requestVO);
        
        // 최종 결제가 완료된 시점에 DB에 결제에 대한 기록을 남긴다
        
        // [1] 대표 정보 등록
        PaymentDto paymentDto = new PaymentDto();
        paymentDto.setPaymentNo(Integer.parseInt(request.getPartnerOrderId())); // 결제번호
        paymentDto.setPaymentTid(responseVO.getTid()); // 거래번호
        paymentDto.setPaymentName(responseVO.getItemName()); // 거래상품명
        paymentDto.setPaymentTotal(responseVO.getAmount().getTotal()); // 거래금액
        paymentDto.setPaymentRemain(paymentDto.getPaymentTotal()); // 취소가능금액
        paymentDto.setPaymentMemberId(claimVO.getMemberId()); // 구매자ID
        paymentDao.paymentInsert(paymentDto); // 대표정보 등록
        
        // [2] 상세 정보 등록
        for (GameQtyVO qtyVO : request.getGameList()) {
            GameDto gameDto = gameDao.selectOne(qtyVO.getGameNo()); // 게임 조회
            if (gameDto == null) throw new TargetNotFoundException("존재하지 않는 게임");
            
            int paymentDetailSeq = paymentService.generatePaymentDetailSeq(); // 상세 결제번호 생성 공통 메서드 호출
            PaymentDetailDto paymentDetailDto = new PaymentDetailDto();
            paymentDetailDto.setPaymentDetailNo(paymentDetailSeq); // 번호 설정
            paymentDetailDto.setPaymentDetailName(gameDto.getGameTitle()); // 상품명(게임명) 설정
            paymentDetailDto.setPaymentDetailPrice(gameDto.getGamePrice()); // 상품판매가(게임가격) 설정
            paymentDetailDto.setPaymentDetailItem(gameDto.getGameNo()); // 상품번호(게임번호) 설정
            paymentDetailDto.setPaymentDetailQty(1);
            paymentDetailDto.setPaymentDetailOrigin(paymentDto.getPaymentNo()); // 결제대표번호
            paymentDao.paymentDetailInsert(paymentDetailDto);
        }
        
        return responseVO;
    }
    
    //게임의 이미지 목록을 조회하는 엔드포인트
    @GetMapping("/image/{gameNo}")
    public List<GameImageDto> getGameImages(@PathVariable int gameNo){
    	return gameImageDao.selectList(gameNo);
    }
    
    //이미지 다운로드를 처리하는 엔드포인트
    @GetMapping("/download/{attachmentNo}")
    public ResponseEntity<ByteArrayResource> downloadImage(
    		@PathVariable int attachmentNo) throws IOException{
    	return attachmentService.find(attachmentNo);
    }
    
    //첨부파일을 하나씩 업로드하는 엔드포인트
    @PostMapping("/upload/{gameNo}")
    public int uploadGameImage(
    		@PathVariable int gameNo,
    		@RequestParam("file") MultipartFile file)
    		throws IllegalStateException, IOException{
    	//1. 첨부파일 저장
    	int attachmentNo = attachmentService.save(file);
    	
    	//2. 게임 이미지 정보 저장
    	GameImageDto gameImageDto = new GameImageDto();
    	gameImageDto.setAttachmentNo(attachmentNo);
    	gameImageDto.setGameNo(gameNo);
    	gameImageDao.insert(gameImageDto);
    	
    	return attachmentNo;
    }
    
    //여러 첨부파일을 한번에 업로드하는 엔드포인트
    @PostMapping("/upload/multiple/{gameNo}")
    public void uploadGameImages(
    		@PathVariable int gameNo, 
    		@RequestParam("files") List<MultipartFile> files)
    		throws IllegalStateException, IOException{
	    	//1. 게임 이미지 정보 저장
	    	for(MultipartFile file : files) {
	    		int attachmentNo = attachmentService.save(file);
    		
    		//2. 게임 이미지 정보 저장
        	GameImageDto gameImageDto = new GameImageDto();
        	gameImageDto.setAttachmentNo(attachmentNo);
        	gameImageDto.setGameNo(gameNo);
        	gameImageDao.insert(gameImageDto);
    	}
    }
}
