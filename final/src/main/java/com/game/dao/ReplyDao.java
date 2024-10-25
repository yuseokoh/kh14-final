package com.game.dao;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.game.dto.ReplyDto;

@Repository
public class ReplyDao {
	
	@Autowired
	private SqlSession sqlSession;
	
	//시퀀스
	public int sequence() {
		return sqlSession.selectOne("reply.sequence");
	}
	
	//댓글 등록
	public void insert(ReplyDto replyDto) {
		sqlSession.insert("reply.insert", replyDto);
	}
	
	//댓글 삭제
	public void delete(int replyNo) {
		sqlSession.delete("reply.delete", replyNo);
	}
	
	//댓글 수정
	public void update(ReplyDto replyDto) {
		sqlSession.update("reply.update", replyDto);
	}
	
	//댓글 목록
	public List<ReplyDto> list(){
		return sqlSession.selectList("reply.list");
	}
	
	
	
	
	
	
	
}
