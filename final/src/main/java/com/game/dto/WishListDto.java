package com.game.dto;

import java.sql.Date;

import lombok.Data;

@Data
public class WishListDto {

	private int wishList;
	private int memberId;
	private int gameId;
	private Date addedDate;
}
