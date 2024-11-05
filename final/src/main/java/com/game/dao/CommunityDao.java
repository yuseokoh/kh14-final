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
	
	//댓글수
	public void updateReplyCount(int communityNo) {
	    sqlSession.update("community.updateReplyCount", communityNo);
	}
	
	//게시글 목록
	public List<CommunityDto> CommunityList(){
		return sqlSession.selectList("community.list");
	}
	
	//게시글검색
	public List<CommunityDto> CommunitySearch(CommunityComplexRequestVO vo){
		return sqlSession.selectList("community.search", vo);
	}
	
	//게시글 카운트
	public int CommunityCount(CommunityComplexRequestVO vo){
		return sqlSession.selectOne("community.count", vo);
	}
	
	//게시글 등록
	public void CommunityInsert(CommunityDto communityDto) {
		sqlSession.insert("community.insert", communityDto);
	}
	
	//gpt 이미지성공삽입
//	public void CommunityInsert(CommunityDto communityDto) {
//	    // 게시글을 먼저 삽입합니다.
//	    sqlSession.insert("community.insert", communityDto);
//
//	    // 삽입 후 시퀀스를 조회하여 커뮤니티 번호를 설정합니다.
//	    int communityNo = sqlSession.selectOne("community.selectCurrentSequence");
//	    communityDto.setCommunityNo(communityNo);
//	}
	
	//-------------------------------game 이미지보고다시만든거
	 public boolean update(CommunityDto communityDto) {
	        int result = sqlSession.update("community.update", communityDto);
	        return result > 0;
	    }
	 public boolean delete(int communityNo) {
	        return sqlSession.delete("community.delete", communityNo) > 0;
	    }
	 
	 public CommunityDto selectOne(int communityNo) {
	        return sqlSession.selectOne("community.detail", communityNo);
	    }
	 public int getLastInsertId() {
		    return sqlSession.selectOne("community.selectCurrentSequence");
		}
	


	
	//게시글 삭제 ----------이미지 첨부실패시 다시 원상복귀 기존에 쓰는거
//	public void CommunityDelete(int communityNo) {
//		sqlSession.delete("community.delete", communityNo);
//	}
	
	//게시글 수정 ----------이미지 첨부실패시 다시 원상복귀 기존에 쓰는거
//	public void CommunityUpdate(CommunityDto communityDto) {
//		sqlSession.update("community.update", communityDto);
//	}
	
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
    //작성한 게시글 수 
    public int getCount(String communityWriter) {
        Integer count = sqlSession.selectOne("community.countNo", communityWriter);
        return count != null ? count : 0;  // null인 경우 0을 반환
    }
	
}