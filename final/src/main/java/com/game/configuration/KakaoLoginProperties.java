package com.game.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
@ConfigurationProperties(prefix = "kakao.login")
public class KakaoLoginProperties {
    private String clientId;
    private String redirectUri;
    private String tokenUrl;
    private String userInfoUrl;
}
