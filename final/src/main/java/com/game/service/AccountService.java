package com.game.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.game.dao.KakaoUserDao;
import com.game.dao.MemberDao;
import com.game.dto.AccountInfoDto;
import com.game.dto.KakaoUserDto;
import com.game.dto.MemberDto;

@Service
public class AccountService {
    @Autowired
    private MemberDao memberDao;
    @Autowired
    private KakaoUserDao kakaoUserDao;

    public AccountInfoDto getAccountInfo(String memberId) {
        MemberDto member = memberDao.findById(memberId);
        KakaoUserDto kakaoUser = kakaoUserDao.findByLinkedMemberId(memberId);
        
        AccountInfoDto accountInfo = new AccountInfoDto();
        accountInfo.setMemberNickname(member.getMemberNickname());
        accountInfo.setMemberEmail(member.getMemberEmail());
        accountInfo.setMemberJoinDate(member.getMemberJoin());
        
        if (kakaoUser != null) {
            accountInfo.setKakaoNickname(kakaoUser.getMemberNickname());
            accountInfo.setKakaoJoinDate(kakaoUser.getMemberJoin());
        }
        
        return accountInfo;
    }
}
