package com.game.dao;

import java.util.List;

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
	
	//점수 랭킹
	public List<PlayDto> scoreRanking(){
		return sqlSession.selectList("play.scoreRanking");
	}
	//레벨 랭킹
	public List<PlayDto> levelRanking(){
		return sqlSession.selectList("play.levelRanking");
	}
	
	//아이디 검색
	public List<PlayDto> idSearch(String keyword){
		return sqlSession.selectList("play.idSearch", keyword);
	}
	
}
