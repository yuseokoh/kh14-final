package com.game.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.game.dto.GameImageDto;

@Repository
public class GameImageDao {
	
	@Autowired
	private SqlSession sqlSession;
	
	public void insert(GameImageDto gameImageDto) {
		sqlSession.insert("gameImage.add", gameImageDto);
	}
	
	public List<GameImageDto> selectList(int gameNo){
		return sqlSession.selectList("gameImage.listByGame", gameNo);
	}
	
	public void delete(int attachmentNo, int gameNo) {
		Map<String, Object> params = new HashMap<>();
		params.put("attachmentNo", attachmentNo);
		params.put("gameNo", gameNo);
		sqlSession.delete("gameImage.remove", params);
	}

}
