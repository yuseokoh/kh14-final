package com.game.service;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.game.configuration.KakaoLoginProperties;
import com.game.dao.KakaoUserDao;
import com.game.dao.MemberDao;
import com.game.dto.KakaoUserDto;
import com.game.dto.MemberDto;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class KakaoUserService {
	@Autowired
	private KakaoUserDao kakaoUserDao;
	@Autowired
	private MemberDao memberDao;
	@Autowired
	private SqlSession sqlSession;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private HttpHeaders headers;
    @Autowired
    private KakaoLoginProperties kakaoLoginProperties;
	

	   // 카카오 로그인 시 사용자 정보 가져오기 (API 호출)
    public KakaoUserDto getUserInfo(String accessToken) throws URISyntaxException {
        URI uri = new URI(kakaoLoginProperties.getUserInfoUrl());

        headers.set("Authorization", "Bearer " + accessToken);
        HttpEntity<Map<String, String>> entity = new HttpEntity<>(null, headers);

        Map<String, Object> response = restTemplate.postForObject(uri, entity, Map.class);
        if (response == null) {
            throw new IllegalStateException("카카오 API 응답이 null입니다.");
        }

        // 카카오 API에서 사용자 정보 가져오기
        String kakaoId = String.valueOf(response.get("id"));
        Map<String, String> properties = (Map<String, String>) response.get("properties");
        String nickname = properties != null ? properties.get("nickname") : "카카오 사용자";
        Map<String, String> kakaoAccount = (Map<String, String>) response.get("kakao_account");
        String email = (kakaoAccount != null) ? kakaoAccount.get("email") : "no-email@example.com";

        // KakaoUserDto 객체 생성 및 값 설정
        KakaoUserDto kakaoUser = new KakaoUserDto();
        kakaoUser.setKakaoId(kakaoId);
        kakaoUser.setMemberNickname(nickname);
        kakaoUser.setMemberEmail(email);
        kakaoUser.setMemberJoin(new java.sql.Date(System.currentTimeMillis()));

        return kakaoUser;  // 정보 반환
    }

	
    @Transactional
    public KakaoUserDto saveOrUpdateKakaoUser(KakaoUserDto kakaoUser) {
        Optional<KakaoUserDto> existingUser = kakaoUserDao.selectOneByKakaoId(kakaoUser.getKakaoId());

        if (!existingUser.isPresent()) {
            // 유저가 존재하지 않으면 새로운 유저로 저장
            kakaoUserDao.insert(kakaoUser);

            // 저장된 kakao_user_id를 가져와서 KakaoUserDto에 설정
            Integer kakaoUserId = kakaoUserDao.getKakaoUserIdByKakaoId(kakaoUser.getKakaoId());
            kakaoUser.setKakaoUserId(kakaoUserId); // kakao_user_id 설정
        } else {
            // 유저가 이미 존재하면 업데이트
            KakaoUserDto existing = existingUser.get();

            // 이미 이메일이 등록된 경우, 임시 이메일로 덮어쓰지 않음
            if (existing.getMemberEmail() != null && !existing.getMemberEmail().equals("no-email@example.com")) {
                kakaoUser.setMemberEmail(existing.getMemberEmail());
            }

            // 닉네임과 가입 날짜 업데이트
            existing.setMemberNickname(kakaoUser.getMemberNickname());
            existing.setMemberJoin(kakaoUser.getMemberJoin());
            kakaoUserDao.updateKakaoUser(existing);
            return existing;
        }

        return kakaoUser;
    }



	// 카카오 유저 이메일 업데이트 로직
	public void insertWithKakao(KakaoUserDto kakaoUser) {
	    Integer kakaoUserId = kakaoUser.getKakaoUserId(); // KakaoUserDto에서 kakao_user_id 가져오기

	    if (kakaoUserId == null) {
	        throw new RuntimeException("Kakao User ID가 없습니다.");
	    }

	    // member 테이블에 kakao_user_id를 포함한 데이터를 삽입
	    MemberDto memberDto = new MemberDto();
	    memberDto.setKakaoUserId(kakaoUserId);  // 외래키로 연동된 kakao_user_id 설정
	    memberDto.setMemberId("kakao_" + kakaoUser.getKakaoId()); // 멤버 ID는 kakao_카카오ID 형식
	    memberDto.setMemberEmail(kakaoUser.getMemberEmail());
	    memberDto.setMemberNickname(kakaoUser.getMemberNickname()); // 이미 설정된 닉네임 사용
	    memberDto.setMemberLevel("BASIC");

	    try {
	        sqlSession.insert("member.addWithKakao", memberDto);
	        log.info("member 테이블에 데이터 삽입 성공");
	    } catch (Exception e) {
	        log.error("member 테이블에 데이터 삽입 실패", e);
	    }
	}

	@Transactional
	public void insertKakaoUserAndMember(KakaoUserDto kakaoUser) {
	    KakaoUserDto savedKakaoUser = saveOrUpdateKakaoUser(kakaoUser);  // 재사용

	    Integer kakaoUserId = savedKakaoUser.getKakaoUserId();
	    if (kakaoUserId != null) {
	        memberDao.insertWithKakao(savedKakaoUser);  // 멤버 추가
	        System.out.println("Member inserted with Kakao ID: " + kakaoUserId);
	    } else {
	        throw new RuntimeException("Kakao User ID 생성 실패");
	    }
	}




//	@Transactional
//	public void insertKakaoUserAndMember(KakaoUserDto kakaoUser) {
//		// 기존 유저가 있는지 확인
//		Optional<KakaoUserDto> existingUser = kakaoUserDao.selectOneByKakaoId(kakaoUser.getKakaoId());
//
//
//		   if (existingUser.isPresent()) {
//		        log.info("Kakao user already exists: {}", kakaoUser.getKakaoId());
//		        // 기존 유저가 있으면 업데이트
//		        kakaoUserDao.updateKakaoUser(kakaoUser);
//		        memberDao.updateWithKakao(kakaoUser);
//		    } else {
//		        // 존재하지 않으면 새로운 유저 정보 삽입
//		        kakaoUserDao.insert(kakaoUser);
//
//		        // 카카오 유저 ID로 멤버 테이블에 삽입
//		        Integer kakaoUserId = kakaoUser.getKakaoUserId();
//		        if (kakaoUserId != null) {
//		            memberDao.insertWithKakao(kakaoUser);  // 새로운 멤버 추가
//		        } else {
//		            throw new RuntimeException("Kakao User ID 생성 실패");
//		        }
//		    }



	public void updateKakaoUserEmail(String kakaoId, String memberEmail) {
	    // 파라미터를 담을 Map 생성
	    Map<String, Object> params = new HashMap<>();
	    params.put("kakaoId", kakaoId);
	    params.put("memberEmail", memberEmail);

	    try {
	        // MyBatis 매퍼를 호출하여 kakao_user 테이블의 이메일 업데이트
	        int updatedRows = sqlSession.update("kakaoUser.updateKakaoUserEmail", params);
	        if (updatedRows > 0) {
	            System.out.println("KakaoUser 이메일 업데이트 성공: kakaoId = " + kakaoId + ", memberEmail = " + memberEmail);

	            // member 테이블에서도 이메일 업데이트
	            memberDao.updateMemberEmail(kakaoId, memberEmail);
	        } else {
	            System.out.println("KakaoUser 이메일 업데이트 실패: 해당 kakaoId를 찾을 수 없음 = " + kakaoId);
	        }
	    } catch (Exception e) {
	        System.out.println("KakaoUser 이메일 업데이트 중 오류 발생: kakaoId = " + kakaoId);
	        e.printStackTrace();
	    }
	}



}
