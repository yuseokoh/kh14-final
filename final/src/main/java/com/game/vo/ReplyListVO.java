package com.game.vo;

import java.util.List;

import com.game.dto.ReplyDto;

import lombok.Data;

@Data
public class ReplyListVO {
	private List<ReplyDto>list;
	private int totalPage;
	private int currentPage;
}
