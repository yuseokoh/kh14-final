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

        // API 호출 로그
        log.info("카카오 API 호출 시작: URL = {}, AccessToken = {}", uri, accessToken);

        
        Map<String, Object> response = restTemplate.postForObject(uri, entity, Map.class);
        if (response == null) {
            throw new IllegalStateException("카카오 API 응답이 null입니다.");
        }

        log.info("카카오 API 응답: {}", response);

        String kakaoId = String.valueOf(response.get("id"));
        Map<String, String> properties = (Map<String, String>) response.get("properties");
        String nickname = properties != null ? properties.getOrDefault("nickname", "카카오 사용자") : "카카오 사용자";
        Map<String, String> kakaoAccount = (Map<String, String>) response.get("kakao_account");
        String email = (kakaoAccount != null) ? kakaoAccount.getOrDefault("email", "no-email@example.com") : "no-email@example.com";

        log.info("추출된 카카오 사용자 정보 - ID: {}, 닉네임: {}, 이메일: {}", kakaoId, nickname, email);

        KakaoUserDto kakaoUser = new KakaoUserDto();
        kakaoUser.setKakaoId(kakaoId);
        kakaoUser.setMemberNickname(nickname);
        kakaoUser.setMemberEmail(email);
        kakaoUser.setMemberJoin(new java.sql.Date(System.currentTimeMillis()));
        log.info("생성된 KakaoUserDto 객체: {}", kakaoUser);
        return kakaoUser;
    }


	
//    @Transactional
//    public KakaoUserDto saveOrUpdateKakaoUser(KakaoUserDto kakaoUser) {
//        Optional<KakaoUserDto> existingUser = kakaoUserDao.selectOneByKakaoId(kakaoUser.getKakaoId());
//
//        if (!existingUser.isPresent()) {
//            log.info("새로운 카카오 유저 저장: {}", kakaoUser);
//            kakaoUserDao.insert(kakaoUser);
//
//            Integer kakaoUserId = kakaoUserDao.getKakaoUserIdByKakaoId(kakaoUser.getKakaoId());
//            kakaoUser.setKakaoUserId(kakaoUserId); // kakao_user_id 설정
//        } else {
//            log.info("기존 유저 업데이트: {}", kakaoUser);
//            KakaoUserDto existing = existingUser.get();
//
//            if (existing.getMemberEmail() != null && !existing.getMemberEmail().equals("no-email@example.com")) {
//                kakaoUser.setMemberEmail(existing.getMemberEmail());
//            }
//
//            existing.setMemberNickname(kakaoUser.getMemberNickname() != null ? kakaoUser.getMemberNickname() : existing.getMemberNickname());
//            existing.setMemberJoin(kakaoUser.getMemberJoin() != null ? kakaoUser.getMemberJoin() : existing.getMemberJoin());
//
//            kakaoUserDao.updateKakaoUser(existing);
//            return existing; // 업데이트된 기존 사용자 반환
//        }
//
//        return kakaoUser; // 새로운 사용자 정보 반환
//    }

    @Transactional
    public KakaoUserDto saveOrUpdateKakaoUser(KakaoUserDto kakaoUser) {
        log.info("saveOrUpdateKakaoUser 호출 - kakaoId: {}, memberEmail: {}", 
                 kakaoUser.getKakaoId(), kakaoUser.getMemberEmail());

        Optional<KakaoUserDto> existingUser = kakaoUserDao.selectOneByKakaoId(kakaoUser.getKakaoId());
        log.info("기존 사용자 존재 여부: {}", existingUser.isPresent());
        // 여기에서 기존 사용자의 존재 여부를 로그로 기록합니다.
        log.info("기존 사용자 존재 여부: {}", existingUser.isPresent());

        if (!existingUser.isPresent()) {
            log.info("KakaoUser 삽입 시도 - kakaoId: {}, memberNickname: {}, memberEmail: {}", 
                     kakaoUser.getKakaoId(), 
                     kakaoUser.getMemberNickname(), 
                     kakaoUser.getMemberEmail());
            
            try {
                kakaoUserDao.insert(kakaoUser);
                log.info("Kakao 유저 삽입 성공 - kakaoId: {}", kakaoUser.getKakaoId());

                // 데이터베이스에 삽입된 데이터 확인
                Optional<KakaoUserDto> insertedUser = kakaoUserDao.selectOneByKakaoId(kakaoUser.getKakaoId());
                if (!insertedUser.isPresent()) {
                    log.error("Kakao 유저가 삽입 후에도 데이터베이스에서 조회되지 않습니다.");
                    throw new RuntimeException("Kakao 유저 삽입에 문제가 발생했습니다.");
                } else {
                    log.info("Kakao 유저가 데이터베이스에 정상적으로 삽입되었습니다.");
                }
            } catch (Exception e) {
                log.error("Kakao 유저 삽입 중 오류 발생", e);
                throw new RuntimeException("Kakao 유저 삽입에 실패했습니다.");
            }

            Integer kakaoUserId = kakaoUserDao.getKakaoUserIdByKakaoId(kakaoUser.getKakaoId());
            if (kakaoUserId != null) {
                kakaoUser.setKakaoUserId(kakaoUserId);
                log.info("kakao_user_id 설정 완료: {}", kakaoUserId);
            } else {
                log.warn("kakao_user_id가 null입니다. 데이터 삽입에 문제 발생 가능성 있음.");
            }
        } else {
            log.info("기존 유저 업데이트: {}", kakaoUser);
            KakaoUserDto existing = existingUser.get();
            if (existing.getMemberEmail() != null && !existing.getMemberEmail().equals("no-email@example.com")) {
                kakaoUser.setMemberEmail(existing.getMemberEmail());
            }
            existing.setMemberNickname(kakaoUser.getMemberNickname() != null ? kakaoUser.getMemberNickname() : existing.getMemberNickname());
            existing.setMemberJoin(kakaoUser.getMemberJoin() != null ? kakaoUser.getMemberJoin() : existing.getMemberJoin());
            kakaoUserDao.updateKakaoUser(existing);
            log.info("기존 유저 업데이트 완11료: {}", existing);
            return existing;
        }
        return kakaoUser;
    }





//    @Transactional
//    public void insertWithKakao(KakaoUserDto kakaoUser) {
//        Integer kakaoUserId = kakaoUser.getKakaoUserId(); // KakaoUserDto에서 kakao_user_id 가져오기
//
//        if (kakaoUserId == null) {
//            throw new RuntimeException("Kakao User ID가 없습니다.");
//        }
//
//        MemberDto memberDto = new MemberDto();
//        memberDto.setKakaoUserId(kakaoUserId); // 외래키로 연동된 kakao_user_id 설정
//        memberDto.setMemberId(kakaoUser.getKakaoId());
//        memberDto.setMemberEmail(kakaoUser.getMemberEmail());
//        memberDto.setMemberNickname(kakaoUser.getMemberNickname());
//        memberDto.setMemberLevel("카카오 회원");
//
//        try {
//            sqlSession.insert("member.addWithKakao", memberDto);
//            log.info("member 테이블에 데이터 삽입 성공");
//        } catch (Exception e) {
//            log.error("member 테이블에 데이터 삽입 실패", e);
//            throw new RuntimeException("member 테이블 삽입 실패");
//        }
//    }


	// 카카오 유저 이메일 업데이트 로직
//	public void insertWithKakao(KakaoUserDto kakaoUser) {
//	    Integer kakaoUserId = kakaoUser.getKakaoUserId(); // KakaoUserDto에서 kakao_user_id 가져오기
//
//	    if (kakaoUserId == null) {
//	        throw new RuntimeException("Kakao User ID가 없습니다.");
//	    }
//
//	    // member 테이블에 kakao_user_id를 포함한 데이터를 삽입
//	    MemberDto memberDto = new MemberDto();
//	    memberDto.setKakaoUserId(kakaoUserId);  // 외래키로 연동된 kakao_user_id 설정
//	    memberDto.setMemberId( kakaoUser.getKakaoId()); 
//	    memberDto.setMemberEmail(kakaoUser.getMemberEmail());
//	    memberDto.setMemberNickname(kakaoUser.getMemberNickname()); // 이미 설정된 닉네임 사용
//	    memberDto.setMemberLevel("카카오 회원");
//
//	    try {
//	        sqlSession.insert("member.addWithKakao", memberDto);
//	        log.info("member 테이블에 데이터 삽입 성공");
//	    } catch (Exception e) {
//	        log.error("member 테이블에 데이터 삽입 실패", e);
//	    }
//	}





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



//	public void updateKakaoUserEmail(String kakaoId, String memberEmail) {
//	    // 파라미터를 담을 Map 생성
//	    Map<String, Object> params = new HashMap<>();
//	    params.put("kakaoId", kakaoId);
//	    params.put("memberEmail", memberEmail);
//
//	    try {
//	        // MyBatis 매퍼를 호출하여 kakao_user 테이블의 이메일 업데이트
//	        int updatedRows = sqlSession.update("kakaoUser.updateKakaoUserEmail", params);
//	        if (updatedRows > 0) {
//	            System.out.println("KakaoUser 이메일 업데이트 성공: kakaoId = " + kakaoId + ", memberEmail = " + memberEmail);
//
//	            // member 테이블에서도 이메일 업데이트
//	            memberDao.updateMemberEmail(kakaoId, memberEmail);
//	        } else {
//	            System.out.println("KakaoUser 이메일 업데이트 실패: 해당 kakaoId를 찾을 수 없음 = " + kakaoId);
//	        }
//	    } catch (Exception e) {
//	        System.out.println("KakaoUser 이메일 업데이트 중 오류 발생: kakaoId = " + kakaoId);
//	        e.printStackTrace();
//	    }
//	}
	
    public void updateKakaoUserEmailAndLink(String kakaoId, String memberEmail) {
        Map<String, Object> params = new HashMap<>();
        params.put("kakaoId", kakaoId);
        params.put("memberEmail", memberEmail);

        try {
            // 1. 카카오 유저 테이블의 이메일 업데이트
            int updatedRows = sqlSession.update("kakaoUser.updateKakaoUserEmail", params);
            if (updatedRows > 0) {
                log.info("KakaoUser 이메일 업데이트 성공: kakaoId = {}, memberEmail = {}", kakaoId, memberEmail);

                // 2. 멤버 테이블에서 이메일로 회원 조회
                Optional<MemberDto> existingMember = memberDao.selectOneByEmail(memberEmail);
                if (existingMember.isPresent()) {
                    // 3. 회원이 존재하면 카카오 유저와 연동
                    String memberId = existingMember.get().getMemberId();
                    kakaoUserDao.updateLinkedMemberId(kakaoId, memberId);
                    log.info("카카오 유저와 기존 회원 연동 완료: memberId = {}", memberId);
                } else {
                    log.warn("Member 테이블에서 해당 이메일을 가진 회원을 찾을 수 없습니다.");
                }
            } else {
                log.warn("KakaoUser 이메일 업데이트 실패: 해당 kakaoId를 찾을 수 없음 = {}", kakaoId);
            }
        } catch (Exception e) {
            log.error("KakaoUser 이메일 업데이트 및 회원 연동 중 오류 발생: kakaoId = {}", kakaoId, e);
        }
    }

    @Transactional
    public void linkKakaoAndMemberAccounts(String kakaoId, String memberEmail) {
        log.info("linkKakaoAndMemberAccounts - 받은 이메일 값: {}", memberEmail);
        if (memberEmail == null || memberEmail.isEmpty()) {
            log.warn("전달된 이메일이 null이거나 비어 있습니다: kakaoId = {}", kakaoId);
            return;
        }
     // 이메일 값이 제대로 전달되었는지 확인하는 디버깅 로그
        log.info("linkKakaoAndMemberAccounts 메서드 호출 - kakaoId: {}, memberEmail: {}", kakaoId, memberEmail);
        // 멤버 테이블에서 이메일로 회원 찾기
        Optional<MemberDto> existingMember = memberDao.selectOneByEmail(memberEmail);
        log.info("이메일로 회원 조회 결과 존재 여부: {}", existingMember.isPresent());
        if (existingMember.isPresent()) {
            String memberId = existingMember.get().getMemberId();
            
            try {
                // 올바른 파라미터를 사용하여 linked_member_id 설정
                Map<String, Object> params = new HashMap<>();
                params.put("kakaoId", kakaoId);
                params.put("memberId", memberId);

                // linked_member_id 업데이트
                kakaoUserDao.updateLinkedMemberId(params);
                log.info("카카오 유저와 기존 회원 연동 완료: memberId = {}", memberId);
            } catch (Exception e) {
                log.error("카카오 유저 연동 중 오류 발생: ", e);
                throw new RuntimeException("카카오 유저 연동에 실패했습니다.");
            }
        } else {
            log.warn("일치하는 회원이 없습니다. 추가 회원가입 로직을 처리해야 합니다.");
        }
    }





	
	
//	public void linkKakaoAndMemberAccounts(MemberDto existingMember, KakaoUserDto kakaoUserDto) {
//	    if (existingMember == null || kakaoUserDto == null) {
//	        throw new IllegalArgumentException("existingMember 또는 kakaoUserDto가 null입니다.");
//	    }
//	    if (kakaoUserDto.getKakaoId() == null || kakaoUserDto.getKakaoId().isEmpty()) {
//	        throw new IllegalArgumentException("Kakao ID가 null이거나 비어 있습니다.");
//	    }
//	    if (existingMember.getMemberEmail() == null || existingMember.getMemberEmail().isEmpty()) {
//	        throw new IllegalArgumentException("Member 이메일이 null이거나 비어 있습니다.");
//	    }
//
//	    try {
//	        log.info("기존 멤버와 카카오 유저 연동: memberId = {}, kakaoId = {}", existingMember.getMemberId(), kakaoUserDto.getKakaoId());
//
//	        // 카카오 유저에 기존 멤버 ID를 연동
//	        kakaoUserDto.setLinkedMemberId(existingMember.getMemberId());
//	        kakaoUserDao.updateKakaoUser(kakaoUserDto);
//
//	        // 이메일 업데이트
//	        existingMember.setMemberEmail(kakaoUserDto.getMemberEmail());
//	        memberDao.updateMemberEmail(existingMember);
//	        log.info("Member 테이블 이메일 업데이트 성공: {}", existingMember);
//
//	        log.info("계정 연동 완료: memberId = {}, linkedMemberId = {}", existingMember.getMemberId(), kakaoUserDto.getLinkedMemberId());
//	    } catch (Exception e) {
//	        log.error("계정 연동 중 오류 발생", e);
//	        throw new RuntimeException("계정 연동에 실패했습니다.");
//	    }
//	}





	  
	  
	  @Transactional
	  public void insertKakaoUserAndMember(KakaoUserDto kakaoUser) {
	      KakaoUserDto savedKakaoUser = saveOrUpdateKakaoUser(kakaoUser);
	      Integer kakaoUserId = savedKakaoUser.getKakaoUserId();

	      if (kakaoUserId != null) {
	          log.info("멤버 테이블에 카카오 사용자 연동: kakaoUserId = {}", kakaoUserId);
	          
	          // 중복 이메일 허용하도록 멤버 데이터 삽입
	          try {
	              memberDao.insertWithKakao(savedKakaoUser);
	          } catch (Exception e) {
	              log.error("member 테이블에 데이터 삽입 중 오류 발생", e);
	              throw new RuntimeException("member 테이블 삽입 실패");
	          }
	      } else {
	          log.error("Kakao User ID 생성 실패");
	          throw new RuntimeException("Kakao User ID 생성 실패");
	      }
	  }



	  public void createKakaoUser(KakaoUserDto kakaoUserDto) {
		    if (kakaoUserDto == null) {
		        throw new IllegalArgumentException("KakaoUserDto가 null입니다.");
		    }

		    try {
		        log.info("새로운 카카오 유저 생성: {}", kakaoUserDto);
		        // 새로운 카카오 유저 삽입
		        kakaoUserDao.insert(kakaoUserDto);

		        // 삽입 후 생성된 kakao_user_id를 가져와 설정
		        Integer kakaoUserId = kakaoUserDao.getKakaoUserIdByKakaoId(kakaoUserDto.getKakaoId());
		        kakaoUserDto.setKakaoUserId(kakaoUserId);
		        log.info("카카오 유저 생성 완료, ID: {}", kakaoUserId);
		    } catch (Exception e) {
		        log.error("카카오 유저 생성 중 오류 발생", e);
		        throw new RuntimeException("카카오 유저 생성에 실패했습니다.");
		    }
		}



	  public KakaoUserDto findKakaoUserById(String kakaoId) {
		    if (kakaoId == null || kakaoId.isEmpty()) {
		        throw new IllegalArgumentException("Kakao ID가 null이거나 비어 있습니다.");
		    }

		    // `kakaoUserDao.selectOneByKakaoId` 메서드를 사용하여 카카오 유저를 조회합니다.
		    Optional<KakaoUserDto> kakaoUserOptional = kakaoUserDao.selectOneByKakaoId(kakaoId);

		    if (kakaoUserOptional.isPresent()) {
		        log.info("카카오 유저 조회 성공 - kakaoId: {}", kakaoId);
		        return kakaoUserOptional.get();
		    } else {
		        log.warn("카카오 유저를 찾을 수 없습니다 - kakaoId: {}", kakaoId);
		        return null;
		    }
		}
	  
	  
	  
	  
	  public void linkKakaoUserWithMember(String kakaoId, String kakaoEmail) {
		    // 1. 이메일로 멤버를 조회
		    Optional<MemberDto> memberOptional = memberDao.selectOneByEmail(kakaoEmail);

		    if (memberOptional.isEmpty()) {
		        log.warn("이메일에 해당하는 멤버를 찾을 수 없습니다: " + kakaoEmail);
		        return;
		    }

		    // 2. 찾은 멤버의 ID를 linked_member_id에 저장
		    String memberId = memberOptional.get().getMemberId();
		    Map<String, Object> params = new HashMap<>();
		    params.put("kakaoId", kakaoId);
		    params.put("linkedMemberId", memberId);

		    // 3. kakao_user 테이블의 linked_member_id 필드를 업데이트
		    int updatedRows = sqlSession.update("kakao_user.updateLinkedMemberId", params);
		    if (updatedRows > 0) {
		        log.info("kakao_user 테이블의 linked_member_id 업데이트 성공: " + memberId);
		    } else {
		        log.warn("kakao_user 테이블의 linked_member_id 업데이트 실패: " + kakaoEmail);
		    }
		}










}
