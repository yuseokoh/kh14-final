package com.game.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.game.dto.GameDto;

@Repository
public class GameDao {

    @Autowired
    private SqlSession sqlSession;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<GameDto> selectList() {
        return sqlSession.selectList("game.list");
    }

    //mybatis에서 사용하는 검색기능
    public List<GameDto> selectList(String column, String keyword) {
        Map<String, Object> params = new HashMap<>();
        params.put("column", column);
        params.put("keyword", keyword);
        return sqlSession.selectList("game.search", params);
    }

    public void insert(GameDto gameDto) {
        sqlSession.insert("game.insert", gameDto);
    }

    public boolean update(GameDto gameDto) {
        int result = sqlSession.update("game.fix", gameDto);
        return result > 0;
    }

    public boolean delete(int gameNo) {
        return sqlSession.delete("game.del", gameNo) > 0;
    }

    public GameDto selectOne(int gameNo) {
        return sqlSession.selectOne("game.detail", gameNo);
    }
    
    public Integer findGameNoByMemberId(String memberId) {
        return sqlSession.selectOne("cart.findGameNoByTitle", memberId);
    }
    
    // 게임 평점 업데이트
    public void updateScore(int gameNo) {
    sqlSession.update("game.updateScore", gameNo);
    }

    // 게임 리뷰 카운트 업데이트
    public void updateReviewCount(int gameNo) {
    sqlSession.update("game.updateReviewCount", gameNo);
    }
    
    // 게임 
    public void updateGameScore(int gameNo) {
        sqlSession.update("game.updateScore", gameNo);
    }

    public void updateGameReviewCount(int gameNo) {
        sqlSession.update("game.updateReviewCount", gameNo);
    }
}