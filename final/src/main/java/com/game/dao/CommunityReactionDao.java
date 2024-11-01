package com.game.dao;

import java.util.HashMap;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class CommunityReactionDao {
	@Autowired
    private SqlSession sqlSession;

    private static final String NAMESPACE = "communityReaction.";

 // 특정 게시물에 대해 회원의 반응 여부 확인 (좋아요/싫어요 여부 확인)
    public boolean checkReaction(String memberId, int communityNo) {
        Map<String, Object> params = new HashMap<>();
        params.put("memberId", memberId);
        params.put("communityNo", communityNo);
        Integer count = sqlSession.selectOne(NAMESPACE + "hasReaction", params);
        return count != null && count > 0;
    }

    // 특정 게시물에 대한 회원의 반응 유형 확인 (좋아요/싫어요 타입 가져오기)
    public String getReactionType(String memberId, int communityNo) {
        Map<String, Object> params = new HashMap<>();
        params.put("memberId", memberId);
        params.put("communityNo", communityNo);
        return sqlSession.selectOne(NAMESPACE + "getReactionType", params);
    }

    // 좋아요 또는 싫어요 추가
    public void insertReaction(String memberId, int communityNo, String reactionType) {
        Map<String, Object> params = new HashMap<>();
        params.put("memberId", memberId);
        params.put("communityNo", communityNo);
        params.put("reactionType", reactionType);
        sqlSession.insert(NAMESPACE + "insertReaction", params);
    }

    // 기존 반응 삭제 (좋아요/싫어요 삭제)
    public void deleteReaction(String memberId, int communityNo) {
        Map<String, Object> params = new HashMap<>();
        params.put("memberId", memberId);
        params.put("communityNo", communityNo);
        sqlSession.delete(NAMESPACE + "deleteReaction", params);
    }

    // 특정 게시물에 대한 좋아요 개수 조회
    public int countLikes(int communityNo) {
        return sqlSession.selectOne(NAMESPACE + "countLikes", communityNo);
    }

    // 특정 게시물에 대한 싫어요 개수 조회
    public int countDislikes(int communityNo) {
        return sqlSession.selectOne(NAMESPACE + "countDislikes", communityNo);
    }
}