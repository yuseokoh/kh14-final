package com.game.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import com.game.dto.GameDto;

@Service
public class GameMapper implements RowMapper<GameDto> {
	@Override
	public GameDto mapRow(ResultSet rs, int rowNum) throws SQLException{
		GameDto gameDto = new GameDto();
		gameDto.setGameNo(rs.getInt("game_no"));
		gameDto.setGameTitle(rs.getString("game_title"));
		gameDto.setGamePrice(rs.getInt("game_price"));
		gameDto.setGameDeveloper(rs.getString("game_developer"));
		gameDto.setGamePublicationDate(rs.getDate("game_publication_date"));
		gameDto.setGameDiscount(rs.getInt("game_discount"));
		gameDto.setGameCategory(rs.getString("game_category"));
		gameDto.setGameGrade(rs.getString("game_grade"));
		gameDto.setGameTheme(rs.getString("game_theme"));
		gameDto.setGameDescription(rs.getString("game_description"));
		gameDto.setGameShortDescription(rs.getString("game_short_description"));
		gameDto.setGameUserScore(rs.getInt("game_user_score"));
		gameDto.setGameReviewCount(rs.getInt("game_review_count"));
		gameDto.setGamePlatforms(rs.getString("game_platforms"));
		gameDto.setGameSystemRequirement(rs.getString("game_system_requirement"));
		return gameDto;
}
//	private int gameDiscount;
//	private String gameCategory;
//	private String gameGrade;
//	private String gameTheme;
//	private String gameDescription;
//	private String gameShortDescription;
//	private int gameUserScore;
//	private int gameReviewCount;
//	private String gamePlatforms;
//	private String gameSystemRequirement;
	
}
