package com.game.dto;



import java.time.LocalDateTime;

import lombok.Data;

@Data
public class LibraryDto {

	 private Long libraryId; 
	    private String memberId;     
	    private int gameNo;
	    private LocalDateTime purchaseDate;
	    private int playtimeHours;       // 플레이 시간 (시간 단위)
	    private int installed;           // 설치 여부 (0: 미설치, 1: 설치됨)
	    private int attachmentNo; // attachmentNo로 이미지 파일 번호를 받음
	    private String gameTitle; // 게임 이름
}
