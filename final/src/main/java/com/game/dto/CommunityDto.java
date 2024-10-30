package com.game.dto;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import lombok.Data;

@Data
public class CommunityDto {
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
	
//	 private int beginRow; //페이징
//	 private int endRow; //페이징
//	
//	//댓글 관련 
//	private int communityGroup;
//	private Integer communityTarget;
//	private int communityDepth; //대댓글 이런형식 때 필요한거 
//	
//	 // 작성자가 탈퇴했을 경우 "탈퇴한 사용자"로 반환
//    public String getBoardWriterString() {
//        if(communityWriter == null) 
//            return "탈퇴한 사용자";
//        return communityWriter;
//    }
//
//    // 댓글 작성 시간 포맷
//    public String getCommunityWtimeString() {
//        Timestamp stamp = new Timestamp(communityWtime.getTime());
//        LocalDateTime time = stamp.toLocalDateTime();
//        LocalDate today = LocalDate.now();
//
//        if(time.toLocalDate().equals(today)) {
//            return time.format(DateTimeFormatter.ofPattern("HH:mm"));
//        } else {
//            return time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
//        }
//    }
//
//    // 댓글이 새 댓글인지 확인하는 메소드
//    public boolean isNew() {
//        return this.communityTarget == null;
//    }
//
//    // 댓글이 답글(대댓글)인지 확인하는 메소드
//    public boolean isReply() {
//        return this.communityTarget != null;
//    }
}


