package com.game.dao;

import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.game.dto.ReplyDto;
import com.game.vo.ReplyComplexRequestVO;

@Repository
public class ReplyDao {
	
	@Autowired
	private SqlSession sqlSession;
	
//	//복합 목록 메소드
//		public List<ReplyDto> complexList(ReplyComplexRequestVO vo){
//			return sqlSession.selectList("reply.replyList", vo);
//		}
//		
//		//복합 목록 카운트 메소드
//		public int complexListCount(ReplyComplexRequestVO vo) {
//			return sqlSession.selectOne("reply.replyListCount", vo);
//		}
	
	
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
		replyDto.setReplyUtime(new Date(System.currentTimeMillis())); // 현재 시간으로 업데이트
		sqlSession.update("reply.update", replyDto);
	}
	
	//댓글 목록
	public List<ReplyDto> list(ReplyComplexRequestVO vo){
//		Map<String, Object> params = new HashMap<>();
//		params.put("replyOrigin", replyOrigin);
//		params.put("beginRow", null);
//		params.put("endRow", null);
		return sqlSession.selectList("reply.list", vo);
	}
	
	// 상세
	public ReplyDto selectOne(int replyNo) {
		return sqlSession.selectOne("reply.detail");
	}
	
	//카운트를 위한 Dao
	public int count(ReplyComplexRequestVO vo) {
        return sqlSession.selectOne("reply.count", vo);
    }

	
	//댓글수나오게 하는거 
	public int getCommunityNoByReplyNo(int replyNo) {
		return sqlSession.selectOne("reply.getCommunityNoByReplyNo", replyNo);
	}
	
	
	
	
	
	
	
}
