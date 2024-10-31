package com.game.dao;

import java.util.HashMap;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.game.dto.GameImageDto;
import com.game.dto.MemberImageDto;

@Repository
public class MemberImageDao {

	@Autowired
	private SqlSession sqlSession;
	
	public void insert(MemberImageDto memberImageDto) {
		sqlSession.insert("memberImage.add",memberImageDto);
	}
	
	public MemberImageDto selectone(String memberId) {
		return sqlSession.selectOne("memberImage.image",memberId);
	}
	
	public void delete(int attachmentNo,String memberId) {
		Map<String, Object> params = new HashMap<>();
		params.put("attachmentNo", attachmentNo);
		params.put("memberId", memberId);
		sqlSession.delete("memberImage.del",params);
	}
}
