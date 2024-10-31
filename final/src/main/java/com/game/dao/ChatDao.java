package com.game.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.game.dto.ChatDto;

@Repository
public class ChatDao {
	
	@Autowired
	private SqlSession sqlSession;
	
	public int sequence() {
		return sqlSession.selectOne("chat.sequence");
	}
	public void insert(ChatDto chatDto) {
		sqlSession.insert("chat.add", chatDto);
	}
	public List<ChatDto> selectList(int beginRow, int endRow){
		Map<String, Object> params = Map.of("beginRow", beginRow, "endRow", endRow);
		return sqlSession.selectList("chat.list", params);
	}
}
