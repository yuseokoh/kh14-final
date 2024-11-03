package com.game.vo;


import java.util.List;

import com.game.dto.CommunityDto;
import com.game.dto.CommunityImageDto;

import lombok.Data;

@Data
public class CommunityComplexResponseVO {
	private boolean isLast; //다음항목이 존재하는가?
	private int count; //갯수몇개인가
//	private int beginRow, endRow;
	private List<CommunityDto> communityList; // 검색 결과
//	private CommunityDto communityDto; // 특정 게시글 정보 --첫번쨰로 DB에들어갓던코드
//    private List<CommunityImageDto> images; // 이미지 목록 추가
}
