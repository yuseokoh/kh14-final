package com.game.dao;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.game.dto.KakaoUserDto;

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

    public void updateLinkedMemberId(String kakaoId, String memberId) {
        Map<String, Object> params = new HashMap<>();
        params.put("kakaoId", kakaoId);
        params.put("memberId", memberId);
        sqlSession.update("kakaoUser.updateLinkedMemberId", params);
    }
	
	
}
