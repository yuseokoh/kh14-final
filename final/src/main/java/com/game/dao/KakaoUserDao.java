package com.game.dao;

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


    public void updateKakaoUser(KakaoUserDto kakaoUserDto) {
        sqlSession.update("kakaoUser.updateKakaoUser", kakaoUserDto);
    }

	
	
	
}
