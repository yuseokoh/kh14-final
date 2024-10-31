// MemberReviewDto.java
package com.game.dto;

import java.sql.Date;
import lombok.Data;

@Data
public class MemberReviewDto {
    private int reviewNo;
    private String memberId;
    private int gameNo;
    private String reviewContent;
    private int reviewScore;
    private Date reviewDate;
    private int likes;
    private String reviewStatus;
    private String memberNickname;
}