package com.game.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.game.interceptor.MemberInterceptor;

@Configuration
public class InterceptorConfiguration implements WebMvcConfigurer{
	
	@Autowired
	private MemberInterceptor memberInterceptor;
	
//	@Override
//	public void addInterceptors(InterceptorRegistry registry) {
//		registry.addInterceptor(memberInterceptor)
//			.addPathPatterns(memberInterceptor)
//		
//			);
//	}
	
}
