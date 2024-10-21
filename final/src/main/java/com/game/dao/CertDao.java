package com.game.dao;

import java.util.List;
import java.util.Optional;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.game.dto.CertDto;
import com.game.mapper.CertMapper;

@Repository
public class CertDao {

	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	private CertMapper certMapper;
	@Autowired
	private SqlSession sqlSession;
	
	public void insert(CertDto certDto) {
		String sql = "insert into cert(cert_email, cert_number) values(?, ?)";
		Object[] data = {certDto.getCertEmail(), certDto.getCertNumber()};
		jdbcTemplate.update(sql, data);
	}
	public boolean delete(String certEmail) {
		String sql = "delete cert where cert_email = ?";
		Object[] data = {certEmail};
		return jdbcTemplate.update(sql, data) > 0;
	}
	
	//이메일과 인증번호가 유효한지 검사하는 기능
	public boolean check(CertDto certDto, int duration) {
		String sql = "select * from cert "
						+ "where cert_email=? "
									+ "and cert_number=? "
									+ "and cert_time between sysdate-?/60/24 and sysdate";
		Object[] data = {
				certDto.getCertEmail(), certDto.getCertNumber(), duration
		};
		List<CertDto> list = jdbcTemplate.query(sql, certMapper, data);
		return list.size() > 0;
	}
	public Optional<CertDto> selectOneByEmail(String memberEmail) {
	    String sql = "select * from cert where cert_email = ?";
	    Object[] data = {memberEmail};
	    List<CertDto> list = jdbcTemplate.query(sql, certMapper, data);
	    return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
	}
	
	
	  public CertDto findByEmail(String memberEmail) {
	        return sqlSession.selectOne("com.game.mapper.CertMapper.findByEmail", memberEmail);
	    }

}