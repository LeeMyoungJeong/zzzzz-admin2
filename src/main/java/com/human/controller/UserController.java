package com.human.controller;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.human.domain.KakaoProfile;
import com.human.domain.OAuthToken;
import com.human.domain.UserAuth;
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

	// 카카오 로그인 ㄱㄱ
	@GetMapping("/auth/kakaoCallback")
	//@ResponseBody
	public String kakaoCallback(HttpServletRequest request, String code) throws Exception { // data를 리턴해주는 컨트롤러 함수
		
		log.info("카카오 로그인 해보자");
		/*
		 * 전달해야 할 정보들
		grant_type=authorization_code
		client_id=6799cc9d0bc8986a498c73b770a27f09
		redirect_uri=http://localhost:8080/auth/kakaoCallback
		code={동적인 코드, 응답받은것}
		
		*/

		// POST 방식으로 key=value 데이터를 요청 (카카오쪽으로)
		RestTemplate rt = new RestTemplate();
		
		// HttpHeader 오브젝트 생성
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
			// 헤더에 Content-type을 담는다는 의미는 
			// 내가 지금 전송할 http바디 데이터가 키-밸류 형태의 데이터임을 알려주는 것임
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("grant_type", "authorization_code");
		params.add("client_id", "6799cc9d0bc8986a498c73b770a27f09");
		params.add("redirect_uri", "http://localhost:8080/auth/kakaoCallback");
		params.add("code", code);
		
		// HttpHeader와 HttpBody를 하나의 오브젝트에 담기
		HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = 
				new HttpEntity<>(params, headers);
		
		// Http 요청하기 - Post방식으로 - 그리고 response 변수의 응답 받음.
		ResponseEntity<String>response = rt.exchange(
				"https://kauth.kakao.com/oauth/token",
				HttpMethod.POST,
				kakaoTokenRequest,
				String.class
		);
		

		
		// Gson, Json Simple, ObjectMapper 라이브러리 사용 가능
		ObjectMapper objectMapper = new ObjectMapper();
		OAuthToken oauthToken = null;
		try {
			oauthToken = objectMapper.readValue(response.getBody(), OAuthToken.class);
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		
		System.out.println("카카오 엑세스 토큰 : " + oauthToken.getAccess_token());

		
		
		
		
		
		
		
		
		
		//Step2
		
		RestTemplate rt2 = new RestTemplate();
		
		// HttpHeader 오브젝트 생성
		HttpHeaders headers2 = new HttpHeaders();
		headers2.add("Authorization", "Bearer "+ oauthToken.getAccess_token());
		headers2.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
			// 헤더에 Content-type을 담는다는 의미는 
			// 내가 지금 전송할 http바디 데이터가 키-밸류 형태의 데이터임을 알려주는 것임
		
		// HttpHeader와 HttpBody를 하나의 오브젝트에 담기
		HttpEntity<MultiValueMap<String, String>> kakaoProfileRequest2 = 
				new HttpEntity<>(headers2);
		
		// Http 요청하기 - Post방식으로 - 그리고 response 변수의 응답 받음.
		ResponseEntity<String> response2 = rt2.exchange(
				"https://kapi.kakao.com/v2/user/me",
				HttpMethod.POST,
				kakaoProfileRequest2,
				String.class
		);
		 
		// 카카오 엑세스 토큰 : DYv1jdM_0xpzh559RRV1Y-WrYgMdN-PgQNuMZ4uzCj11GQAAAYgjqIB1 --> 잘 받아옴
		System.out.println(response2.getBody());
		
		ObjectMapper objectMapper2 = new ObjectMapper();
		KakaoProfile kakaoProfile = null;
		try {
			kakaoProfile = objectMapper2.readValue(response2.getBody(), KakaoProfile.class);
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		
		Users kakaoProfileUser = new Users();
		kakaoProfileUser.setEmail(kakaoProfile.kakao_account.email);
		kakaoProfileUser.setName(kakaoProfile.properties.nickname);
		kakaoProfileUser.setUserId(kakaoProfile.kakao_account.email);
		kakaoProfileUser.setName(kakaoProfile.properties.nickname);
		kakaoProfileUser.setEnabled(1);
	    
		Users user = userService.findByUser(kakaoProfileUser);
		if(user == null) {
			int result = userService.join(kakaoProfileUser);
			user = userService.findByUser(kakaoProfileUser);
		}
		
		if("이지용".equals(user.getName())) {
			
		}else {
			
		}
		
		HttpSession session = request.getSession();
//		UserAuth userAuth = userAuthService.findUserAuthByUser(user);
		
		session.setAttribute("auth", "USER");
		session.setAttribute("user", user);
		
		System.out.println("카카오 아이디(번호) : " + kakaoProfile.getId());
		System.out.println("카카오 이메일 : " + kakaoProfile.getKakao_account().getEmail());

		String userId = user.getUserId();
		String userPw = "kakao123456";
		
		log.info("userId : " + userId);
		log.info("userPw : " + userPw);
		
		//UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userId, userPw);
        //Authentication authentication = authenticationManager.authenticate(authenticationToken);
        //SecurityContextHolder.getContext().setAuthentication(authentication);
		
		//return response2.getBody();
		return "/index";
	}
	


}
