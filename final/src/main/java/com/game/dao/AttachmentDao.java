// AttachmentDao.java
package com.game.dao;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.game.dto.AttachmentDto;

@Repository
public class AttachmentDao {
    @Autowired
    private SqlSession sqlSession;
    
    public int sequence() {
        return sqlSession.selectOne("attachment.sequence");
    }
    
    public void insert(AttachmentDto attachmentDto) {
        sqlSession.insert("attachment.add", attachmentDto);
    }
    
    public AttachmentDto selectOne(int attachmentNo) {
        return sqlSession.selectOne("attachment.find", attachmentNo);
    }
    
    public boolean delete(int attachmentNo) {
        return sqlSession.delete("attachment.remove", attachmentNo) > 0;
    }
}