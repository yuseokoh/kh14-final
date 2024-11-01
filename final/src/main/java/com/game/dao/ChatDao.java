package com.game.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.game.dto.ChatDto;
import com.game.vo.WebsocketMessageVO;

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
	public List<ChatDto> selectListMember(String memberId, int beginRow, int endRow){
		Map<String, Object> params = new HashMap<>();
		params.put("memberId", memberId);
		params.put("beginRow", beginRow);
		params.put("endRow", endRow);
		return sqlSession.selectList("chat.listMember", params);
	}
	public List<WebsocketMessageVO> selectListMemberComplete(String memberId, int beginRow, int endRow){
		Map<String, Object> params = new HashMap<>();
		params.put("memberId", memberId);
		params.put("beginRow", beginRow);
		params.put("endRow", endRow);
		return sqlSession.selectList("chat.listMemberComplete", params);
	}
	public List<WebsocketMessageVO> selectListMemberComplete(String memberId, int beginRow, int endRow, int firstMessageNo){
		Map<String, Object> params = new HashMap<>();
		params.put("memberId", memberId);
		params.put("beginRow", beginRow);
		params.put("endRow", endRow);
		params.put("firstMessageNo", firstMessageNo);
		return sqlSession.selectList("chat.listMemberComplete", params);
	}
}
