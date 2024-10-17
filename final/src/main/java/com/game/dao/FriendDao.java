package com.game.dao;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.game.dto.FriendDto;

@Repository
public class FriendDao {
	
	@Autowired
	private SqlSession sqlSession;
	
	public int sequence() {
		return sqlSession.insert("friend.friendSequence");
	}
	
	public void request(FriendDto friendDto) {
		sqlSession.insert("friend.request", friendDto);
	}
	
}
