package com.game.restcontroller;

import java.io.IOException;
import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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

import com.game.dao.CertDao;
import com.game.dao.CommunityDao;
import com.game.dao.MemberDao;
import com.game.dao.MemberImageDao;
import com.game.dao.MemberTokenDao;
import com.game.dao.PlayDao;
import com.game.dto.MemberDto;
import com.game.dto.MemberImageDto;
import com.game.dto.MemberTokenDto;
import com.game.error.TargetNotFoundException;
import com.game.service.AttachmentService;
import com.game.service.EmailService;
import com.game.service.KakaoLoginService;
import com.game.service.TokenService;
import com.game.vo.MemberClaimVO;
import com.game.vo.MemberComplexRequestVO;
import com.game.vo.MemberComplexResponseVO;
import com.game.vo.MemberLoginRequestVO;
import com.game.vo.MemberLoginResponseVO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@CrossOrigin(origins = {"http://localhost:3000"})
@RestController
@RequestMapping("/member")
public class MemberRestController {

    @Autowired
    private MemberDao memberDao;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private MemberTokenDao memberTokenDao;
    @Autowired
    private PasswordEncoder encoder;
    @Autowired
    private EmailService emailService;
    @Autowired
    private CertDao certDao;
    @Autowired
    private KakaoLoginService kakaoLoginService;
    @Autowired
    private MemberImageDao memberImageDao;
    @Autowired
    private AttachmentService attachmentService;
    @Autowired
    private PlayDao playDao;
    @Autowired
    private CommunityDao communityDao;
    
    @PostMapping("/search")
    public MemberComplexResponseVO search(@RequestBody MemberComplexRequestVO vo) {
        int count = memberDao.complexSearchCount(vo);
        boolean last = vo.getEndRow() == null || count <= vo.getEndRow();

        MemberComplexResponseVO response = new MemberComplexResponseVO();
        response.setMemberList(memberDao.complexSearch(vo));
        response.setCount(count);
        response.setLast(last);
        return response;
    }

    //게시글 작성 및 게임 플레이시 포인트 증가시키기
    
//    @PostMapping("/login")
//    public MemberLoginResponseVO login(@RequestBody MemberLoginRequestVO vo) {
//
//        MemberDto memberDto = memberDao.selectOne(vo.getMemberId());
//        if (memberDto == null) {
//            throw new TargetNotFoundException("아이디 없음");
//        }
//        boolean isValid = encoder.matches(vo.getMemberPw(), memberDto.getMemberPw());
//
//        if (isValid) {
//            MemberLoginResponseVO response = new MemberLoginResponseVO();
//
//            response.setMemberId(memberDto.getMemberId());
//            response.setMemberLevel(memberDto.getMemberLevel());
//            MemberClaimVO claimVO = new MemberClaimVO();
//            claimVO.setMemberId(memberDto.getMemberId());
//            claimVO.setMemberLevel(memberDto.getMemberLevel());
//            response.setAccessToken(tokenService.createAccessToken(claimVO));
//            response.setRefreshToken(tokenService.createRefreshToken(claimVO));
//
//            memberDao.updateLastLogin(memberDto.getMemberId());
//            memberDao.updateLogoutTime(memberDto.getMemberId());
//            
//            return response;
//        } else {
//            throw new TargetNotFoundException("로그인 실패");
//        }
//    }
    
    @PostMapping("/login")
    public MemberLoginResponseVO login(@RequestBody MemberLoginRequestVO vo) {
        // 입력된 로그인 정보 로그
        System.out.println("로그인 시도 - 아이디: " + vo.getMemberId());

        // 회원 정보 조회
        MemberDto memberDto = memberDao.selectOne(vo.getMemberId());
        if (memberDto == null) {
            System.out.println("로그인 실패 - 존재하지 않는 아이디: " + vo.getMemberId());
            throw new TargetNotFoundException("존재하지 않는 아이디입니다.");
        }

        // 비밀번호 비교 (암호화 방식 일치 여부 확인을 위해 로그 추가)
        System.out.println("입력된 비밀번호: " + vo.getMemberPw());
        System.out.println("저장된 비밀번호(해시): " + memberDto.getMemberPw());
        boolean isValid = encoder.matches(vo.getMemberPw(), memberDto.getMemberPw());
        if (!isValid) {
            System.out.println("로그인 실패 - 비밀번호 불일치: " + vo.getMemberId());
            throw new TargetNotFoundException("비밀번호가 일치하지 않습니다.");
        }

        // 로그인 성공 시 응답 생성
        System.out.println("로그인 성공 - 아이디: " + vo.getMemberId());
        MemberLoginResponseVO response = new MemberLoginResponseVO();
        response.setMemberId(memberDto.getMemberId());
        response.setMemberLevel(memberDto.getMemberLevel());

        // 토큰 생성
        MemberClaimVO claimVO = new MemberClaimVO();
        claimVO.setMemberId(memberDto.getMemberId());
        claimVO.setMemberLevel(memberDto.getMemberLevel());
        response.setAccessToken(tokenService.createAccessToken(claimVO));
        response.setRefreshToken(tokenService.createRefreshToken(claimVO));

        // 로그인 시간 갱신
        memberDao.updateLastLogin(memberDto.getMemberId());
        // 로그아웃 시간 갱신
        memberDao.updateLogoutTime(memberDto.getMemberId());
        return response;
    }
    
//    @PostMapping("/kakaoLogin")
//    public ResponseEntity<MemberLoginResponseVO> kakaoLogin(@RequestBody Map<String, String> request) {
//        String code = request.get("code");
//        System.out.println("받은 인가 코드: " + code);
//
//        try {
//            // 액세스 토큰 발급
//            String accessToken = kakaoLoginService.getAccessToken(code);
//            System.out.println("발급된 액세스 토큰: " + accessToken);
//
//            // 사용자 정보 요청
//            MemberDto kakaoUser = kakaoLoginService.getUserInfo(accessToken);
//            System.out.println("카카오 사용자 정보: " + kakaoUser.toString());
//
//            // 기존 회원 여부 확인 및 회원가입 처리
//            Optional<MemberDto> existingMember = memberDao.selectOneByKakaoId(kakaoUser.getKakaoId());
//            MemberDto member;
//            if (existingMember.isPresent()) {
//                System.out.println("기존 회원 로그인: " + existingMember.get().getMemberId());
//                member = existingMember.get();
//            } else {
//                System.out.println("신규 회원 가입: 카카오 아이디 " + kakaoUser.getKakaoId());
//                member = new MemberDto();
//                member.setMemberId("kakao_" + kakaoUser.getKakaoId());
//
//                // 이메일이 null인 경우 기본값 설정
//                if (kakaoUser.getMemberEmail() == null || kakaoUser.getMemberEmail().isEmpty()) {
//                    member.setMemberEmail("default_email@example.com"); // 기본 이메일 설정
//                } else {
//                    member.setMemberEmail(kakaoUser.getMemberEmail());
//                }
//
//                // 닉네임이 null인 경우 기본값 설정
//                if (kakaoUser.getMemberNickname() == null || kakaoUser.getMemberNickname().isEmpty()) {
//                    member.setMemberNickname("카카오사용자"); // 기본 닉네임 설정
//                } else {
//                    member.setMemberNickname(kakaoUser.getMemberNickname());
//                }
//
//                member.setKakaoId(kakaoUser.getKakaoId());
//                member.setMemberLevel("BASIC");
//                member.setMemberPw(encoder.encode("TEMP_PASSWORD")); // 임시 비밀번호 설정
//
//                memberDao.insert(member);
//                System.out.println("신규 회원 등록 완료: " + member.getMemberId());
//            }
//
//            // JWT 토큰 생성
//            MemberClaimVO claimVO = new MemberClaimVO();
//            claimVO.setMemberId(member.getMemberId());
//            claimVO.setMemberLevel(member.getMemberLevel());
//
//            MemberLoginResponseVO response = new MemberLoginResponseVO();
//            response.setMemberId(member.getMemberId());
//            response.setMemberLevel(member.getMemberLevel());
//            response.setAccessToken(tokenService.createAccessToken(claimVO));
//            response.setRefreshToken(tokenService.createRefreshToken(claimVO));
//
//            System.out.println("JWT 토큰 생성 완료: " + response.getAccessToken());
//            return ResponseEntity.ok(response); // 클라이언트에 로그인 완료 후 JWT 토큰을 반환
//
//        } catch (Exception e) {
//            System.out.println("액세스 토큰 요청 중 오류 발생: " + e.getMessage());
//            e.printStackTrace();
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
//        }
//    }

    
    
    @PostMapping("/refresh")
    public MemberLoginResponseVO refresh(
            @RequestHeader("Authorization") String refreshToken) {
        //[1] refreshToken이 없거나 Bearer로 시작하지 않으면 안됨
        if (refreshToken == null)
            throw new TargetNotFoundException("토큰 없음");
        if (tokenService.isBearerToken(refreshToken) == false)
            throw new TargetNotFoundException("Bearer 토큰 아님");

        //[2] 토큰에서 정보를 추출
        MemberClaimVO claimVO =
                tokenService.check(tokenService.removeBearer(refreshToken));
        if (claimVO.getMemberId() == null)
            throw new TargetNotFoundException("아이디 없음");
        if (claimVO.getMemberLevel() == null)
            throw new TargetNotFoundException("등급 없음");

        //[3] 토큰 발급 내역을 조회
        MemberTokenDto memberTokenDto = new MemberTokenDto();
        memberTokenDto.setTokenTarget(claimVO.getMemberId());
        memberTokenDto.setTokenValue(tokenService.removeBearer(refreshToken));
        MemberTokenDto resultDto = memberTokenDao.selectOne(memberTokenDto);
        if (resultDto == null)//발급내역이 없음
            throw new TargetNotFoundException("발급 내역이 없음");

        //[4] 기존의 리프레시 토큰 삭제
        memberTokenDao.delete(memberTokenDto);

        //[5] 로그인 정보 재발급
        MemberLoginResponseVO response = new MemberLoginResponseVO();
        response.setMemberId(claimVO.getMemberId());
        response.setMemberLevel(claimVO.getMemberLevel());
        response.setAccessToken(tokenService.createAccessToken(claimVO));//재발급
        response.setRefreshToken(tokenService.createRefreshToken(claimVO));//재발급
        return response;
    }

    @GetMapping("/{memberId}")
    public MemberDto find(@PathVariable String memberId) {
        MemberDto memberDto = memberDao.selectOne(memberId);

        // 이미지 정보 가져오기
        MemberImageDto memberImage = memberImageDao.selectone(memberId);
        if (memberImage != null) {
            memberDto.setAttachment(memberImage.getAttachment());
        }

        return memberDto;
    }

    // 회원 가입
//    @PostMapping("/join")
//    public ResponseEntity<String> join(@RequestBody MemberDto memberDto) {
//        try {
//            System.out.println("memberEmail: " + memberDto.getMemberEmail()); // 디버그용 로그
//
//            // 이메일 인증 여부 확인
//            CertDto certDto = certDao.findByEmail(memberDto.getMemberEmail());
//            if (certDto == null || memberDto.getVerificationToken() == null || !memberDto.getVerificationToken().equals(certDto.getCertNumber())) {
//                System.out.println("인증 토큰이 올바르지 않습니다.");
//                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("인증 토큰이 올바르지 않습니다.");
//            }
//
//            // 비밀번호 암호화
//            memberDto.setMemberPw(encoder.encode(memberDto.getMemberPw()));
//
//            // 이메일 인증 확인 후 회원 정보 등록
//            memberDao.insert(memberDto);
//            return ResponseEntity.ok("회원가입이 완료되었습니다.");
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("회원가입 중 오류가 발생했습니다.");
//        }
//    }
    
 // 회원 가입
 	@PostMapping("/join")
 	public void join(@RequestBody MemberDto memberDto) {
 		memberDao.insert(memberDto);
 	}

    // 회원 가입 시 아이디 중복 검사
    @GetMapping("/checkId/{memberId}")
    public boolean checkId(@PathVariable String memberId) {
        return memberDao.checkId(memberId);
    }

    // 비밀번호 변경
    @PostMapping("/changePassword")
    public boolean changePassword(@RequestParam String memberId, @RequestParam String newPassword) {
        return memberDao.updatePassword(memberId, encoder.encode(newPassword));
    }

    // 회원 정보 수정
//    @PutMapping("/edit")
//    public boolean edit(@RequestBody MemberDto memberDto) {
//        return memberDao.updateMemberInfo(memberDto);
//    }
    
    @PutMapping("/edit")
    public boolean edit(
            @RequestPart("member") MemberDto memberDto,
            @RequestPart(value = "files", required = false) List<MultipartFile> files) throws IllegalStateException, IOException {
        // memberDto와 files를 사용하여 처리 로직 구현
        // 파일 처리를 추가로 구현할 수 있습니다.
        boolean result = memberDao.updateMemberInfo(memberDto);
        if(!result) {
        	throw new TargetNotFoundException("존재하지 않는 회원정보");
        }
        // 예시: 파일 업로드가 필요한 경우
        if (files != null && !files.isEmpty()) {
            for (MultipartFile file : files) {
                // 파일 저장 로직 구현
                int attachmentNo = attachmentService.save(file);
                
                MemberImageDto memberImageDto = new MemberImageDto();
                memberImageDto.setAttachment(attachmentNo);
                memberImageDto.setMemberId(memberDto.getMemberId());
                memberImageDao.insert(memberImageDto);
            }
        }
        return memberDao.updateMemberInfo(memberDto);
    }

    //게임플레이 점수에 따른 포인트증가
    @PatchMapping("/point")
    public boolean getPoint(@RequestBody MemberDto memberDto) {
    	boolean result = memberDao.updateMemberInfo(memberDto);
    	int point = playDao.getPoint(memberDto.getMemberId())+(communityDao.getCount(memberDto.getMemberId())*10);
    	memberDto.setMemberPoint(point);
    	return result;
    }
    
    // 회원 정보 삭제
    @DeleteMapping("/delete/{memberId}")
    public boolean delete(@PathVariable String memberId) {
        return memberDao.deleteMember(memberId);
    }

    //회원 이미지 조회
    @GetMapping("/image/{memberId}")
    	public MemberImageDto getMemberImages(@PathVariable String memberId){
    		return memberImageDao.selectone(memberId);
    	}
    //이미지 다운로드를 처리
    @GetMapping("/download/{attachment}")
    public ResponseEntity<ByteArrayResource> downloadImage(
    		@PathVariable int attachment) throws IOException{
    	return attachmentService.find(attachment);
    }
  //첨부파일을 하나씩 업로드하는 엔드포인트
    @PostMapping("/upload/{memberId}")
    public int uploadGameImage(
    		@PathVariable String memberId,
    		@RequestParam("file") MultipartFile file)
    		throws IllegalStateException, IOException{
    	//1. 첨부파일 저장
    	int attachment = attachmentService.save(file);
    	
    	//2. 게임 이미지 정보 저장
    	MemberImageDto memberImageDto = new MemberImageDto();
    	memberImageDto.setAttachment(attachment);
    	memberImageDto.setMemberId(memberId);
    	memberImageDao.insert(memberImageDto);
    	
    	return attachment;
    }
    // memberLevel = '관리자'인 회원 전용기능
    // memberLevel = '일반회원'의 정보수정
    @PostMapping("/developer-request")
    public ResponseEntity<?> requestDeveloperRole(@RequestBody Map<String, String> request) {
        String memberId = request.get("memberId");
        System.out.println("개발자 권한 요청 received - memberId: " + memberId); // 로그 추가
        
        try {
            MemberDto member = memberDao.selectOne(memberId);
            
            if (member == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                   .body("회원을 찾을 수 없습니다.");
            }
            
            if (!"일반회원".equals(member.getMemberLevel())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                   .body("일반회원만 개발자 권한을 요청할 수 있습니다.");
            }

            // 이미 요청이 있는지 확인
            if (member.getDeveloperRequest() == 1) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                                   .body("이미 개발자 권한 요청이 진행 중입니다.");
            }

            // 요청 정보 업데이트
            Map<String, Object> params = new HashMap<>();
            params.put("memberId", memberId);
            params.put("developerRequest", 1);
            // new Date() 대신 현재 시간을 얻는 다른 방법 사용
            params.put("developerRequestDate", new Date(System.currentTimeMillis()));
            
            boolean result = memberDao.updateDeveloperRequest(params);
            System.out.println("개발자 권한 요청 업데이트 결과: " + result); // 로그 추가
            
            if (result) {
                return ResponseEntity.ok("개발자 권한 요청이 완료되었습니다.");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                   .body("요청 처리 중 오류가 발생했습니다.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                               .body("서버 오류가 발생했습니다.");
        }
    }
		 
	 // 새로운 관리자용 회원정보 수정 엔드포인트
	 @PutMapping("/admin/edit/{memberId}")
	 public ResponseEntity<?> adminEditMember(
	         @PathVariable String memberId,
	         @RequestBody MemberDto memberDto,
	         @RequestHeader("Authorization") String token) {
	     try {
	         // 토큰에서 현재 로그인한 사용자 정보 추출
	         String accessToken = token.replace("Bearer ", "");
	         MemberClaimVO claimVO = tokenService.check(accessToken);
	         
	         // 관리자 권한 체크
	         MemberDto admin = memberDao.selectOne(claimVO.getMemberId());
	         if (!"관리자".equals(admin.getMemberLevel())) {
	             return ResponseEntity.status(HttpStatus.FORBIDDEN)
	                     .body("관리자만 접근 가능합니다.");
	         }
	
	         // URL의 memberId와 요청 바디의 memberId가 일치하는지 확인
	         if (!memberId.equals(memberDto.getMemberId())) {
	             return ResponseEntity.status(HttpStatus.BAD_REQUEST)
	                     .body("잘못된 회원 정보입니다.");
	         }
	
	         // 수정하려는 회원이 존재하는지 확인
	         MemberDto targetMember = memberDao.selectOne(memberId);
	         if (targetMember == null) {
	             return ResponseEntity.status(HttpStatus.NOT_FOUND)
	                     .body("존재하지 않는 회원입니다.");
	         }
	         
	         // 레벨 변경 유효성 검사
	         if (!memberDto.getMemberLevel().equals("일반회원") && 
	             !memberDto.getMemberLevel().equals("개발자")) {
	             return ResponseEntity.status(HttpStatus.BAD_REQUEST)
	                     .body("유효하지 않은 회원 레벨입니다.");
	         }
	
	         // 회원 정보 업데이트
	         boolean result = memberDao.updateMemberInfo(memberDto);
	         if (!result) {
	             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                     .body("회원 정보 수정에 실패했습니다.");
	         }
	
	         return ResponseEntity.ok()
	                 .body("회원 정보가 성공적으로 수정되었습니다.");
	
	     } catch (Exception e) {
	         log.error("회원 정보 수정 중 오류 발생", e);
	         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                 .body("회원 정보 수정 중 오류가 발생했습니다.");
	     }
	 }
 
	 @GetMapping("/notifications")
	 public ResponseEntity<List<MemberDto>> getDeveloperRequests(
	     @RequestHeader("Authorization") String token) {
	     try {
	         // 토큰에서 현재 로그인한 사용자 정보 추출
	         String accessToken = token.replace("Bearer ", "");
	         MemberClaimVO claimVO = tokenService.check(accessToken);
	         
	         // 관리자 권한 체크
	         MemberDto admin = memberDao.selectOne(claimVO.getMemberId());
	         if (!"관리자".equals(admin.getMemberLevel())) {
	             return ResponseEntity.status(HttpStatus.FORBIDDEN)
	                     .body(null);
	         }

	         // 개발자 권한 요청 목록 조회
	         List<MemberDto> developerRequests = memberDao.selectDeveloperRequests();
	         return ResponseEntity.ok(developerRequests);

	     } catch (Exception e) {
	         log.error("알림 조회 중 오류 발생", e);
	         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                 .body(null);
	     }
	 }
	 
	 @PutMapping("/admin/approve-developer/{memberId}")
	 public ResponseEntity<?> approveDeveloperRequest(
	         @PathVariable String memberId,
	         @RequestHeader("Authorization") String token) {
	     try {
	         // 토큰에서 현재 로그인한 사용자 정보 추출
	         String accessToken = token.replace("Bearer ", "");
	         MemberClaimVO claimVO = tokenService.check(accessToken);
	         
	         // 관리자 권한 체크
	         MemberDto admin = memberDao.selectOne(claimVO.getMemberId());
	         if (!"관리자".equals(admin.getMemberLevel())) {
	             return ResponseEntity.status(HttpStatus.FORBIDDEN)
	                     .body("관리자만 접근 가능합니다.");
	         }

	         // 승인할 회원 조회
	         MemberDto targetMember = memberDao.selectOne(memberId);
	         if (targetMember == null) {
	             return ResponseEntity.status(HttpStatus.NOT_FOUND)
	                     .body("존재하지 않는 회원입니다.");
	         }

	         // 개발자 권한 요청 상태 확인
	         if (targetMember.getDeveloperRequest() != 1) {
	             return ResponseEntity.status(HttpStatus.BAD_REQUEST)
	                     .body("개발자 권한 요청이 없는 회원입니다.");
	         }

	         // 회원 레벨 업데이트 및 요청 상태 초기화
	         Map<String, Object> updateParams = new HashMap<>();
	         updateParams.put("memberId", memberId);
	         updateParams.put("memberLevel", "개발자");
	         updateParams.put("developerRequest", 0);
	         updateParams.put("developerRequestDate", null);

	         boolean result = memberDao.updateMemberLevel(updateParams);
	         if (!result) {
	             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                     .body("권한 승인 처리 중 오류가 발생했습니다.");
	         }

	         return ResponseEntity.ok()
	                 .body("개발자 권한이 승인되었습니다.");

	     } catch (Exception e) {
	         log.error("개발자 권한 승인 중 오류 발생", e);
	         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                 .body("권한 승인 중 오류가 발생했습니다.");
	     }
	 }
	 
	 
	 
}