package com.game.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.game.dto.LibraryDto;

@Repository
public class LibraryDao {

	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	private SqlSession sqlSession;
	
	public void insert(String memberId, int gameNo) {
		Map<String, Object> params = new HashMap<>();
		sqlSession.insert("library.insert",params);
	}
	
	public boolean delete(int gameNo) {
		return sqlSession.delete("library.del",gameNo)>0;
	}


	public List<LibraryDto> selectListByMemberId(String memberId){
		return sqlSession.selectList("library.listbymemberid",memberId);
	}
}
