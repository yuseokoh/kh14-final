package com.game.vo;

import java.sql.Date;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.game.advice.JsonEmptyIntegerToNullDeserializer;
import com.game.advice.JsonEmptyStringToNullDeserializer;

import lombok.Data;

@Data
public class CommunityComplexRequestVO {
	private String communityCategory; //카테고리(예: 정보, 자유, 공략, 질문...)
	private String communityTitle; //커뮤니티 제목
	private int communityNo; //커뮤니티 글 번호
	private String communityWriter;//작성자 =memberId pri
	private String communityContent; //커뮤니티 내용
	private Date communityWtime; //커뮤니티 글 작성일
	private Date communityUtime; //커뮤니티 글 수정일
	private int communityViews; //커뮤니티 글 조회수
	private int communityLikes; //커뮤니티 글 좋아요수
	private String communityState; //커뮤니티 상태 (공개,비공개)
	private int communityReplies; //커뮤니티 댓글수
	private Integer beginRow, endRow;
	
	
	
	//명호형
//	@JsonDeserialize(using = JsonEmptyStringToNullDeserializer.class)
//	private String column, keyword;	
//	@JsonDeserialize(using = JsonEmptyIntegerToNullDeserializer.class)
//	private Integer beginRow, endRow;
}
