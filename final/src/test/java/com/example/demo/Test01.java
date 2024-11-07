package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import com.game.configuration.EmailConfiguration;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@SpringBootTest(classes = {EmailConfiguration.class})
public class Test01 {

    @Autowired
    private JavaMailSender sender;
    
    @Test
    public void testSendSignupInvitation() throws MessagingException {
        String recipientEmail = "dhdbtjraksa@naver.com";
        String signupUrl = "http://192.168.30.58:3000/#/member/SignupForm";
        
        // [1] Create a MimeMessage object that can be sent using the sender
        MimeMessage message = sender.createMimeMessage();
        
        // [2] Create a helper to set message details
        MimeMessageHelper helper = 
                            new MimeMessageHelper(message, false, "UTF-8");
        
        // [3] Set email details using the helper
        helper.setTo(recipientEmail);
        helper.setSubject("회원가입을 위한 초대");
        helper.setText("회원가입 페이지로 이동하려면 아래 링크를 클릭하세요: <a href='" + signupUrl + "'>회원가입하기</a>", true);        
        // [4] Send the email
        sender.send(message);
    }
}
