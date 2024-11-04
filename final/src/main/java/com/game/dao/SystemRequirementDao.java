package com.game.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.game.dto.SystemRequirementDto;

@Repository
public class SystemRequirementDao {
    
    @Autowired
    private SqlSession sqlSession;
    
    // 게임의 시스템 요구사항 조회
    public List<SystemRequirementDto> findByGameNo(int gameNo) {
        return sqlSession.selectList("systemRequirement.findByGameNo", gameNo);
    }
    
    // 시스템 요구사항 등록
    public void insert(SystemRequirementDto systemRequirementDto) {
        sqlSession.insert("systemRequirement.insert", systemRequirementDto);
    }
    
    // 시스템 요구사항 수정
    public boolean update(SystemRequirementDto systemRequirementDto) {
        return sqlSession.update("systemRequirement.update", systemRequirementDto) > 0;
    }
    
    // 시스템 요구사항 삭제
    public boolean delete(int requirementId) {
        return sqlSession.delete("systemRequirement.delete", requirementId) > 0;
    }
    
    // 게임의 특정 타입 시스템 요구사항 조회
    public SystemRequirementDto findByGameNoAndType(int gameNo, String requirementType) {
        return sqlSession.selectOne("systemRequirement.findByGameNoAndType", 
            Map.of("gameNo", gameNo, "requirementType", requirementType));
    }
}