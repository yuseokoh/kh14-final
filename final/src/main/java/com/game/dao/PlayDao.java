package com.game.dao;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.game.dto.PlayDto;

@Repository
public class PlayDao {

	@Autowired
	private SqlSession sqlSession;
	
	
	public int sequence() {
		return sqlSession.selectOne("play.playSequence");
	}
	//랭킹 등록
	public void insert(PlayDto playDto) {
		sqlSession.insert("play.insert", playDto);
	}
}
