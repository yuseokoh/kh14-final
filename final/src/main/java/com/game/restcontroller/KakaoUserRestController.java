package com.game.restcontroller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.game.dao.KakaoUserDao;
import com.game.dao.MemberDao;
import com.game.dto.KakaoUserDto;
import com.game.dto.MemberDto;
import com.game.service.KakaoLoginService;
import com.game.service.KakaoTokenService;
import com.game.service.KakaoUserService;
import com.game.service.TokenService;
import com.game.vo.KakaoUserClaimVO;
import com.game.vo.MemberClaimVO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@CrossOrigin(origins = { "http://localhost:3000" })
@RestController
@RequestMapping("/kakao")
public class KakaoUserRestController {

	@Autowired
	private KakaoUserDao kakaoUserDao;

	@Autowired
	private KakaoLoginService kakaoLoginService;

	@Autowired
	private TokenService tokenService;

	@Autowired
	private MemberDao memberDao;

	@Autowired
	private KakaoUserService kakaoUserService;

	@Autowired
	private KakaoTokenService kakaoTokenService;
	
	@PostMapping("/login")
	public ResponseEntity<Map<String, Object>> kakaoLogin(@RequestBody Map<String, String> request) {
	    String code = request.get("code");
	    System.out.println("받은 인가 코드: " + code);

	    try {
	        // 1. 카카오 인가 코드를 사용해 액세스 토큰 가져오기
	        String accessToken = kakaoLoginService.getAccessToken(code);
	        System.out.println("발급된 액세스 토큰: " + accessToken);

	        // 2. 액세스 토큰을 사용해 카카오 사용자 정보 가져오기
	        KakaoUserDto kakaoUser = kakaoLoginService.handleKakaoLogin(accessToken);
	        System.out.println("카카오 사용자 정보: " + kakaoUser);

	        // 3. JWT 토큰 및 리프레시 토큰 생성
	        KakaoUserClaimVO claimVO = new KakaoUserClaimVO();
	        claimVO.setKakaoId(kakaoUser.getKakaoId());
	        claimVO.setMemberLevel("카카오 회원");  // 카카오 유저 기본 권한 레벨 설정

	        // Access Token 생성
	        String accessJwtToken = kakaoTokenService.createKakaoAccessToken(claimVO);

	        // Refresh Token 생성
	        String refreshJwtToken = kakaoTokenService.createRefreshToken(claimVO);

	        // 4. 로그인 시간 갱신
	        memberDao.updateLastLogin(kakaoUser.getKakaoId());

	        // 5. 이메일이 필요한 경우 이메일 입력 페이지로 이동하도록 응답 생성
	        Map<String, Object> responseBody = new HashMap<>();
	        if (kakaoUser.isEmailRequired()) {
	            responseBody.put("emailRequired", true);
	            responseBody.put("kakaoId", kakaoUser.getKakaoId());  // kakaoId를 응답에 포함
	        } else {
	            responseBody.put("emailRequired", false);  // 이메일 입력 필요 여부
	        }

	        // 6. 응답 데이터를 구성하여 클라이언트에 전달
	        responseBody.put("jwtToken", accessJwtToken);           // Access JWT 토큰
	        responseBody.put("refreshToken", refreshJwtToken);      // Refresh Token
	        responseBody.put("accessToken", accessToken);
	        responseBody.put("kakaoId", kakaoUser.getKakaoId());    // 카카오 사용자 ID
	        responseBody.put("nickname", kakaoUser.getMemberNickname());  // 사용자 닉네임

	        // 7. 응답으로 데이터를 전달
	        return ResponseEntity.ok(responseBody);

	    } catch (Exception e) {
	        log.error("Error during Kakao login", e);
	        return ResponseEntity.status(500).body(Map.of("error", "Kakao login failed"));
	    }
	}


	
	
	
//	// 카카오 로그인
//	@PostMapping("/login")
//	public MemberLoginResponseVO login(@RequestBody MemberLoginRequestVO vo) {
//	    // 카카오 로그인 처리
//	    String code = vo.getCode(); // MemberLoginRequestVO에 'code' 필드 추가 필요
//	    log.info("받은 인가 코드: {}", code);
//
//	    try {
//	        // 인가 코드가 유효한지 확인
//	        if (code == null || code.isEmpty()) {
//	            log.error("인가 코드가 null이거나 비어있습니다.");
//	            throw new IllegalStateException("유효한 인가 코드를 제공해야 합니다.");
//	        }
//
//	        // 디버깅 로그 추가
//	        log.debug("getAccessToken 메서드 호출 준비 - 인가 코드: {}", code);
//
//	        // 액세스 토큰 요청
//	        String accessToken = kakaoLoginService.getAccessToken(code);
//	        log.debug("발급된 액세스 토큰: {}", accessToken);
//
//	        KakaoUserDto kakaoUser = kakaoLoginService.handleKakaoLogin(accessToken);
//	        if (kakaoUser == null || kakaoUser.getKakaoId() == null) {
//	            throw new IllegalStateException("카카오 사용자 정보를 가져오지 못했습니다.");
//	        }
//
//	        // 디버깅 로그 추가
//	        log.info("카카오 사용자 정보: {}", kakaoUser);
//	        log.info("카카오 ID: {}", kakaoUser.getKakaoId());
//
//	        // 카카오 사용자 정보를 DB에 저장 또는 업데이트
//	        kakaoUserService.saveOrUpdateKakaoUser(kakaoUser);
//
//	        // 이메일이 필요한 경우
//	        if (kakaoUser.isEmailRequired()) {
//	            MemberLoginResponseVO response = new MemberLoginResponseVO();
//	            response.setEmailRequired(true);
//	            response.setKakaoId(kakaoUser.getKakaoId());
//	            return response;
//	        }
//
//	        // 2. 토큰 생성
//	        // DB에서 MemberClaimVO 조회
//	        MemberClaimVO claimVO = kakaoUserDao.selectOneByKakaoId2(kakaoUser.getKakaoId());
//	        if (claimVO == null) {
//	            throw new IllegalStateException("카카오 사용자 정보를 찾을 수 없습니다.");
//	        }
//
//	        // 생성된 토큰 로그 추가
//	        log.debug("Claim VO: {}", claimVO);
//
//	        // MemberLoginResponseVO에 Access Token 및 Refresh Token 설정
//	        MemberLoginResponseVO response = new MemberLoginResponseVO();
//	        response.setMemberId(claimVO.getMemberId());
//	        response.setMemberLevel(claimVO.getMemberLevel());
//	        response.setKakaoId(claimVO.getKakaoId());
//	        response.setAccessToken(tokenService.createAccessToken(claimVO));
//	        response.setRefreshToken(tokenService.createRefreshToken(claimVO));
//
//	        log.debug("생성된 Access Token: {}", response.getAccessToken());
//	        log.debug("생성된 Refresh Token: {}", response.getRefreshToken());
//
//	        // 마지막 로그인 및 로그아웃 시간 업데이트
//	        memberDao.updateLastLogin(claimVO.getMemberId());
//	        memberDao.updateLogoutTime(claimVO.getMemberId());
//	        return response;
//
//	    } catch (Exception e) {
//	        log.error("카카오 로그인 중 오류 발생: {}", e.getMessage());
//	        throw new IllegalStateException("카카오 로그인 실패", e);
//	    }
//	}





//	@PostMapping("/login")
//	public ResponseEntity<Map<String, Object>> kakaoLogin(@RequestBody Map<String, String> request) {
//	    String code = request.get("code");
//	    log.info("받은 인가 코드: {}", code);
//
//	    try {
//	        // 1. 카카오 인가 코드를 사용해 액세스 토큰 가져오기
//	        String accessToken = kakaoLoginService.getAccessToken(code);
//	        log.info("발급된 액세스 토큰: {}", accessToken);
//
//	        // 2. 액세스 토큰을 사용해 카카오 사용자 정보 가져오기
//	        KakaoUserDto kakaoUser = kakaoLoginService.handleKakaoLogin(accessToken);
//	        log.info("kakaoLogin - 가져온 이메일 값: {}", kakaoUser.getMemberEmail());
//	        log.info("카카오 사용자 정보: {}", kakaoUser);
//
//	        // 3. 사용자 정보를 저장 또는 업데이트
//	        log.info("saveOrUpdateKakaoUser 호출 전");
//	        kakaoUserService.saveOrUpdateKakaoUser(kakaoUser);
//	        log.info("saveOrUpdateKakaoUser 호출 후");
//
//	        // 4. 이메일이 필요한 경우 이메일 입력 페이지로 이동하도록 처리
//	        if (kakaoUser.isEmailRequired()) {
//	            log.info("이메일 입력이 필요합니다. 이메일 입력 페이지로 이동합니다.");
//	            Map<String, Object> responseBody = new HashMap<>();
//	            responseBody.put("emailRequired", true);
//	            responseBody.put("kakaoId", kakaoUser.getKakaoId());
//	            return ResponseEntity.ok(responseBody);
//	        }
//
//	        // 5. JWT 토큰 및 리프레시 토큰 생성
//	        KakaoUserClaimVO claimVO = new KakaoUserClaimVO();
//	        claimVO.setKakaoId(kakaoUser.getKakaoId());
//	        claimVO.setMemberLevel("카카오 회원");
//
//	        // Access Token 생성
//	        log.info("createKakaoAccessToken 호출 전");
//	        String accessJwtToken = tokenService.createKakaoAccessToken(claimVO);
//	        log.info("createKakaoAccessToken 호출 후");
//	        log.info("생성된 Access JWT 토큰: {}", accessJwtToken);
//
//	        // Refresh Token 생성
//	        String refreshJwtToken = tokenService.createRefreshToken(kakaoUser);
//	        log.info("생성된 Refresh JWT 토큰: {}", refreshJwtToken);
//
//	        // 토큰 생성이 실패했는지 검증
//	        if (accessJwtToken == null || refreshJwtToken == null) {
//	            throw new RuntimeException("Token generation failed");
//	        }
//
//	        // 6. 로그인 시간 갱신
//	        log.info("로그인 시간 갱신 중...");
//	        memberDao.updateLastLogin(kakaoUser.getKakaoId());
//	        log.info("로그인 시간 갱신 완료");
//
//	        // 7. 응답 데이터를 구성하여 클라이언트에 전달
//	        Map<String, Object> responseBody = new HashMap<>();
//	        responseBody.put("accessToken", accessToken);
//	        responseBody.put("jwtToken", accessJwtToken);
//	        responseBody.put("refreshToken", refreshJwtToken);
//	        responseBody.put("emailRequired", false);
//	        responseBody.put("kakaoId", kakaoUser.getKakaoId());
//	        responseBody.put("nickname", kakaoUser.getMemberNickname());
//	        log.info("응답 데이터: {}", responseBody);
//
//	        // 8. 응답으로 데이터를 전달
//	        return ResponseEntity.ok(responseBody);
//
//	    } catch (Exception e) {
//	        log.error("Error during Kakao login", e);
//	        return ResponseEntity.status(500).body(Map.of("error", "Kakao login failed: " + e.getMessage()));
//	    }
//	}




//    @PostMapping("/login")
//    public ResponseEntity<Map<String, Object>> kakaoLogin(@RequestBody Map<String, String> request) {
//        String code = request.get("code");
//        System.out.println("받은 인가 코드: " + code);
//        try {
//            // 1. 카카오 인가 코드를 사용해 액세스 토큰 가져오기
//            String accessToken = kakaoLoginService.getAccessToken(code);
//            System.out.println("발급된 액세스 토큰: " + accessToken);
//          
//            // 2. 액세스 토큰을 사용해 카카오 사용자 정보 가져오기
//            KakaoUserDto kakaoUser = kakaoLoginService.handleKakaoLogin(accessToken);
//            log.info("kakaoLogin - 가져온 이메일 값: {}", kakaoUser.getMemberEmail());
//            System.out.println("카카오 사용자 정보: " + kakaoUser);
//
//            // 3. 이메일이 필요한 경우 이메일 입력 페이지로 이동하도록 처리
//            if (kakaoUser.isEmailRequired()) {
//                Map<String, Object> responseBody = new HashMap<>();
//                responseBody.put("emailRequired", true);
//                responseBody.put("kakaoId", kakaoUser.getKakaoId());  // kakaoId를 응답에 포함
//                return ResponseEntity.ok(responseBody);  // 이메일 입력 페이지로 이동하도록 응답
//            }
//
//            // 4. JWT 토큰 및 리프레시 토큰 생성
//            // Access Token 및 Refresh Token 생성
//            KakaoUserClaimVO claimVO = new KakaoUserClaimVO();
//            claimVO.setKakaoId(kakaoUser.getKakaoId());
//            claimVO.setMemberLevel("카카오 회원");  // 카카오 유저 기본 권한 레벨 설정
//
//            // Access Token 생성
//            String accessJwtToken = tokenService.createKakaoAccessToken(claimVO);
//
//            // Refresh Token 생성
//            String refreshJwtToken = tokenService.createRefreshToken(kakaoUser);
//
//            // 5. 로그인 시간 갱신
//            memberDao.updateLastLogin(kakaoUser.getKakaoId());
//
//            // 6. 응답 데이터를 구성하여 클라이언트에 전달
//            Map<String, Object> responseBody = new HashMap<>();
//            responseBody.put("jwtToken", accessJwtToken);           // Access JWT 토큰
//            responseBody.put("refreshToken", refreshJwtToken);      // Refresh Token
//            responseBody.put("accessToken", accessToken);
//            responseBody.put("emailRequired", false);               // 이메일 입력 필요 여부
//            responseBody.put("kakaoId", kakaoUser.getKakaoId());    // 카카오 사용자 ID
//            responseBody.put("nickname", kakaoUser.getMemberNickname());  // 사용자 닉네임
//
//            // 7. 응답으로 데이터를 전달
//            return ResponseEntity.ok(responseBody);
//
//        } catch (Exception e) {
//            log.error("Error during Kakao login", e);
//            return ResponseEntity.status(500).body(Map.of("error", "Kakao login failed"));
//        }
//    }

//    @PostMapping("/login")
//    public ResponseEntity<Map<String, Object>> kakaoLogin(@RequestBody Map<String, String> request) {
//        String code = request.get("code");
//        System.out.println("받은 인가 코드: " + code);
//        try {
//            // 1. 카카오 인가 코드를 사용해 액세스 토큰 가져오기
//            String accessToken = kakaoLoginService.getAccessToken(code);
//            System.out.println("발급된 액세스 토큰: " + accessToken);
//
//            // 2. 액세스 토큰을 사용해 카카오 사용자 정보 가져오기
//            KakaoUserDto kakaoUser = kakaoLoginService.handleKakaoLogin(accessToken);
//            System.out.println("카카오 사용자 정보: " + kakaoUser);
//            // 카카오 사용자에 대한 액세스 토큰 생성
//            
//            // 응답 데이터 준비
//            Map<String, Object> responseBody = new HashMap<>();
//
//            // 3. 이메일이 필요한 경우 이메일 입력 페이지로 이동하도록 처리
//            if (kakaoUser.isEmailRequired()) {
//                responseBody.put("emailRequired", true);
//                responseBody.put("kakaoId", kakaoUser.getKakaoId());  // kakaoId를 응답에 포함
//                return ResponseEntity.ok(responseBody);  // 이메일 입력 페이지로 이동하도록 응답
//            }
//
//            // 4. 이메일 입력이 필요하지 않은 경우 JWT 토큰 생성
//            String jwtToken = kakaoLoginService.createJwtToken(kakaoUser);
//            // 5. Refresh Token 생성
//            String refreshToken = tokenService.createRefreshToken(kakaoUser);
//
//            // 6. 응답 데이터를 구성하여 추가
//            responseBody.put("jwtToken", jwtToken);               // JWT 토큰
//            responseBody.put("accessToken", accessToken);         // 액세스 토큰
//            responseBody.put("refreshToken", refreshToken);       // Refresh Token 추가
//            responseBody.put("emailRequired", false);             // 이메일 입력 필요 여부
//            responseBody.put("kakaoId", kakaoUser.getKakaoId());  // 카카오 사용자 ID
//            responseBody.put("nickname", kakaoUser.getMemberNickname());  // 사용자 닉네임
//
//            // 7. 응답으로 데이터를 전달
//            return ResponseEntity.ok(responseBody);
//
//        } catch (Exception e) {
//            log.error("Error during Kakao login", e);
//            return ResponseEntity.status(500).body(Map.of("error", "Kakao login failed"));
//        }
//    }

// // 이메일 저장을 위한 엔드포인트
//    @PostMapping("/saveEmail")
//    public ResponseEntity<?> saveEmail(@RequestBody KakaoUserDto kakaoUserDto) {
//        try {
//            // 로그 추가: kakaoId와 email을 출력
//            log.info("Received kakaoId: {}, memberEmail: {}", kakaoUserDto.getKakaoId(), kakaoUserDto.getMemberEmail());
//            
//            // 이메일 저장 전에 카카오 유저가 있는지 확인하고 없으면 새로 추가
//            KakaoUserDto existingUser = kakaoUserService.saveOrUpdateKakaoUser(kakaoUserDto);
//            
//            // 이메일 업데이트 로직
//            existingUser.setMemberEmail(kakaoUserDto.getMemberEmail());
//            kakaoUserService.updateKakaoUserEmail(existingUser.getKakaoId(), existingUser.getMemberEmail());
//            
//            return ResponseEntity.ok(existingUser);
//        } catch (Exception e) {
//            log.error("이메일 저장 중 오류 발생", e);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("이메일 저장에 실패했습니다.");
//        }
//        
//    }

//    @PostMapping("/saveEmail")
//    public ResponseEntity<?> saveEmail(@RequestBody KakaoUserDto kakaoUserDto) {
//        try {
//            log.info("Received kakaoId: {}, memberEmail: {}", kakaoUserDto.getKakaoId(), kakaoUserDto.getMemberEmail());
//
//            // 이메일 유효성 검사
//            if (kakaoUserDto.getMemberEmail() == null || kakaoUserDto.getMemberEmail().isEmpty()) {
//                return ResponseEntity.badRequest().body("유효하지 않은 이메일입니다.");
//            }
//
//            // 멤버 테이블에서 이메일로 회원 찾기
//            MemberDto existingMember = memberDao.findByEmail(kakaoUserDto.getMemberEmail());
//            if (existingMember != null) {
//                log.info("이메일이 존재하므로 계정 연동을 시도합니다.");
//                
//             // 여기에 추가: linkKakaoAndMemberAccounts 호출 전 이메일 값 확인
//                log.info("linkKakaoAndMemberAccounts 호출 전 - kakaoId: {}, memberEmail: {}", kakaoUserDto.getKakaoId(), kakaoUserDto.getMemberEmail());
//
//                //여기까지는 이메일 정상전달 
//                
//                // 잘못된 값이 전달되지 않도록 올바른 이메일이 설정되어 있는지 확인합니다.
//                if (kakaoUserDto.getMemberEmail() == null || !kakaoUserDto.getMemberEmail().contains("@")) {
//                    log.warn("잘못된 이메일 값이 감지되었습니다: {}", kakaoUserDto.getMemberEmail());
//                    return ResponseEntity.badRequest().body("유효하지 않은 이메일 주소입니다.");
//                }
//                
//                // 카카오 계정과 멤버 계정을 연동
//                kakaoUserService.linkKakaoAndMemberAccounts(kakaoUserDto.getKakaoId(), existingMember.getMemberId());
//
//                return ResponseEntity.ok(Map.of("success", true, "message", "Accounts linked successfully"));
//            } else {
//                log.info("이메일이 존재하지 않으므로 새 카카오 유저를 생성합니다.");
//                
//                // 새 카카오 유저 생성
//                kakaoUserService.createKakaoUser(kakaoUserDto);
//
//                return ResponseEntity.ok(Map.of("success", true, "message", "New account created successfully"));
//            }
//        } catch (Exception e) {
//            log.error("이메일 저장 중 오류 발생", e);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("이메일 저장에 실패했습니다.");
//        }
//    }

//    @PostMapping("/saveEmail")
//    public ResponseEntity<?> saveEmail(@RequestBody KakaoUserDto kakaoUserDto) {
//        try {
//            log.info("Received kakaoId: {}, memberEmail: {}", kakaoUserDto.getKakaoId(), kakaoUserDto.getMemberEmail());
//
//            // 이메일 유효성 검사
//            if (kakaoUserDto.getMemberEmail() == null || kakaoUserDto.getMemberEmail().isEmpty()) {
//                return ResponseEntity.badRequest().body("유효하지 않은 이메일입니다.");
//            }
//
//            // 멤버 테이블에서 이메일로 회원 찾기
//            MemberDto existingMember = memberDao.findByEmail(kakaoUserDto.getMemberEmail());
//            if (existingMember != null) {
//                log.info("이메일이 존재하므로 계정 연동을 시도합니다.");
//
//                // 링크 호출 전 이메일 값 확인
//                log.info("linkKakaoAndMemberAccounts 호출 전 - kakaoId: {}, memberEmail: {}", kakaoUserDto.getKakaoId(), kakaoUserDto.getMemberEmail());
//
//                // 올바른 이메일 값이 전달되는지 확인
//                kakaoUserService.linkKakaoAndMemberAccounts(kakaoUserDto.getKakaoId(), kakaoUserDto.getMemberEmail());
//
//                return ResponseEntity.ok(Map.of("success", true, "message", "Accounts linked successfully"));
//            } else {
//                log.info("이메일이 존재하지 않으므로 새 카카오 유저를 생성합니다.");
//
//                // 새 카카오 유저 생성
//                kakaoUserService.createKakaoUser(kakaoUserDto);
//
//                return ResponseEntity.ok(Map.of("success", true, "message", "New account created successfully"));
//            }
//        } catch (Exception e) {
//            log.error("이메일 저장 중 오류 발생", e);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("이메일 저장에 실패했습니다.");
//        }
//    }

//    @PostMapping("/saveEmail")
//    public ResponseEntity<?> saveEmail(@RequestBody KakaoUserDto kakaoUserDto) {
//        try {
//            log.info("Received kakaoId: {}, memberEmail: {}", kakaoUserDto.getKakaoId(), kakaoUserDto.getMemberEmail());
//
//            // 이메일 유효성 검사
//            if (kakaoUserDto.getMemberEmail() == null || kakaoUserDto.getMemberEmail().isEmpty()) {
//                return ResponseEntity.badRequest().body("유효하지 않은 이메일입니다.");
//            }
//
//            // 카카오 유저 테이블의 이메일을 업데이트
//            log.info("카카오 유저 테이블의 이메일을 갱신 시도 - kakaoId: {}, memberEmail: {}", kakaoUserDto.getKakaoId(), kakaoUserDto.getMemberEmail());
//            kakaoUserDao.updateKakaoUserEmail(kakaoUserDto.getKakaoId(), kakaoUserDto.getMemberEmail());
//            log.info("카카오 유저 테이블의 이메일 갱신 완료");
//
//            // 멤버 테이블에서 이메일로 회원 찾기
//            MemberDto existingMember = memberDao.findByEmail(kakaoUserDto.getMemberEmail());
//            if (existingMember != null) {
//                log.info("이메일이 존재하므로 계정 연동을 시도합니다.");
//
//                // 링크 호출 전 이메일 값 확인
//                log.info("linkKakaoAndMemberAccounts 호출 전 - kakaoId: {}, memberEmail: {}", kakaoUserDto.getKakaoId(), kakaoUserDto.getMemberEmail());
//
//                // 계정 연동
//                kakaoUserService.linkKakaoAndMemberAccounts(kakaoUserDto.getKakaoId(), kakaoUserDto.getMemberEmail());
//
//                return ResponseEntity.ok(Map.of("success", true, "message", "Accounts linked successfully"));
//            } else {
//                log.info("이메일이 존재하지 않으므로 새 카카오 유저를 생성합니다.");
//
//                // 새 카카오 유저 생성
//                kakaoUserService.createKakaoUser(kakaoUserDto);
//
//                return ResponseEntity.ok(Map.of("success", true, "message", "New account created successfully"));
//            }
//        } catch (Exception e) {
//            log.error("이메일 저장 중 오류 발생", e);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("이메일 저장에 실패했습니다.");
//        }
//    }

	@PostMapping("/saveEmail")
	public ResponseEntity<Map<String, Object>> saveEmail(@RequestBody KakaoUserDto kakaoUserDto) {
		Map<String, Object> response = new HashMap<>();
		try {
			log.info("Received kakaoId: {}, memberEmail: {}", kakaoUserDto.getKakaoId(), kakaoUserDto.getMemberEmail());

			// 기존 이메일 확인
			KakaoUserDto existingUser = kakaoUserService.findKakaoUserById(kakaoUserDto.getKakaoId());
			if (existingUser != null && !existingUser.getMemberEmail().startsWith("no-email@")) {
				// 이미 이메일이 등록된 경우
				response.put("success", false);
				response.put("message", "이미 이메일이 등록되었습니다.");
				response.put("emailAlreadyRegistered", true); // 이메일이 이미 등록된 경우의 플래그 추가
				return ResponseEntity.ok(response); // 상태 코드를 200으로 반환
			}
			
			 // 이메일 업데이트
            kakaoUserDao.updateKakaoUserEmail(kakaoUserDto.getKakaoId(), kakaoUserDto.getMemberEmail());

			// 이메일 유효성 검사
			if (kakaoUserDto.getMemberEmail() == null || kakaoUserDto.getMemberEmail().isEmpty()) {
				response.put("success", false);
				response.put("message", "유효하지 않은 이메일입니다.");
				return ResponseEntity.badRequest().body(response);
			}

			// 카카오 유저 테이블의 이메일 갱신
			log.info("카카오 유저 테이블의 이메일을 갱신 시도 - kakaoId: {}, memberEmail: {}", kakaoUserDto.getKakaoId(),
					kakaoUserDto.getMemberEmail());
			kakaoUserDao.updateKakaoUserEmail(kakaoUserDto.getKakaoId(), kakaoUserDto.getMemberEmail());
			log.info("카카오 유저 테이블의 이메일 갱신 완료");

			// 멤버 테이블에서 이메일로 회원 찾기
			MemberDto existingMember = memberDao.findByEmail(kakaoUserDto.getMemberEmail());
			if (existingMember != null) {
				log.info("이메일이 존재하므로 계정 연동을 시도합니다.");
				kakaoUserService.linkKakaoAndMemberAccounts(kakaoUserDto.getKakaoId(), kakaoUserDto.getMemberEmail());
				response.put("success", true);
				response.put("message", "Accounts linked successfully");
				return ResponseEntity.ok(response);
			} else {
				log.info("이메일이 존재하지 않으므로 새 카카오 유저를 생성합니다.");
				kakaoUserService.createKakaoUser(kakaoUserDto);
				response.put("success", true);
				response.put("message", "New account created successfully");
				return ResponseEntity.ok(response);
			}
		} catch (Exception e) {
			log.error("이메일 저장 중 오류 발생", e);
			response.put("success", false);
			response.put("message", "이메일 저장에 실패했습니다.");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}

	// 유저 저장 또는 업데이트 로직을 통합
//    private void saveOrUpdateKakaoUser(KakaoUserDto kakaoUserDto) {
//        Optional<KakaoUserDto> existingUser = kakaoUserDao.selectOneByKakaoId(kakaoUserDto.getKakaoId());
//
//        if (!existingUser.isPresent()) {
//            // 유저가 존재하지 않으면 새로운 유저로 저장
//            kakaoUserDao.insert(kakaoUserDto);
//        } else {
//            // 유저가 존재하면 정보 업데이트
//            kakaoUserDao.updateKakaoUser(kakaoUserDto);
//        }
//    }

	@GetMapping("/find/{kakaoId}")
	public KakaoUserDto find(@PathVariable String kakaoId) {
		return kakaoUserDao.selectOneByKakaoId(kakaoId).orElse(null);
	}

	@GetMapping("/verify")
	public MemberClaimVO verifyToken(@RequestHeader("Authorization") String token) {
		if (tokenService.isBearerToken(token)) {
			String strippedToken = tokenService.removeBearer(token);
			return tokenService.check(strippedToken);
		}
		throw new IllegalArgumentException("잘못된 토큰 형식입니다.");
	}

	@PostMapping("/logout")
	public ResponseEntity<String> logoutUser(@RequestHeader("Authorization") String token) {
		try {
			if (tokenService.isBearerToken(token)) {
				String strippedToken = tokenService.removeBearer(token);
				tokenService.invalidateToken(strippedToken); // 토큰 무효화 처리
			}
			return ResponseEntity.ok("로그아웃 성공");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("로그아웃 중 오류 발생");
		}
	}

//    @PostMapping("/link")
//    public ResponseEntity<String> linkAccounts(@RequestBody Map<String, String> request) {
//        String email = request.get("email");
//        log.info("linkAccounts 메서드 호출됨, 전달된 이메일: {}", email); // 이메일 값 로깅
//        try {
//            kakaoUserService.linkKakaoAndMemberAccounts(email);
//            log.info("계정 연동 성공");
//            return ResponseEntity.ok("Accounts linked successfully");
//        } catch (Exception e) {
//            log.error("계정 연동 중 오류 발생", e);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to link accounts");
//        }
//    }

}
