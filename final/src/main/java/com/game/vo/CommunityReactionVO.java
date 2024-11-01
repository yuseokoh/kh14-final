package com.game.vo;

import lombok.Data;

@Data
public class CommunityReactionVO {
    private boolean isLiked; //값이 ture라면 현재 사용자가 게시글에 좋아요
    private boolean isDisliked; // 값이 ture라면 현재 사용자가 게시글에 싫어요
    private int likeCount; //좋아요수
    private int dislikeCount; //싫어요 수
}