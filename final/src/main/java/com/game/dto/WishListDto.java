package com.game.dto;
import java.sql.Date;
import lombok.Data;
@Data
public class WishListDto {
	private int wishListId;
	private int memberId;
	private int gameNo;
	private Date addedDate;
}