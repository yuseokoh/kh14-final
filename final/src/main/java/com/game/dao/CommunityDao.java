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
	
	//게시글 삭제
	public void CommunityDelete(int communityNo) {
		sqlSession.delete("community.delete", communityNo);
	}
	
	//게시글 수정
	public void CommunityUpdate(CommunityDto communityDto) {
		sqlSession.update("community.update", communityDto);
	}
	
//	//이미지 때문에 생긴 수정
//	public boolean update(CommunityDto communityDto) {
//		return sqlSession.update("community.update", communityDto) > 0;
//		
//	}
//	//이미지 때문에 생긴 수정
//	 public void insert(CommunityDto communityDto) {
//	        sqlSession.insert("community.insert", communityDto);
//	    }

	
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

	public int getCount(String memberId) {
		// TODO Auto-generated method stub
		return 0;
	}
    
    
//    //이미지쉐리떄문에
//	public void insert(CommunityDto communityDto) {
//        sqlSession.insert("community.insert", communityDto);
//    }
//
//    public boolean update(CommunityDto communityDto) {
//        int result = sqlSession.update("community.fix", communityDto);
//        return result > 0;
//    }
    
    

    
    
    //명호형이만든거
// 	public List<CommunityDto> selectListByPaging(CommunityComplexRequestVO vo) {
// 		return sqlSession.selectList("community.list", vo);
// 	}
//	public int countWithPaging(CommunityComplexRequestVO vo) {
//		return sqlSession.selectOne("community.count", vo);
//	}
	
	
}