package com.game.vo;

import java.util.List;

import com.game.dto.MemberDto;

import lombok.Data;

@Data
public class MemberComplexResponseVO {
	private boolean last;
	private int count;
	private List<MemberDto> memberList;
}
