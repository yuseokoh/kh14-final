package com.game.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.game.dto.CommunityImageDto;

@Repository
public class CommunityImageDao {
	
	@Autowired
	private SqlSession sqlSession;
	
	public void insert(CommunityImageDto communityImageDto) {
		sqlSession.insert("communityImage.add", communityImageDto);
	}
	
	public List<CommunityImageDto> selectList(int communityNo){
		return sqlSession.selectList("communityImage.listByCommunity", communityNo);
	}
	
	public void delete(int attachmentNo, int communityNo) {
		Map<String, Object> params = new HashMap<>();
		params.put("attachmentNo", attachmentNo);
		params.put("communityNo", communityNo);
		sqlSession.delete("communityImage.remove", params);
	}
	
}
