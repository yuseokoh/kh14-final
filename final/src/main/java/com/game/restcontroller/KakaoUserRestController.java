package com.game.restcontroller;

import java.net.URISyntaxException;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.game.error.TargetNotFoundException;
import com.game.service.KakaoLoginService;
import com.game.service.KakaoUserService;
import com.game.service.TokenService;
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
    public String kakaoLogin(@RequestBody Map<String, String> request) throws URISyntaxException {
        String code = request.get("code");
        System.out.println("Received login request with code: " + code);

        // 카카오로부터 액세스 토큰 발급
        String accessToken = kakaoLoginService.getAccessToken(code);
        System.out.println("Access Token received: " + accessToken);

        // 사용자 정보 가져오기
        KakaoUserDto kakaoUser = kakaoLoginService.getUserInfo(accessToken);
        System.out.println("User Info received: " + kakaoUser);

        if (kakaoUser == null) {
            throw new TargetNotFoundException("카카오 사용자 정보를 찾을 수 없습니다.");
        }

        // 중복된 사용자 삽입 방지
        Optional<KakaoUserDto> existingKakaoUser = kakaoUserDao.selectOneByKakaoId(kakaoUser.getKakaoId());
        if (!existingKakaoUser.isPresent()) {
            kakaoUserDao.insert(kakaoUser);
        } else {
            kakaoUserDao.updateKakaoUser(kakaoUser); // 이미 존재하는 유저의 정보 업데이트
        }

        // 이메일이 null인 경우 이메일 입력 페이지로 리다이렉트
        if (kakaoUser.getMemberEmail() == null) {
            return "redirect:/email-input"; // 이메일 입력 페이지로 리다이렉트
        }

        // 기존 회원이 있는 경우 kakao_user_id를 연동
        Optional<MemberDto> existingMember = memberDao.selectOneByEmail(kakaoUser.getMemberEmail());
        if (existingMember.isPresent()) {
            MemberDto member = existingMember.get();
            memberDao.updateKakaoUserId(member.getMemberId(), kakaoUser.getKakaoUserId());
        } else {
            // 새로운 카카오 회원인 경우 회원가입 처리
            memberDao.insertWithKakao(kakaoUser);
        }

        // JWT 토큰 생성 및 반환
        MemberClaimVO claimVO = new MemberClaimVO();
        claimVO.setMemberId(kakaoUser.getKakaoId());
        claimVO.setMemberLevel("USER");

        String jwtToken = tokenService.createAccessToken(claimVO);
        System.out.println("Generated JWT Token: " + jwtToken);

        return jwtToken;
    }


    // 이메일 저장을 위한 엔드포인트
    @PostMapping("/saveEmail")
    public ResponseEntity<?> saveEmail(@RequestBody KakaoUserDto kakaoUserDto) {
        try {
            // 서비스 레이어에서 이메일 저장 처리
            KakaoUserDto updatedUser = kakaoUserService.updateKakaoUserEmail(kakaoUserDto.getKakaoId(), kakaoUserDto.getMemberEmail());
            // 성공적으로 업데이트된 경우 응답 반환
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("이메일 저장에 실패했습니다.");
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
}
