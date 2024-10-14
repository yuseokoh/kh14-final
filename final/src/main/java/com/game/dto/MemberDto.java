package com.game.dto;

import java.sql.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class MemberDto {
	private String memberId;//아이디
	private String memberPw;//비밀번호
	private String memberEmail;//이메일
//	private String memberBirth;  //생일 추가해야함 (다른 패키지에도)
	private String memberLevel;//등급
	private int memberPoint;//보유포인트
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
	private Date memberJoin;//가입일
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
	private Date memberLogin;//최종로그인 일시
}