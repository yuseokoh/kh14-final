package com.game.vo;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.game.advice.JsonEmptyIntegerToNullDeserializer;
import com.game.advice.JsonEmptyStringToNullDeserializer;

import lombok.Data;

@Data
public class CommunityComplexRequestVO {
	
	
	
	//명호형
	@JsonDeserialize(using = JsonEmptyStringToNullDeserializer.class)
	private String column, keyword;	
	@JsonDeserialize(using = JsonEmptyIntegerToNullDeserializer.class)
	private Integer beginRow, endRow;
}
