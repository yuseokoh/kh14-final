package com.game.dao;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.game.dto.MemberTokenDto;

@Repository
public class MemberTokenDao {
	
	@Autowired
	private SqlSession sqlSession;
	public void insert(MemberTokenDto memberTokenDto) {
		sqlSession.insert("memberToken.insert", memberTokenDto);
	}
	public MemberTokenDto selectone(MemberTokenDto memberTokenDto) {
		return sqlSession.selectOne("memberToken.check", memberTokenDto);
	}
	public boolean delete(MemberTokenDto memberTokenDto) {
		return sqlSession.delete("memberToken.delete", memberTokenDto) > 0;
	}
	public int clear() {
		return sqlSession.delete("memberToken.clear");
	}
	
	
	
}
