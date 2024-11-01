package com.game.vo;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class CommunityEditRequestVO {
	private int communityNo;
	private String communityCategory;
	private String communityTitle;
	private String communityContent;
	private List<MultipartFile> attachList;//첨부 파일 정보 리스트
	private List<Integer> originList;// 기존 이미지의 첨부파일 번호
}
