package com.game.dto;

import java.sql.Date;

import lombok.Data;

@Data
public class AccountInfoDto {
    private String memberNickname;
    private String memberEmail;
    private Date memberJoinDate;
    private String kakaoNickname;
    private Date kakaoJoinDate;
}
