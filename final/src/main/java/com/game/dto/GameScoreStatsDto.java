// GameScoreStatsDto.java
package com.game.dto;

import lombok.Data;

@Data
public class GameScoreStatsDto {
    private int gameNo;
    private int totalScore;
    private int reviewCount;
    private double averageScore;
}