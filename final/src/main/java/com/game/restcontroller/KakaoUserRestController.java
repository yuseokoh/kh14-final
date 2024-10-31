package com.game.restcontroller;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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
import com.game.service.KakaoLoginService;
import com.game.service.KakaoUserService;
import com.game.service.TokenService;
import com.game.vo.KakaoUserClaimVO;
import com.game.vo.MemberClaimVO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@CrossOrigin(origins = {"http://localhost:3000"})
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

            // 3. 이메일이 필요한 경우 이메일 입력 페이지로 이동하도록 처리
            if (kakaoUser.isEmailRequired()) {
                Map<String, Object> responseBody = new HashMap<>();
                responseBody.put("emailRequired", true);
                responseBody.put("kakaoId", kakaoUser.getKakaoId());  // kakaoId를 응답에 포함
                return ResponseEntity.ok(responseBody);  // 이메일 입력 페이지로 이동하도록 응답
            }

            // 4. JWT 토큰 및 리프레시 토큰 생성
            // Access Token 및 Refresh Token 생성
            KakaoUserClaimVO claimVO = new KakaoUserClaimVO();
            claimVO.setKakaoId(kakaoUser.getKakaoId());
            claimVO.setMemberLevel("BASIC");  // 카카오 유저 기본 권한 레벨 설정

            // Access Token 생성
            String accessJwtToken = tokenService.createKakaoAccessToken(claimVO);

            // Refresh Token 생성
            String refreshJwtToken = tokenService.createRefreshToken(kakaoUser);

            // 5. 로그인 시간 갱신
            memberDao.updateLastLogin(kakaoUser.getKakaoId());

            // 6. 응답 데이터를 구성하여 클라이언트에 전달
            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("jwtToken", accessJwtToken);           // Access JWT 토큰
            responseBody.put("refreshToken", refreshJwtToken);      // Refresh Token
            responseBody.put("accessToken", accessToken);
            responseBody.put("emailRequired", false);               // 이메일 입력 필요 여부
            responseBody.put("kakaoId", kakaoUser.getKakaoId());    // 카카오 사용자 ID
            responseBody.put("nickname", kakaoUser.getMemberNickname());  // 사용자 닉네임

            // 7. 응답으로 데이터를 전달
            return ResponseEntity.ok(responseBody);

        } catch (Exception e) {
            log.error("Error during Kakao login", e);
            return ResponseEntity.status(500).body(Map.of("error", "Kakao login failed"));
        }
    }

    
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

    





 // 이메일 저장을 위한 엔드포인트
    @PostMapping("/saveEmail")
    public ResponseEntity<?> saveEmail(@RequestBody KakaoUserDto kakaoUserDto) {
        try {
            // 로그 추가: kakaoId와 email을 출력
            log.info("Received kakaoId: {}, memberEmail: {}", kakaoUserDto.getKakaoId(), kakaoUserDto.getMemberEmail());
            
            // 이메일 저장 전에 카카오 유저가 있는지 확인하고 없으면 새로 추가
            KakaoUserDto existingUser = kakaoUserService.saveOrUpdateKakaoUser(kakaoUserDto);
            
            // 이메일 업데이트 로직
            existingUser.setMemberEmail(kakaoUserDto.getMemberEmail());
            kakaoUserService.updateKakaoUserEmail(existingUser.getKakaoId(), existingUser.getMemberEmail());
            
            return ResponseEntity.ok(existingUser);
        } catch (Exception e) {
            log.error("이메일 저장 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("이메일 저장에 실패했습니다.");
        }
        
    }



    // 유저 저장 또는 업데이트 로직을 통합
    private void saveOrUpdateKakaoUser(KakaoUserDto kakaoUserDto) {
        Optional<KakaoUserDto> existingUser = kakaoUserDao.selectOneByKakaoId(kakaoUserDto.getKakaoId());

        if (!existingUser.isPresent()) {
            // 유저가 존재하지 않으면 새로운 유저로 저장
            kakaoUserDao.insert(kakaoUserDto);
        } else {
            // 유저가 존재하면 정보 업데이트
            kakaoUserDao.updateKakaoUser(kakaoUserDto);
        }
    }

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

    
    
    
    
    
    
}
