package com.game.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class SpringdocConfiguration {

	@Bean
	public OpenAPI apiInfo() {
		Info info = new Info()
				.version("v0.0.1")
				.title("3조 파이널 REST API")
				.description("React에서 이용하기 위해 만든 백엔드 문서입니다");
		
		String jwtName = "Authorization";
		SecurityRequirement requirement = new SecurityRequirement();
		requirement.addList(jwtName);
		
		Components components = new Components()
				.addSecuritySchemes(
						jwtName, 
						new SecurityScheme()
							.name(jwtName)
							.type(SecurityScheme.Type.HTTP)
							.scheme("bearer")
				);
		
		return new OpenAPI().info(info)
				.addSecurityItem(requirement)
				.components(components);
	}
	
}