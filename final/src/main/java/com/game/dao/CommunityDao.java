package com.game.dao;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.game.dto.CommunityDto;
import com.game.vo.CommunityComplexRequestVO;

@Repository
public class CommunityDao {

	@Autowired
	private SqlSession sqlSession;
	
	//복합 검색 메소드
	public List<CommunityDto> complexSearch(CommunityComplexRequestVO vo){
		return sqlSession.selectList("community.complexSearch", vo);
	}
	
	//복합 검색 카운트 메소드
	public int complexSearchCount(CommunityComplexRequestVO vo) {
		return sqlSession.selectOne("community.complexSearchCount", vo);
	}
	
	
	
	
	
	
	
	
	
	
	
	
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
    

    
    
    //명호형이만든거
// 	public List<CommunityDto> selectListByPaging(CommunityComplexRequestVO vo) {
// 		return sqlSession.selectList("community.list", vo);
// 	}
//	public int countWithPaging(CommunityComplexRequestVO vo) {
//		return sqlSession.selectOne("community.count", vo);
//	}
	
	
}
    
    
