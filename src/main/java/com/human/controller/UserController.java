package com.human.controller;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.human.domain.Users;
import com.human.mapper.UserMapper;
import com.human.service.UserService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class UserController {

	////////////////////////////////////////////////////////

	@Autowired
	private UserService userService;
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
	private UserMapper userMapper;
	
	////////////////////////////////////////////////////////
	
	
	
	// 메인화면 - Get
	@GetMapping("/")
	public String main() {
		log.info("홈화면요청ㄱㄱ");
		return "/index";
	}
	
	// 로그인 페이지 - Get
	@GetMapping("/auth/login")
	public String loginForm(Model model, @CookieValue(value="remember-id", required = false) Cookie cookie) {
		
		log.info("로그인 요청 ㄱㄱ");

		String userId = ""; 			// userId라는 이름의 String타입 변수가 필요함. 초기값은 공백
		boolean rememberId = false; 	// rememberId 라는 이름의 boolean 변수도 필요함. 초기값은 false;		
		
		if ( cookie != null ) {
			
			log.info("CookieName :  " + cookie.getName() );       // 쿠키 변수는 코멘트, 도메인, 맥스에이지, 네임, 패스, 밸류, 버전, 클래스, 시큐어 등의 필드값을 가질 수 있음
			log.info("CookieValue :  " + cookie.getValue() );
			log.info("CookieComment :  " + cookie.getComment() );
			log.info("CookieDomain :  " + cookie.getDomain() );
			log.info("CookieMaxAge :  " + cookie.getMaxAge() );
			log.info("CookiePath :  " + cookie.getPath() );
			log.info("CookieVersion :  " + cookie.getVersion() );
			log.info("CookieClass :  " + cookie.getClass() );
			log.info("CookieSecure :  " + cookie.getSecure() );   // 보는김에 고마 다 찍아쁘자
		
			userId = cookie.getValue();
			rememberId = true;
			
			log.info("userId : " + userId);
		
		}		
		
		model.addAttribute("userId", userId);
		model.addAttribute("rememberId", rememberId);
		
		return "/auth/login";
	}

	
	// 아이디 중복 확인
	@GetMapping("/checkUserId") 
		// join.html 에서 자바스크립트 코드를 사용하여 XMLHttpRequest객체로 /checkUserId라는 url에 userId라는 파라미터를
		// 전달해야 아이디 중복 확인 작업을 할 수 있다.
		// 그래서 컨트롤러 단에서 /checkUserId라는 url을 GetMapping 해주어야 한다.
	@ResponseBody
	public String checkUserId(@RequestParam("userId") String userId) throws Exception {
		log.info(userId + "의 중복확인");
		// @RequestParam은 스프링 MVC 에서 HTTP 요청 파라미터를 받아오는 방법 중 하나이다.
		// 위의 코드를 통해 userId라는 이름의 요청 파라미터를 받아온다.
	    int result = userMapper.checkUserId(userId);
	    // 서비스를 거치지 않고 곧바로 Mapper를 요청했어... 그게 되나??
	    if (result == 1) {
	        return "fail";
	    } else {
	        return "success";
	    }
	}
	


	// 회원가입 페이지 - Get
	@GetMapping("/auth/join")
	public String joinForm() {
		log.info("회원가입 화면 요청 ㄱㄱ");
		return "/auth/join";
	}
	
	// 회원가입 처리 - Post
	@PostMapping("/auth/join") //  "/" 표시 잘 확인해라...
	public String join(Users user, RedirectAttributes rttr, HttpServletRequest request) throws Exception {
		
		log.info("회원가입 처리 : " + user);

		// 회원가입 처리
		int result = userService.join(user);
		
		// 회원가입 성공 후 바로 로그인 처리에 필요한 로직 ↓
		if (result > 0) {
			userService.tokenAuthentication(user, request);
		}
		
		rttr.addFlashAttribute("msg", user.getName());
		
		
		
		
		return "redirect:/";
	}



}
