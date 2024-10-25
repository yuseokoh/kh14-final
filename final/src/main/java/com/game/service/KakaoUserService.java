package com.game.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.game.dao.KakaoUserDao;
import com.game.dto.KakaoUserDto;

@Service
public class KakaoUserService {
	@Autowired
	private KakaoUserDao kakaoUserDao;
	
	
	  @Transactional
	    public KakaoUserDto saveOrUpdateKakaoUser(KakaoUserDto kakaoUser) {
	        Optional<KakaoUserDto> existingUser = kakaoUserDao.selectOneByKakaoId(kakaoUser.getKakaoId());

	        if (!existingUser.isPresent()) {
	            // 유저가 존재하지 않으면 새로운 유저로 저장
	            kakaoUserDao.insert(kakaoUser);
	            return kakaoUser;
	        } else {
	            // 유저가 이미 존재하면 기존 유저 반환
	            return existingUser.get();
	        }
	    }
//	@Transactional
//	public void saveOrUpdateKakaoUser(KakaoUserDto kakaoUserDto) {
//	    // kakao_id로 유저가 이미 존재하는지 확인
//	    Optional<KakaoUserDto> existingUser = kakaoUserDao.selectOneByKakaoId(kakaoUserDto.getKakaoId());
//
//	    if (!existingUser.isPresent()) {
//	        // 유저가 존재하지 않으면 새로운 유저로 저장
//	        kakaoUserDao.insert(kakaoUserDto);
//	    } else {
//	        // 유저가 이미 존재하면 로직을 처리하지 않고 반환
//	        System.out.println("유저가 이미 존재합니다. kakaoId: " + kakaoUserDto.getKakaoId());
//	    }
//	}


	// 카카오 유저 이메일 업데이트 로직
	    public KakaoUserDto updateKakaoUserEmail(String kakaoId, String email) {
	        Optional<KakaoUserDto> kakaoUserOpt = kakaoUserDao.selectOneByKakaoId(kakaoId);

	        if (kakaoUserOpt.isPresent()) {
	            KakaoUserDto kakaoUser = kakaoUserOpt.get();
	            kakaoUser.setMemberEmail(email);  // 이메일 업데이트
	            kakaoUserDao.updateKakaoUser(kakaoUser);  // DB에 업데이트
	            return kakaoUser;
	        } else {
	            throw new RuntimeException("카카오 유저를 찾을 수 없습니다.");
	        }
	    }
	    
	    
	}