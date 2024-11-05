package com.game.dao;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.game.dto.KakaoUserDto;
import com.game.vo.MemberClaimVO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class KakaoUserDao {

	@Autowired
	private SqlSession sqlSession;

	public Optional<KakaoUserDto> selectOneByKakaoId(String kakaoId) {
		return Optional.ofNullable(sqlSession.selectOne("kakaoUser.selectOneByKakaoId", kakaoId));
	}

	public void insert(KakaoUserDto kakaoUserDto) {
		sqlSession.insert("kakaoUser.add", kakaoUserDto);
	}
//    public void insert(KakaoUserDto kakaoUserDto) {
//        sqlSession.insert("kakaoUser.add", kakaoUserDto);
//        Integer kakaoUserId = getKakaoUserIdByKakaoId(kakaoUserDto.getKakaoId());
//        if (kakaoUserId != null) {
//            kakaoUserDto.setKakaoUserId(kakaoUserId);
//        }
//    }

	public void updateKakaoUser(KakaoUserDto kakaoUserDto) {
		sqlSession.update("kakaoUser.updateKakaoUser", kakaoUserDto);
	}

	public Integer getKakaoUserIdByKakaoId(String kakaoId) {
		return sqlSession.selectOne("kakaoUser.selectKakaoUserIdByKakaoId", kakaoId);
	}

	public KakaoUserDto findByEmail(String email) {
		return sqlSession.selectOne("kakao_user.findByEmail", email);
	}

	public KakaoUserDto findByLinkedMemberId(String memberId) {
		return sqlSession.selectOne("kakao_user.findByLinkedMemberId", memberId);
	}

	// 카카오 유저의 linked_member_id 업데이트
	public void updateLinkedMemberId(String kakaoId, String memberId) {
		Map<String, Object> params = new HashMap<>();
		params.put("kakaoId", kakaoId);
		params.put("memberId", memberId);
		sqlSession.update("kakaoUser.updateLinkedMemberId", params);
	}

	public void updateEmail(String kakaoId, String email) {
		Map<String, Object> params = new HashMap<>();
		params.put("kakaoId", kakaoId);
		params.put("memberEmail", email);

		sqlSession.update("kakaoUser.updateEmail", params);
	}

	public void updateLinkedMemberId(Map<String, Object> params) {
	    try {
	        int updatedRows = sqlSession.update("kakaoUser.updateLinkedMemberId", params);
	        if (updatedRows > 0) {
	            log.info("linked_member_id 업데이트 성공: kakaoId = {}, memberId = {}", params.get("kakaoId"), params.get("memberId"));
	        } else {
	            log.warn("linked_member_id 업데이트 실패: 해당 kakaoId를 찾을 수 없음 = {}", params.get("kakaoId"));
	        }
	    } catch (Exception e) {
	        log.error("linked_member_id 업데이트 중 오류 발생: kakaoId = {}", params.get("kakaoId"), e);
	        throw new RuntimeException("linked_member_id 업데이트에 실패했습니다.");
	    }
	}

	public void updateKakaoUserEmail(String kakaoId, String memberEmail) {
	    // 파라미터를 담을 Map 생성
	    Map<String, Object> params = new HashMap<>();
	    params.put("kakaoId", kakaoId);
	    params.put("memberEmail", memberEmail);

	    // MyBatis 매퍼 호출하여 이메일 업데이트
	    sqlSession.update("kakaoUser.updateKakaoUserEmail", params);
	}
	
	 // MemberClaimVO를 반환하는 메서드
    public MemberClaimVO selectOneByKakaoId2(String kakaoId) {
        return sqlSession.selectOne("kakaoUser.selectOneByKakaoId2", kakaoId);
    }



}
