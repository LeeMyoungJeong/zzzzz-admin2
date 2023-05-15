package com.human.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class LoginController {

    @PostMapping("/auth/login")
    public String login() {
        // 로그인 성공 로직 처리
    	log.info("로그인화면에서 post하나??");
        return "redirect:/home";
    }
    
    @GetMapping("/home")
    public String home() {
        return "/index";
    }
	
}
