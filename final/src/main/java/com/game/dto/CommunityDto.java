package com.game.dto;

import java.sql.Date;

import lombok.Data;

@Data
public class CommunityDto {
	private String communityCategory; //카테고리(예: 정보, 자유, 공략, 질문...)
	private String communityTitle; //커뮤니티 제목
	private int communityNo; //커뮤니티 글 번호
	private String communityContent; //커뮤니티 내용
	private Date communityWrite; //커뮤니티 글 작성일
	private Date communityModify; //커뮤니티 글 수정일
	private int communityViews; //커뮤니티 글 조회수
	private int communityLikes; //커뮤니티 글 좋아요수
	private String communityState; //커뮤니티 상태 (공개,비공개)
	private int communityReplies; //커뮤니티 댓글수

}
