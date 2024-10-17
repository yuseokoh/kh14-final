package com.game.dto;

import java.sql.Date;

import lombok.Data;

@Data
public class CommunityDto {
	private String communtiyCategory; //카테고리(예: 정보, 자유, 공략, 질문...)
	private String communtiyTitel; //커뮤니티 제목
	private int communtiyNo; //커뮤니티 글 번호
	private String communtiyContent; //커뮤니티 내용
	private Date communtiyWrite; //커뮤니티 글 작성일
	private Date communtiyModify; //커뮤니티 글 수정일
	private int communtiyViews; //커뮤니티 글 조회수
	private int communtiyLikes; //커뮤니티 글 좋아요수
	private String communtiyState; //커뮤니티 상태 (공개,비공개)
	private int communtiyReplies; //커뮤니티 댓글수

}
