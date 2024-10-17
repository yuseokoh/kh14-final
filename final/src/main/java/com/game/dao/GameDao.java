package com.game.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.game.dto.GameDto;

@Repository
public class GameDao {

	@Autowired
	private SqlSession sqlSession;
	
//	@Autowired
//	private GameMapper gameMapper;

	public List<GameDto> selectList() {
		return sqlSession.selectList("game.list");
	}

	//mybatis에서 사용하는 검색기능
	public List<GameDto> selectList(String column, String keyword) {
		Map<String, Object> params = new HashMap<>();
		params.put("column", column);
		params.put("keyword", keyword);
		return sqlSession.selectList("emp.search", params);
	}

	public void insert(GameDto gameDto) {
		sqlSession.insert("game.add", gameDto);		
	}

	public boolean update(GameDto gameDto) {
		int result = sqlSession.update("game.fix", gameDto);
		return result > 0;
	}

	public boolean delete(int gameNo) {
		return sqlSession.delete("game.del", gameNo) > 0;
	}	
	
}
