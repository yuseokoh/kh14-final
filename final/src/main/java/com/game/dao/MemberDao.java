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
import com.game.vo.MemberComplexRequestVO;

@Repository
public class MemberDao {

	@Autowired
	private SqlSession sqlSession;
	@Autowired
	private PasswordEncoder encoder;
	
	@Autowired
	private KakaoUserDao kakaoUserDao;

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
		memberDto.setMemberLevel("BASIC");
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
	    // KakaoUserDto에서 kakaoUserId 값을 수동으로 생성하거나 받아옴
	    Integer kakaoUserId = getKakaoUserIdFromKakaoId(kakaoUser.getKakaoId()); // 예시 로직
	    kakaoUser.setKakaoUserId(kakaoUserId);

	    // kakao_user 테이블에 저장
	    kakaoUserDao.insert(kakaoUser);

	    // member 테이블에 외래키 연동
	    MemberDto memberDto = new MemberDto();
	    memberDto.setKakaoUserId(kakaoUserId);
	    memberDto.setMemberId("kakao_" + kakaoUser.getKakaoId());
	    memberDto.setMemberEmail(kakaoUser.getMemberEmail());
	    memberDto.setMemberNickname(kakaoUser.getMemberNickname());
	    memberDto.setMemberLevel("BASIC");
	    
	    sqlSession.insert("member.addWithKakao", memberDto);
	}



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
	    return newUser.getKakaoUserId();  // 새로 삽입된 kakao_user_id 반환
	}


	// 카카오 연동 시 kakao_user_id 업데이트 메서드 추가
	public boolean updateKakaoUserId(String memberId, Integer kakaoUserId) {
		Map<String, Object> params = new HashMap<>();
		params.put("memberId", memberId);
		params.put("kakaoUserId", kakaoUserId);
		return sqlSession.update("member.updateKakaoUserId", params) > 0;
	}

}
