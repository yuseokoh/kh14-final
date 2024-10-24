package com.game.dao;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.game.dto.CommunityDto;

@Repository
public class CommunityDao {

	@Autowired
	private SqlSession sqlSession;
	
	//게시글 목록
	public List<CommunityDto> CommunityList(){
		return sqlSession.selectList("community.list");
	}
	
	//게시글 등록
	public void CommunityInsert(CommunityDto communityDto) {
		sqlSession.insert("community.insert", communityDto);
	}
	
	//게시글 삭제
	public void CommunityDelete(int communityNo) {
		sqlSession.delete("community.delete", communityNo);
	}
	
	//게시글 수정
	public void CommunityUpdate(CommunityDto communityDto) {
		sqlSession.update("community.update", communityDto);
	}
	
	//제목 검색
	public List<CommunityDto> SearchByTitle(String keyword) {
	    return sqlSession.selectList("community.searchByTitle", keyword);
	}

	
	//게시글 조회수 증가
	public void CommunityViews(int communityNo) {
        sqlSession.update("community.views", communityNo);
    }
	
	//게시글 좋아요수 증가
	public void CommunityLikes(int communityNo) {
        sqlSession.update("community.likes", communityNo);
    }
	
	//특정 게시글 조회
	public CommunityDto CommunityIdSearch(int communityNo) {
        return sqlSession.selectOne("community.getPostById", communityNo);
    }
	
	// 특정 게시글 상세 조회
    public CommunityDto CommunityDetail(int communityNo) {
        return sqlSession.selectOne("community.detail", communityNo);
    }
    
// // 댓글 또는 답글 추가
//    public void insertComment(CommunityDto commentDto) {
//        sqlSession.insert("community.insertComment", commentDto);
//    }
//
//    // 댓글 수정
//    public void updateComment(CommunityDto commentDto) {
//        sqlSession.update("community.updateComment", commentDto);
//    }
//
//    // 댓글 삭제
//    public void deleteComment(int communityNo) {
//        sqlSession.delete("community.deleteComment", communityNo);
//    }
//
//    // 특정 게시글의 댓글 목록 조회
//    public List<CommunityDto> getCommentsByPostId(int communityNo) {
//        return sqlSession.selectList("community.getCommentsByPostId", communityNo);
//    }
}
    
    
