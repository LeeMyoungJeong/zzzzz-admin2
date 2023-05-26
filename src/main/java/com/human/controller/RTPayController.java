package com.human.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("checkpay")
@Slf4j
public class RTPayController {
		
	// 결제 성공 페이지 호출
    @PostMapping
    public String checkPay(HttpServletRequest request) {
    	request.getQueryString();
    	return "checkpay";
    }
    
 // 결제 성공 페이지 호출
    @GetMapping
    public String getCheckPay(HttpServletRequest request) {
    	request.getQueryString();
    	return "checkpay";
    }
    
    
    
    
    
    
}
