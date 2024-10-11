package com.game.dao;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.game.dto.MemberDto;
import com.game.vo.MemberComplexRequestVO;

@Repository
public class MemberDao {
 
	@Autowired
	private SqlSession sqlSession;
	
	public List<MemberDto> complexSearch(MemberComplexRequestVO vo){
		return sqlSession.selectList("member.complexSearch",vo);
	}
	public int complexSearchCount(MemberComplexRequestVO vo) {
		return sqlSession.selectOne("member.complexSearchCount", vo);
	}
	public MemberDto selectOne(String memberId) {
		return sqlSession.selectOne("member.find", memberId);
	}
	

}
