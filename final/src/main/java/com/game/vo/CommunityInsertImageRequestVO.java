package com.game.vo;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class CommunityInsertImageRequestVO {
	private int communityNo;
	private String communityWriter;
	private String communityCategory;
	private String communityTitle;
	private String communityContent;
	private List<MultipartFile> attachList;
}
