package com.game.vo;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.game.advice.JsonEmptyIntegerToNullDeserializer;
import com.game.advice.JsonEmptyStringToNullDeserializer;

public class CommunityListRequestVO {
	@JsonDeserialize(using = JsonEmptyStringToNullDeserializer.class)
	private String column, keyword;	
	@JsonDeserialize(using = JsonEmptyIntegerToNullDeserializer.class)
	private Integer beginRow, endRow;
}
