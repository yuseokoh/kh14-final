package com.game.dao;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.game.dto.FriendDto;
import com.game.dto.MemberDto;

@Repository
public class FriendDao {
	
	@Autowired
	private SqlSession sqlSession;
	
	public int sequence() {
		return sqlSession.selectOne("friend.friendSequence");
	}
	public List<MemberDto> memberList(){
		return sqlSession.selectList("friend.memberList");
	}
	
	public List<MemberDto> searchMember(String memberId){
		return sqlSession.selectList("friend.searchMember", memberId);
	}
	
	public List<FriendDto> friendList(String memberId){
		return sqlSession.selectList("friend.friendList", memberId);
	}
	
	public void request(FriendDto friendDto) {
		sqlSession.insert("friend.request", friendDto);
	}
	
	public List<FriendDto> requestToOther(String memberId){
		return sqlSession.selectList("friend.requestToOther", memberId);
	}
	
	public List<FriendDto> requestFromOther(String memberId){
		return sqlSession.selectList("friend.requestFromOther", memberId);
	}
	
	public FriendDto selectOne(int friendFk) {
		return sqlSession.selectOne("friend.find", friendFk);
	}
	
	public boolean requestDelete(int friendFk) {
		return sqlSession.delete("friend.requestDelete", friendFk) > 0;
	}
	
	public boolean getRequest(int friendFk) {
		return sqlSession.update("friend.getRequest", friendFk) > 0;
	}
}
