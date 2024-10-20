package com.game.dao;


import com.game.dto.CartDto;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CartDao {

    @Autowired
    private SqlSession sqlSession;

    // 장바구니에 아이템 추가
    public void insert(CartDto cartDto) {
        sqlSession.insert("cart.insert", cartDto);
    }

    // 장바구니 아이템 삭제
    public boolean delete(int cartId) {
        int result = sqlSession.delete("cart.delete", cartId);
        return result > 0;
    }

    // 특정 회원의 장바구니 조회
    public List<CartDto> listByMemberId(String memberId) {
        return sqlSession.selectList("cart.list", memberId);
    }
    
    public int findGameNo(String gameTitle) {
        return sqlSession.selectOne("cart.findGameNo", gameTitle);
    }
}
