package com.game.dto;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class LibraryDto {

    private Long libraryId; 
    private String memberId;     
    private int gameNo;
    private int attachmentNo;        // attachmentNo로 이미지 파일 번호를 받음
    private String gameTitle;        // 게임 이름
    private String paymentDetailStatus; // 결제 상태 ("승인", "취소" 등)
}