package com.game.vo;

import java.util.List;

import com.game.dto.CommunityDto;

import lombok.Data;

@Data
public class CommunityListResponseVO {
	private List<CommunityDto> communityList; //검색결과
	private boolean isLast;
	private int count;
}
