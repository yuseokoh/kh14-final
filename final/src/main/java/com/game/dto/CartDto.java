package com.game.dto;

import lombok.Data;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Data
public class CartDto {

    private int cartId;           
    private Date addedDate;   
    private String memberId;     
    private int gameNo;
    private String gameTitle; 
    private int gamePrice;
    private int attachmentNo; // attachmentNo로 이미지 파일 번호를 받음
}
