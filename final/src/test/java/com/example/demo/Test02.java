package com.example.demo;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.game.dao.MemberDao;
import com.game.dto.MemberDto;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class Test02 {

    @TestConfiguration
    static class TestConfig {
        @Bean
        public PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        }
    }

    @Autowired
    private MemberDao memberDao;

    @Test
    @Order(1)
    public void testInsertMember() {
        MemberDto memberDto = new MemberDto();
        memberDto.setMemberId("testUser");
        memberDto.setMemberPw("password123");

        memberDao.insert(memberDto);
        System.out.println("회원가입 성공 여부: " + !memberDao.checkId("testUser"));
    }

    @Test
    @Order(2)
    public void testUpdatePassword() {
        String memberId = "testUser";
        String newPassword = "newPassword123";

        boolean result = memberDao.updatePassword(memberId, newPassword);
        System.out.println("비밀번호 변경 성공 여부: " + result);
    }

    @Test
    @Order(3)
    public void testUpdateLastLogin() {
        String memberId = "testUser";

        boolean result = memberDao.updateLastLogin(memberId);
        System.out.println("최종 로그인 시각 갱신 성공 여부: " + result);
    }

    @Test
    @Order(4)
    public void testDeleteMember() {
        String memberId = "testUser";

        boolean result = memberDao.deleteMember(memberId);
        System.out.println("회원 삭제 성공 여부: " + result);
    }
}