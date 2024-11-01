package com.game.vo;

import java.util.List;

import com.game.dto.CommunityDto;

import lombok.Data;

@Data
public class CommunityDetailResponseVO {
	private CommunityDto communityDto;
	private List<Integer> images;
}
