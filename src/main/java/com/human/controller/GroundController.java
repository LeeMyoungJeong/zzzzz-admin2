package com.human.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.human.domain.Board;
import com.human.domain.Ground;
import com.human.service.GroundService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class GroundController {

	// 필요 기능 정의
	/**
	 *  1. 제휴 구장 list - 화면 Get
	 *  2. 제휴 구장 add - 화면 Get
	 *  3. 제휴 구장 add - 처리 Post
	 *  4. 제휴 구장 수정 - 화면 Get
	 *  5. 제휴 구장 수정 - 처리 Post
	 *  
	 *  -- 제휴 구장 삭제는 굳이 필요 없을듯...
	 * 
	 */
	
	
	//////////////////////////////////////////////////
	
	@Autowired
	private GroundService groundService;
	
	//////////////////////////////////////////////////

	
	// 제휴 구장 목록 - 화면 Get
	@GetMapping("/ground/groundList")
	public String groundList(Model model, Ground ground) throws Exception {
		
		List <Ground> groundList = groundService.groundList();
		
		log.info("groundList : " + groundList);
		model.addAttribute("groundList", groundList);
		
		return "/ground/groundList";
	}
	
	// 제휴 구장 등록하기 - 화면 Get
	@GetMapping("/ground/register")
	public String registerForm(Model model, Principal principal) throws Exception {
		String userId = principal.getName();
		log.info("제휴구장 등록 화면 요청");
		log.info("username(사용자 아이디) : " + userId);
		
		model.addAttribute("userId", userId);
		
		return "/ground/register";
	}
	
	// 제휴 구장 등록하기 - 처리 Post
	@PostMapping("/ground/register")
	public String register(Ground ground) throws Exception {
		
		int result = groundService.register(ground);
		
		return "redirect:/ground/groundList";
		
	}
	
	
}
