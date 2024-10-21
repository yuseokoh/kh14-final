package com.game.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.game.dao.CommunityDao;
import com.game.dto.CommunityDto;

@Service
public class CommunityService {

    @Autowired
    private CommunityDao communityDao;

    // 게시글 목록 조회
    public List<CommunityDto> getCommunityList() {
        return communityDao.CommunityList();
    }

    // 게시글 등록
    public void insertCommunity(CommunityDto communityDto) {
        communityDao.CommunityInsert(communityDto);
    }

    // 게시글 수정
    public void updateCommunity(CommunityDto communityDto) {
        communityDao.CommunityUpdate(communityDto);
    }

    // 게시글 삭제
    public void deleteCommunity(int communityNo) {
        communityDao.CommunityDelete(communityNo);
    }

    // 게시글 검색
    public List<CommunityDto> searchCommunityList(String column, String keyword) {
        return communityDao.CommunitySearch(column, keyword);
    }
    
    // 게시글 조회수 증가
    public void CommunityViews(int communityNo) {
        communityDao.CommunityViews(communityNo);
    }
    
    // 좋아요 수 증가
    public void CommunityLikes(int communityNo) {
        communityDao.CommunityLikes(communityNo);
    }
    
}
