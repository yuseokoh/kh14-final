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

//	// 커뮤니티 이미지 링크 추가
//    public void insertCommunityImage(CommunityImageDto communityImageDto) {
//        sqlSession.insert("communityImage.insert", communityImageDto);
//    }
//
//    // 커뮤니티 게시글에 연결된 모든 이미지 삭제
//    public void deleteCommunityImages(int communityNo) {
//        sqlSession.delete("communityImage.deleteByCommunityNo", communityNo);
//    }
//
//    // 커뮤니티 게시글에 연결된 모든 이미지 조회
//    public List<CommunityImageDto> selectByCommunityNo(int communityNo) {
//        return sqlSession.selectList("communityImage.selectByCommunityNo", communityNo);
//    }
//
//    // 특정 이미지 개별 삭제 메서드 추가
//    public void delete(int attachmentNo, int communityNo) {
//        sqlSession.delete("communityImage.delete", Map.of("attachmentNo", attachmentNo, "communityNo", communityNo));
//    }
	
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
	
	public boolean exists(int attachmentNo) {
	    Integer count = sqlSession.selectOne("communityImage.exists", attachmentNo);
	    return count != null && count > 0;
	}


}
