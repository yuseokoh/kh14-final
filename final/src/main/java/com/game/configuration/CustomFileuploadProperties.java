package com.game.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

import lombok.Data;

@Data
@Service
@ConfigurationProperties(prefix = "custom.fileupload")
public class CustomFileuploadProperties {
	private String path;
}
