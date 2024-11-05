package com.game.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.game.dto.GameScoreStatsDto;

@Repository
public class GameScoreStatsDao {
    @Autowired
    private SqlSession sqlSession;

    // 게임의 평점 통계 조회
    public GameScoreStatsDto getGameStats(int gameNo) {
        return sqlSession.selectOne("score.getGameStats", gameNo);
    }

    // 평점 통계 갱신
    @Transactional
    public void refreshGameStats(int gameNo) {
        sqlSession.update("score.refreshGameStats", gameNo);
        sqlSession.update("score.updateGameScore", gameNo);  // game 테이블도 업데이트
    }

    // 평점 통계 삭제
    public void deleteGameStats(int gameNo) {
        sqlSession.delete("score.deleteGameStats", gameNo);
    }

    // 특정 평점 범위의 게임 검색
    public List<Integer> findGamesByScoreRange(double minScore, double maxScore, int minReviews) {
        Map<String, Object> params = new HashMap<>();
        params.put("minScore", minScore);
        params.put("maxScore", maxScore);
        params.put("minReviews", minReviews);
        return sqlSession.selectList("score.findGamesByScoreRange", params);
    }

    // 평점 높은 게임 목록 조회
    public List<GameScoreStatsDto> listTopRatedGames(int minReviews, int limit) {
        Map<String, Object> params = new HashMap<>();
        params.put("minReviews", minReviews);
        params.put("limit", limit);
        return sqlSession.selectList("score.listTopRatedGames", params);
    }
    
    //평균점 계산 메소드
    public double calculateAverageScore(int gameNo) {
    	Double avgScore = sqlSession.selectOne("game.calculateAverageScore", gameNo);
    	return avgScore != null ? avgScore : 0.0;
    }
}