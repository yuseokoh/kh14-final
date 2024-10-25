package com.game.dto;
import java.sql.Date;
import lombok.Data;
@Data
public class WishListDto {
	private int wishListId;
	private String memberId;
	private int gameNo;
	private Date addedDate;
	private String gameTitle; // 게임 이름
    private double gamePrice; // 게임 가격
    private String  releaseDate;
    private int attachmentNo; // attachmentNo로 이미지 파일 번호를 받음
}