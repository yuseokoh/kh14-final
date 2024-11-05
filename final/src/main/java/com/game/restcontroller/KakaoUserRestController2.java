//package com.game.restcontroller;
//
//import java.net.URISyntaxException;
//import java.util.HashMap;
//import java.util.Map;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.CrossOrigin;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import com.game.dao.CertDao;
//import com.game.dao.MemberDao;
//import com.game.dto.CertDto;
//import com.game.dto.MemberDto;
//import com.game.service.EmailService;
//import com.game.service.KakaoLoginService;
//import com.game.service.KakaoUserService;
//
//import lombok.extern.slf4j.Slf4j;
//
//@Slf4j
//@CrossOrigin
//@RestController
//@RequestMapping("/kakao2")
//public class KakaoUserRestController2 {
//	
//	@Autowired
//	private KakaoUserService kakaoUserService;
//	
//	@Autowired
//	private KakaoLoginService kakaoLoginService;
//	
//	@Autowired
//	private MemberDao memberDao;
//	
//	@Autowired
//	private EmailService emailService;
//	
//	@Autowired
//	private CertDao certDao;
//
//	@GetMapping("/login/{code}")
//	public MemberDto login(@PathVariable String code) throws URISyntaxException {
//		String accessToken = kakaoLoginService.getAccessToken(code);
//		
//		String kakaoId = kakaoLoginService.getKakaoId(accessToken);
//		
//		//[1] kakaoId를 가진 회원이 있는 경우 - 로그인으로 전환
//		MemberDto memberDto = memberDao.selectOneByKakaoId2(kakaoId);
//		//[2] kakaoId를 가진 회원이 없는 경우 - 신규 가입이 필요
//		if(memberDto == null) {
//			memberDto = new MemberDto();
//			memberDto.setKakaoId(kakaoId);//카카오아이디 추가
//		}
//		return memberDto;
//	}
//	
//	@PostMapping("/email/send")
//	public void sendEmail(@RequestBody Map<String, String> body) {
//		emailService.sendCert(body.get("certEmail"));
//	}
//	@PostMapping("/email/check")
//	public Map<String, Object> checkEmail(@RequestBody Map<String, String> body) {
//		//인증번호 검사
//		CertDto certDto = new CertDto();
//		certDto.setCertEmail(body.get("certEmail"));
//		certDto.setCertNumber(body.get("certNumber"));
//		boolean valid = certDao.check(certDto, 10);
//		
//		//이메일로 회원정보 찾기
//		MemberDto memberDto = memberDao.selectOneByEmail2(body.get("certEmail"));
//		
//		Map<String, Object> response = new HashMap<>();
//		response.put("valid", valid);
//		if(memberDto != null) {
//			response.put("member", memberDto);
//		}
//		return response;
//	}
//	
//}
