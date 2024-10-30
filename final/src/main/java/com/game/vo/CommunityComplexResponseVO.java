package com.game.vo;


import java.util.List;

import com.game.dto.CommunityDto;

import lombok.Data;

@Data
public class CommunityComplexResponseVO {
	private boolean isLast; //다음항목이 존재하는가?
	private int count; //갯수몇개인가
//	private int beginRow, endRow;
	private List<CommunityDto> communityList; // 검색 결과
}
