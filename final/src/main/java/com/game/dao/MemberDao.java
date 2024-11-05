package com.game.dao;

import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import com.game.dto.KakaoUserDto;
import com.game.dto.MemberDto;
import com.game.dto.MemberTokenDto;
import com.game.vo.MemberComplexRequestVO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class MemberDao {

	@Autowired
	private SqlSession sqlSession;
	@Autowired
	private PasswordEncoder encoder;

	@Autowired
	private KakaoUserDao kakaoUserDao;

	@Autowired
	private MemberTokenDao memberTokenDao;

	public List<MemberDto> complexSearch(MemberComplexRequestVO vo) {
		return sqlSession.selectList("member.complexSearch", vo);
	}

	public int complexSearchCount(MemberComplexRequestVO vo) {
		return sqlSession.selectOne("member.complexSearchCount", vo);
	}

	public MemberDto selectOne(String memberId) {
		return sqlSession.selectOne("member.find", memberId);
	}

	public MemberDto selectOneById(String memberId) {
		return sqlSession.selectOne("member.selectOneById", memberId);
	}

	public Optional<MemberDto> selectOneByEmail(String memberEmail) {
		return Optional.ofNullable(sqlSession.selectOne("member.selectOneByEmail", memberEmail));
	}

	public boolean updateEmailVerified(String memberId) {
		return sqlSession.update("member.updateEmailVerified", memberId) > 0;
	}

	public boolean updateVerificationToken(String memberId, String token) {
		Map<String, Object> params = new HashMap<>();
		params.put("memberId", memberId);
		params.put("token", token);
		return sqlSession.update("member.updateVerificationToken", params) > 0;
	}

	// 회원가입(등록)
	public void insert(MemberDto memberDto) {
		// 비밀번호 암호화
		String rawPw = memberDto.getMemberPw(); // 비밀번호 암호화 안된 것
		String encPw = encoder.encode(rawPw); // 암호화된 비밀번호
		memberDto.setMemberPw(encPw);
		memberDto.setMemberLevel("일반회원");
		sqlSession.insert("member.add", memberDto);
	}

	// 회원 아이디 중복 검사
	public boolean checkId(String memberId) {
		MemberDto memberDto = sqlSession.selectOne("member.find", memberId);
		return memberDto == null;
	}

	// 비밀번호 변경
	public boolean updatePassword(String memberId, String newPassword) {
		String encPw = encoder.encode(newPassword); // 비밀번호 암호화
		Map<String, Object> params = new HashMap<>();
		params.put("memberId", memberId);
		params.put("memberPw", encPw);
		return sqlSession.update("member.changePw", params) > 0;
	}

	// 회원 정보 수정
	public boolean updateMemberInfo(MemberDto memberDto) {
		return sqlSession.update("member.edit", memberDto) > 0;
	}

	// 회원 정보 삭제
	public boolean deleteMember(String memberId) {
		return sqlSession.delete("member.remove", memberId) > 0;
	}

	// 최종 로그인 시각 갱신
	public boolean updateLastLogin(String memberId) {
		return sqlSession.update("member.updateLoginTime", memberId) > 0;
	}

	// 로그아웃 시각 갱신
	public boolean updateLogoutTime(String memberId) {
		return sqlSession.update("member.updateLogoutTime", memberId) > 0;
	}

	// 카카오 사용자 확인용 메서드 추가
	public Optional<MemberDto> selectOneByKakaoId(String kakaoId) {
		return Optional.ofNullable(sqlSession.selectOne("member.selectOneByKakaoId", kakaoId));
	}

	// 카카오 회원 가입 처리 메서드
	public void insertWithKakao(KakaoUserDto kakaoUser) {
		Integer kakaoUserId = kakaoUser.getKakaoUserId();

		if (kakaoUserId == null) {
			throw new RuntimeException("Kakao User ID가 없습니다.");
		}

		// member_id가 이미 존재하는지 확인
		MemberDto existingMember = selectByMemberId("kakao_" + kakaoUser.getKakaoId());
		if (existingMember != null) {
			log.info("중복된 member_id가 존재합니다: " + existingMember.getMemberId());
			// 중복된 member_id가 있을 경우 로그인 처리 진행 (삽입 없이)
			return;
		}

		// 중복되지 않으면 member 테이블에 삽입
		MemberDto memberDto = new MemberDto();
//		memberDto.setKakaoUserId(kakaoUserId);
		memberDto.setMemberId(kakaoUser.getKakaoId());
		memberDto.setMemberEmail(kakaoUser.getMemberEmail());
		memberDto.setMemberNickname(kakaoUser.getMemberNickname());
		memberDto.setMemberLevel("카카오 회원");

		try {
			sqlSession.insert("member.addWithKakao", memberDto);
			log.info("member 테이블에 데이터 삽입 성공");
		} catch (Exception e) {
			log.error("member 테이블에 데이터 삽입 실패", e);
		}
	}

//	public void insertWithKakao(KakaoUserDto kakaoUser) {
//	    // KakaoUserDto에서 kakaoUserId 값을 수동으로 생성하거나 받아옴
//	    Integer kakaoUserId = getKakaoUserIdFromKakaoId(kakaoUser.getKakaoId()); // 예시 로직
//	    kakaoUser.setKakaoUserId(kakaoUserId);
//
//	    // kakao_user 테이블에 저장
//	    kakaoUserDao.insert(kakaoUser);
//
//	    // member 테이블에 외래키 연동
//	    MemberDto memberDto = new MemberDto();
//	    memberDto.setKakaoUserId(kakaoUserId);
//	    memberDto.setMemberId("kakao_" + kakaoUser.getKakaoId());
//	    memberDto.setMemberEmail(kakaoUser.getMemberEmail());
//	    memberDto.setMemberNickname(kakaoUser.getMemberNickname());
//	    memberDto.setMemberLevel("일반회원");
//	    
//	    try {
//	        sqlSession.insert("member.addWithKakao", memberDto);
//	        log.info("member 테이블에 데이터 삽입 성공");
//	    } catch (Exception e) {
//	        log.error("member 테이블에 데이터 삽입 실패", e);
//	    }
//
//	}

	private Integer getKakaoUserIdFromKakaoId(String kakaoId) {
		// 1. kakao_id를 이용해 기존 사용자 검색
		Optional<KakaoUserDto> existingUser = kakaoUserDao.selectOneByKakaoId(kakaoId);

		// 2. 기존 사용자가 존재하면 kakao_user_id 반환
		if (existingUser.isPresent()) {
			return existingUser.get().getKakaoUserId();
		}

		// 3. 존재하지 않으면 새로운 사용자 추가 후 kakao_user_id 반환
		KakaoUserDto newUser = new KakaoUserDto();
		newUser.setKakaoId(kakaoId);
//	    newUser.setMemberJoin(new Date()); 
		newUser.setMemberJoin(new Date(System.currentTimeMillis()));// 가입 날짜 설정

		// 새로운 카카오 사용자 저장
		kakaoUserDao.insert(newUser);

		// 저장 후 새로운 사용자 정보 가져오기
		return newUser.getKakaoUserId(); // 새로 삽입된 kakao_user_id 반환
	}

	// 카카오 연동 시 kakao_user_id 업데이트 메서드 추가
	public boolean updateKakaoUserId(String memberId, Integer kakaoUserId) {
		Map<String, Object> params = new HashMap<>();
		params.put("memberId", memberId);
		params.put("kakaoUserId", kakaoUserId);
		return sqlSession.update("member.updateKakaoUserId", params) > 0;
	}

//	public void updateMemberEmail(MemberDto memberDto) {
//		// null 체크 및 기본값 설정
//		if (memberDto == null || memberDto.getMemberId() == null || memberDto.getMemberEmail() == null) {
//			System.out.println("MemberDto 또는 필수 필드가 null입니다.");
//			return;
//		}
//
//		// 필요에 따라 kakaoUserId가 null일 때 기본값 설정 (예: -1 또는 특정 값)
//		if (memberDto.getKakaoUserId() == null) {
//			System.out.println("kakaoUserId가 null입니다. 기본값을 설정합니다.");
//			memberDto.setKakaoUserId(-1); // 기본값 설정 예제
//		}
//
//		try {
//			// 이메일 업데이트 구문 실행
//			sqlSession.update("MemberMapper.updateMemberEmail", memberDto);
//			System.out.println("Member 테이블 이메일 업데이트 성공: " + memberDto);
//		} catch (Exception e) {
//			// 오류 발생 시 로그 출력
//			System.out.println("회원 이메일 업데이트 중 오류 발생: " + e.getMessage());
//		}
//	}

	public void updateWithKakao(KakaoUserDto kakaoUser) {
		// kakaoUser에서 필요한 정보를 추출해 member 정보를 업데이트
		Map<String, Object> params = new HashMap<>();
		params.put("kakaoUserId", kakaoUser.getKakaoUserId());
		params.put("memberNickname", kakaoUser.getMemberNickname());
		params.put("memberEmail", kakaoUser.getMemberEmail());

		try {
			// MyBatis 쿼리를 통해 member 정보를 업데이트
			int updatedRows = sqlSession.update("member.updateWithKakao", params);
			if (updatedRows > 0) {
				log.info("Member updated successfully for kakaoUserId: {}", kakaoUser.getKakaoUserId());
			} else {
				log.warn("No member found with kakaoUserId: {}", kakaoUser.getKakaoUserId());
			}
		} catch (Exception e) {
			log.error("Error updating member with kakaoUserId: {}", kakaoUser.getKakaoUserId(), e);
		}
	}

	// DAO 클래스에 해당 메서드를 구현
	public MemberDto selectByMemberId(String memberId) {
		return sqlSession.selectOne("member.selectByMemberId", memberId);
	}

//	public MemberDto findById(String memberId) {
//	    return selectByMemberId(memberId);  // memberId로 멤버를 조회하여 반환
//	}

	public void saveToken(MemberTokenDto tokenDto) {
		memberTokenDao.insert(tokenDto); // MemberTokenDao의 insert 메서드 사용하여 토큰 저장
	}

	public MemberDto findByEmail(String email) {
		return sqlSession.selectOne("member.findByEmail", email);
	}

	public MemberDto findById(String memberId) {
		return sqlSession.selectOne("member.selectByMemberId", memberId);
	}

//	public void updateMemberEmail(String memberId, String email) {
//		Map<String, Object> params = new HashMap<>();
//		params.put("memberId", memberId);
//		params.put("email", email);
//
//		try {
//			int updatedRows = sqlSession.update("member.updateEmailById", params);
//			if (updatedRows > 0) {
//				log.info("Member 테이블 이메일 업데이트 성공: memberId = {}", memberId);
//			} else {
//				log.warn("Member 테이블 이메일 업데이트 실패: 해당 memberId를 찾을 수 없음 = {}", memberId);
//			}
//		} catch (Exception e) {
//			log.error("Member 테이블 이메일 업데이트 중 오류 발생: memberId = {}", memberId, e);
//		}
//	}

	public MemberDto findByKakaoUserId(String kakaoId) {
		if (kakaoId == null || kakaoId.isEmpty()) {
			throw new IllegalArgumentException("Kakao ID가 null이거나 비어 있습니다.");
		}

		try {
			// MyBatis 매퍼를 호출하여 kakao_user_id로 멤버 검색
			MemberDto memberDto = sqlSession.selectOne("member.findByKakaoUserId", kakaoId);
			if (memberDto != null) {
				log.info("Member 테이블에서 멤버를 찾았습니다: memberId = {}", memberDto.getMemberId());
			} else {
				log.warn("Member 테이블에서 해당 kakao_user_id를 찾을 수 없습니다: kakaoId = {}", kakaoId);
			}
			return memberDto;
		} catch (Exception e) {
			log.error("Member 테이블에서 멤버 검색 중 오류 발생: kakaoId = {}", kakaoId, e);
			throw new RuntimeException("멤버 검색 실패", e);
		}
	}

	// 회원 정보 삭제 (카카오 유저 삭제 시 사용)
	public boolean deleteMemberByKakaoId(String kakaoId) {
		int deletedRows = sqlSession.delete("member.deleteByKakaoId", kakaoId);
		return deletedRows > 0;
	}

	public Optional<MemberDto> selectMemberWithKakaoInfo(String kakaoId) {
	    return Optional.ofNullable(sqlSession.selectOne("member.selectMemberWithKakaoInfo", kakaoId));
	}
	
	public Optional<MemberDto> selectOneByKakaoUserId(String kakaoId) {
	    if (kakaoId == null || kakaoId.isEmpty()) {
	        log.warn("kakaoId가 null이거나 비어 있습니다.");
	        return Optional.empty();
	    }

	    try {
	        MemberDto memberDto = sqlSession.selectOne("member.selectOneByKakaoUserId", kakaoId);
	        if (memberDto != null) {
	            log.info("MemberDto가 성공적으로 검색되었습니다: {}", memberDto);
	        } else {
	            log.info("kakaoId에 해당하는 MemberDto를 찾을 수 없습니다: {}", kakaoId);
	        }
	        return Optional.ofNullable(memberDto);
	    } catch (Exception e) {
	        log.error("kakaoId로 MemberDto 검색 중 오류 발생: {}", kakaoId, e);
	        throw new RuntimeException("kakaoId로 멤버 검색 실패", e);
	    }
	}

	public MemberDto selectOneByKakaoId2(String kakaoId) {
		return sqlSession.selectOne("member.selectOneByKakaoId2", kakaoId);
	}

	public MemberDto selectOneByEmail2(String memberEmail) {
		return sqlSession.selectOne("member.selectOneByEmail2", memberEmail);
	}
    public MemberDto findByEmail(String email) {
        return sqlSession.selectOne("member.findByEmail", email);
    }

    public MemberDto findById(String memberId) {
        return sqlSession.selectOne("member.selectByMemberId", memberId);
    }

    public void updateMemberEmail(String memberId, String email) {
        Map<String, Object> params = new HashMap<>();
        params.put("memberId", memberId);
        params.put("email", email);

        try {
            int updatedRows = sqlSession.update("member.updateEmailById", params);
            if (updatedRows > 0) {
                log.info("Member 테이블 이메일 업데이트 성공: memberId = {}", memberId);
            } else {
                log.warn("Member 테이블 이메일 업데이트 실패: 해당 memberId를 찾을 수 없음 = {}", memberId);
            }
        } catch (Exception e) {
            log.error("Member 테이블 이메일 업데이트 중 오류 발생: memberId = {}", memberId, e);
        }
    }



    public MemberDto findByKakaoUserId(String kakaoId) {
        if (kakaoId == null || kakaoId.isEmpty()) {
            throw new IllegalArgumentException("Kakao ID가 null이거나 비어 있습니다.");
        }

        try {
            // MyBatis 매퍼를 호출하여 kakao_user_id로 멤버 검색
            MemberDto memberDto = sqlSession.selectOne("member.findByKakaoUserId", kakaoId);
            if (memberDto != null) {
                log.info("Member 테이블에서 멤버를 찾았습니다: memberId = {}", memberDto.getMemberId());
            } else {
                log.warn("Member 테이블에서 해당 kakao_user_id를 찾을 수 없습니다: kakaoId = {}", kakaoId);
            }
            return memberDto;
        } catch (Exception e) {
            log.error("Member 테이블에서 멤버 검색 중 오류 발생: kakaoId = {}", kakaoId, e);
            throw new RuntimeException("멤버 검색 실패", e);
        }
    }
    
    public List<MemberDto> selectDeveloperRequests() {
        return sqlSession.selectList("member.selectDeveloperRequests");
    }
    
    public boolean updateDeveloperRequest(Map<String, Object> params) {
        return sqlSession.update("member.updateDeveloperRequest", params) > 0;
    }
    
    public boolean updateMemberLevel(Map<String, Object> params) {
        try {
            int result = sqlSession.update("member.updateMemberLevel", params);
            return result > 0;
        } catch (Exception e) {
            log.error("회원 레벨 업데이트 중 오류 발생", e);
            return false;
        }
    }

}
