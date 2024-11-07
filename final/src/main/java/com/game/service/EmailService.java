package com.game.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.game.dao.CertDao;
import com.game.dto.CertDto;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender sender;

    @Autowired
    private CertDao certDao;

    @Autowired
    private RandomService randomService;
    
	public void sendCert(String email) {
		//인증번호 생성
		String value = randomService.generateNumber(6);
		
		//메세지 생성
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(email);
		message.setSubject("인증번호 안내");
		message.setText("인증번호는 ["+value+"] 입니다.");
		
		//메세지 전송
		sender.send(message);
		
		//DB 기록 남기기
		certDao.delete(email);
		CertDto certDto = new CertDto();
		certDto.setCertEmail(email);
		certDto.setCertNumber(value);
		certDao.insert(certDto);
	}

    // 회원가입 초대 이메일 발송 서비스
    public void sendSignupInvitation(String email) throws MessagingException {
        // 인증번호 생성
        String certNumber = randomService.generateNumber(6);
        
        // 회원가입 URL에 인증번호 추가
        String signupUrl = "http://192.168.30.58:3000/member/SignupForm?certNumber=" + certNumber + "&certEmail=" + email;
        
     // 이메일 본문 HTML 템플릿 작성
        String emailContent = "<div style=\"font-family: Arial, sans-serif; background-color: #1b2838; color: #ffffff; margin: 0; padding: 0;\">" +
                "<div style=\"max-width: 600px; margin: 0 auto; background-color: #2a475e; padding: 20px; border-radius: 8px; text-align: center;\">" +
                "<div style=\"margin: 20px 0;\">" +
                "<img src=\"https://store.cloudflare.steamstatic.com/public/shared/images/header/logo_steam.svg?t=962016\" alt=\"Steam Logo\" style=\"max-width: 100%; height: auto;\">" +
                "</div>" +
                "<h1 style=\"font-size: 1.8rem; margin-bottom: 20px;\">계속해서 새로운 Steam 계정을 만들려면 아래에서 이메일 주소를 인증해 주세요.</h1>" +
                "<a href=\"" + signupUrl + "\" style=\"display: inline-block; margin: 20px 0; padding: 15px 30px; font-size: 1rem; color: #ffffff; background-color: #1d9bf0; text-decoration: none; border-radius: 4px;\">이메일 주소 인증하기</a>" +
                "<p>Steam Guard 보안, Steam 커뮤니티 장터, Steam 거래와 같은 Steam의 기능을 최대한 활용하고 계정을 안전하게 복구하려면 이메일 주소를 인증해야 합니다.</p>" +
                "<h3>이메일 환경 설정 관리</h3>" +
                "<p>Valve는 때때로 Steam의 게임과 이벤트에 대한 정보를 제공하기 위해 이메일을 보내드리기도 합니다. 이러한 이메일을 받고 싶지 않으시거나 이메일 발송 조건을 변경하고 싶으신 경우, 계정을 생성하신 후 <a href=\"#\" style=\"color: #61dafb; text-decoration: none;\">여기</a>로 이동하여 이메일 환경 설정을 변경하실 수 있습니다.</p>" +
                "<p>최근에 이 이메일 주소로 새로운 계정을 만들려고 한 적이 없다면 이 이메일을 무시하셔도 됩니다.</p>" +
                "<div style=\"font-size: 0.9rem; color: #cccccc; margin-top: 30px;\">" +
                "<p>본 이메일은 발신 전용이므로 답장하지 마세요. 추가적인 도움이 필요할 경우 Steam 고객지원에 문의해 주세요.</p>" +
                "<p><a href=\"https://help.steampowered.com\" style=\"color: #61dafb; text-decoration: none;\">https://help.steampowered.com</a></p>" +
                "<div style=\"margin: 20px 0;\">" +
                "<img src=\"https://store.cloudflare.steamstatic.com/public/shared/images/footerLogo_valve_new.png\" alt=\"Valve Logo\" style=\"max-width: 100%; height: auto;\">" +
                "</div>" +
                "<p>© Valve Corporation<br>PO Box 1688 Bellevue, WA 98009</p>" +
                "<p>모든 권리 보유. 모든 상표는 미국 및 기타 국가에서 각각 해당하는 소유자의 재산입니다.</p>" +
                "</div>" +
                "</div>" +
                "</div>";
        
        // [1] Create a MimeMessage object that can be sent using the sender
        MimeMessage message = sender.createMimeMessage();
        
        // [2] Create a helper to set message details
        MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");
        
        // [3] Set email details using the helper
        helper.setTo(email);
        helper.setSubject("회원가입을 위한 초대");
//        helper.setText("회원가입 페이지로 이동하려면 아래 링크를 클릭하세요: <a href='" + signupUrl + "'>회원가입하기</a>", true);
        helper.setText(emailContent, true); // HTML 내용이므로 두 번째 매개변수에 true를 설정합니다.
        // [4] Send the email
        sender.send(message);

        // DB 기록 남기기
        certDao.delete(email);
        CertDto certDto = new CertDto();
        certDto.setCertEmail(email);
        certDto.setCertNumber(certNumber);
        certDao.insert(certDto);
    }
    
}
