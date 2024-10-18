package com.game.vo;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GameApproveRequestVO {
	private String partnerOrderId;
	private String tid;
	private String pgToken;
	private List<GameQtyVO> gameList;
}
