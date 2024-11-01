package com.game.restcontroller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.game.dao.CommunityReactionDao;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/community/reactions")
public class CommunityReactionRestController {

    @Autowired
    private CommunityReactionDao communityReactionDao;

 // 좋아요 또는 싫어요 토글
    @PostMapping("/toggle")
    public Map<String, Object> toggleReaction(
            HttpSession session,
            @RequestParam int communityNo,
            @RequestParam String reactionType) {

        String memberId = (String) session.getAttribute("loggedInUser");
        if (memberId == null) {
            memberId = "testuser123"; // 로그인 기능 비활성화 상태에서 테스트용 사용자 설정
        }

        boolean hasReaction = communityReactionDao.checkReaction(memberId, communityNo);
        String currentReactionType = hasReaction ? communityReactionDao.getReactionType(memberId, communityNo) : null;

        // 현재 반응 상태와 클릭한 버튼에 따라 처리
        if (currentReactionType != null && currentReactionType.equals(reactionType)) {
            communityReactionDao.deleteReaction(memberId, communityNo);
        } else {
            // 반대 반응을 초기화하고 현재 반응을 추가
            communityReactionDao.deleteReaction(memberId, communityNo);
            communityReactionDao.insertReaction(memberId, communityNo, reactionType);
        }

        // 최신 좋아요 및 싫어요 개수 반환
        Map<String, Object> result = new HashMap<>();
        result.put("likeCount", communityReactionDao.countLikes(communityNo));
        result.put("dislikeCount", communityReactionDao.countDislikes(communityNo));
        result.put("isLiked", reactionType.equals("L"));
        result.put("isDisliked", reactionType.equals("U"));

        return result;
    }

    // 특정 게시물의 좋아요 및 싫어요 개수 조회
    @GetMapping("/count")
    public Map<String, Integer> getReactionCounts(@RequestParam int communityNo) {
        Map<String, Integer> result = new HashMap<>();
        result.put("likeCount", communityReactionDao.countLikes(communityNo));
        result.put("dislikeCount", communityReactionDao.countDislikes(communityNo));
        return result;
    }
}
