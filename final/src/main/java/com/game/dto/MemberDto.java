package com.game.dto;

import java.sql.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MemberDto {
    private String memberId;          // 아이디
    private String memberPw;          // 비밀번호
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private Date memberLogin;         // 로그인 일시
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private Date memberJoin;          // 가입일
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
	private Date memberLogout; //로그아웃시간 
    private String memberNickname;    // 닉네임
    private String memberEmail;       // 이메일
    private String memberLevel = "BASIC";   // 등급
    private Date memberBirth;         // 생년월일
    private String memberContact;     // 연락처
    private String memberPost;        // 우편번호
    private String memberAddress1;    // 주소1
    private String memberAddress2;    // 주소2
    private String verificationToken; // 이메일 인증 토큰
    private int emailVerified;        // 이메일 인증 여부 (0: 미인증, 1: 인증)
    private int memberPoint;          // 보유 포인트
//    @JsonProperty("id")
    private Integer  kakaoUserId;           // 카카오 회원번호
}