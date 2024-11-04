package com.game.dto;

import java.sql.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class SystemRequirementDto {
	    private int requirementId;
	    private int gameNo;
	    private String requirementType; // 'minimum' 또는 'recommended'
	    private String os;
	    private String processor;
	    private String memory;
	    private String graphics;
	    private String directxVersion;
	    private String storage;
	    private String soundCard;
	// getters and setters
}
