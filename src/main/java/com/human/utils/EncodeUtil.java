package com.human.utils;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration		// 스프링 설정 클래스로 빈에 등록
public class EncodeUtil {
	
	@Bean 	
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

}
