package com.game.restcontroller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
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
import org.springframework.web.bind.annotation.RestController;

import com.game.dao.CertDao;
import com.game.dao.MemberDao;
import com.game.dao.MemberTokenDao;
import com.game.dto.CertDto;
import com.game.dto.MemberDto;
import com.game.dto.MemberTokenDto;
import com.game.error.TargetNotFoundException;
import com.game.service.EmailService;
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

    @GetMapping("/find")
    public MemberDto find(@RequestHeader("Authorization") String accessToken) {
        if (tokenService.isBearerToken(accessToken) == false)
            throw new TargetNotFoundException("유효하지 않은 토큰");
        MemberClaimVO claimVO = tokenService.check(tokenService.removeBearer(accessToken));

        MemberDto memberDto = memberDao.selectOne(claimVO.getMemberId());
        if (memberDto == null)
            throw new TargetNotFoundException("존재하지 않는 회원");

        memberDto.setMemberPw(null);
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
    @PutMapping("/edit")
    public boolean edit(@RequestBody MemberDto memberDto) {
        return memberDao.updateMemberInfo(memberDto);
    }

    // 회원 정보 삭제
    @DeleteMapping("/delete/{memberId}")
    public boolean delete(@PathVariable String memberId) {
        return memberDao.deleteMember(memberId);
    }

    

}
