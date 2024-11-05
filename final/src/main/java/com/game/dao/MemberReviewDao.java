// MemberReviewDao.java
package com.game.dao;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.game.dto.MemberReviewDto;

/**
 * 게임 리뷰 관련 데이터 접근 객체
 */
@Repository
public class MemberReviewDao {
    @Autowired
    private SqlSession sqlSession;

    /**
     * 새로운 리뷰 등록
     * @param reviewDto 등록할 리뷰 정보
     */
    public void insert(MemberReviewDto reviewDto) {
        sqlSession.insert("review.insert", reviewDto);
    }

    /**
     * 기존 리뷰 수정
     * @param reviewDto 수정할 리뷰 정보
     * @return 수정 성공 여부
     */
    public boolean update(MemberReviewDto reviewDto) {
        return sqlSession.update("review.update", reviewDto) > 0;
    }

    /**
     * 리뷰 상태 변경 (삭제/신고 처리)
     * @param reviewNo 리뷰 번호
     * @param status 변경할 상태
     * @return 변경 성공 여부
     */
    public boolean updateStatus(int reviewNo, String status) {
        Map<String, Object> params = new HashMap<>();
        params.put("reviewNo", reviewNo);
        params.put("reviewStatus", status);
        return sqlSession.update("review.updateStatus", params) > 0;
    }

    /**
     * 리뷰 좋아요 수 증가
     * @param reviewNo 리뷰 번호
     * @return 증가 성공 여부
     */
    public boolean increaseLikes(int reviewNo) {
        return sqlSession.update("review.increaseLikes", reviewNo) > 0;
    }

    /**
     * 단일 리뷰 상세 조회
     * @param reviewNo 리뷰 번호
     * @return 리뷰 상세 정보
     */
    public MemberReviewDto detail(int reviewNo) {
        return sqlSession.selectOne("review.detail", reviewNo);
    }

    /**
     * 게임별 리뷰 목록 조회 (페이징)
     * @param gameNo 게임 번호
     * @param start 시작 행 번호
     * @param end 종료 행 번호
     * @return 리뷰 목록
     */
    public List<MemberReviewDto> listByGame(int gameNo, int start, int size) {
        Map<String, Object> params = new HashMap<>();
        params.put("gameNo", gameNo);
        params.put("startRow", start);
        params.put("endRow", start + size);
        return sqlSession.selectList("review.listByGame", params);
    }
    
    /**
     * 회원별 리뷰 목록 조회
     * @param memberId 회원 ID
     * @return 회원의 리뷰 목록
     */
    public List<MemberReviewDto> listByMember(String memberId) {
        return sqlSession.selectList("review.listByMember", memberId);
    }

    /**
     * 게임의 활성 상태 리뷰 수 조회
     * @param gameNo 게임 번호
     * @return 리뷰 수
     */
    public int countByGame(int gameNo) {
        return sqlSession.selectOne("review.countByGame", gameNo);
    }

    /**
     * 회원의 특정 게임 리뷰 존재 여부 확인
     * @param memberId 회원 ID
     * @param gameNo 게임 번호
     * @return 리뷰 존재 여부
     */
    public boolean existsByMemberAndGame(String memberId, int gameNo) {
        Map<String, Object> params = new HashMap<>();
        params.put("memberId", memberId);
        params.put("gameNo", gameNo);
        Integer count = sqlSession.selectOne("review.existsByMemberAndGame", params);
        return count != null && count > 0;
    }

    /**
     * 인기 리뷰 목록 조회
     * @param gameNo 게임 번호
     * @param days 최근 일수
     * @return 인기 리뷰 목록
     */
    public List<MemberReviewDto> listPopularReviews(int gameNo, int days) {
        Map<String, Object> params = new HashMap<>();
        params.put("gameNo", gameNo);
        params.put("days", days);
        return sqlSession.selectList("review.listPopularReviews", params);
    }
    
    // 리뷰 삭제
    public void physicalDelete(int reviewNo) {
        sqlSession.delete("review.physicalDelete", reviewNo);
    }
}