package com.game.vo;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GamePurchaseRequestVO {
	private List<GameQtyVO> gameList;
	private String approvalUrl;
	private String cancelUrl;
	private String failUrl;
}
