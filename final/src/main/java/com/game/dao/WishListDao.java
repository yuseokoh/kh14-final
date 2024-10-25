package com.game.dao;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import com.game.dto.WishListDto;
@Repository
public class WishListDao {
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	private SqlSession sqlSession;
	
	public int sequence() {
		return sqlSession.selectOne("wishlist.sequence");
	}
	
	public void insert(WishListDto wishListDto) {
		sqlSession.insert("wishlist.insert",wishListDto);
	}
	
	public List<WishListDto> list(){
		return sqlSession.selectList("wishlist.list");
	}
	
	public boolean delete(int wishListId) {
		return sqlSession.delete("wishlist.delete",wishListId)>0;
	}
	
	public List<WishListDto> selectListByMemberId(String memberId) {
		return sqlSession.selectList("wishlist.listByMemberId",memberId);
	}
	
	public List<WishListDto> search(String column,String keyword){
		Map<String, Object> params = new HashMap<>();
		params.put("column", column);
		params.put("keyword", keyword);
		return sqlSession.selectList("wishlist.search",params);
	}
}