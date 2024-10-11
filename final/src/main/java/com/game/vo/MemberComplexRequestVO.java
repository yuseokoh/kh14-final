package com.game.vo;

import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.game.advice.JsonEmptyIntegerToNullDeserializer;
import com.game.advice.JsonEmptyStringToNullDeserializer;

import lombok.Data;

@Data
public class MemberComplexRequestVO {
	@JsonDeserialize(using = JsonEmptyStringToNullDeserializer.class)
	private String memberId;
	@JsonDeserialize(using = JsonEmptyStringToNullDeserializer.class)
	private String memberEmail;
	@JsonDeserialize(using = JsonEmptyStringToNullDeserializer.class)
	private String memberLevel;
	@JsonDeserialize(using = JsonEmptyIntegerToNullDeserializer.class)
	private Integer memberPoint;
	@JsonDeserialize(using = JsonEmptyStringToNullDeserializer.class)
	private String beginMemberJoin, endMemberJoin;
	@JsonDeserialize(using = JsonEmptyStringToNullDeserializer.class)
	private String beginMemberLogin, endMemberLogin;
	@JsonDeserialize(using = JsonEmptyIntegerToNullDeserializer.class)
	private Integer beginRow, endRow;
	
	private List<String> memberLevelList;
	private List<String> orderList;
}
