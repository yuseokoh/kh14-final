package com.game.restcontroller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.game.dao.CertDao;
import com.game.dto.CertDto;
import com.game.service.EmailService;

import jakarta.mail.MessagingException;

@CrossOrigin(origins = {"http://localhost:3000"})
@RestController
@RequestMapping("/rest/cert")
public class CertRestController {
	
	@Autowired
	private EmailService emailService;
	@Autowired
	private CertDao certDao;
	
	//사용자가 요구하는 이메일로 인증메일을 발송하는 기능
	@PostMapping("/send")
	public void send(@RequestParam String certEmail) throws MessagingException {
	    emailService.sendSignupInvitation(certEmail); // 수정된 메서드 호출
	}
	
	//사용자가 입력한 인증번호가 유효한지를 판정하는 기능
	@PostMapping("/check")
	public boolean check(@ModelAttribute CertDto certDto) {
		boolean result = certDao.check(certDto, 10);
		if(result) {//인증성공시
			certDao.delete(certDto.getCertEmail());//인증번호삭제
		}
		return result;
	}
}