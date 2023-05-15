package com.human.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CustomLoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler{
	
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request
									  , HttpServletResponse response
									  , Authentication authentication) throws ServletException, IOException {
	
	
		
	// 로그인 이후의 처리를 위한 클래스이다.
	log.info("인증처리 성공");
    
	log.info("리스폰스 : " + response.toString());
	
	// ㅅㅂ그마 여기 설정 안되어있어서 생성된 쿠키가 삭제가 안되고 계속 ㅊ나오고 있었노...
	String rememberId = request.getParameter("remember-id"); 	// 아이디저장 체크 안하면 null값이 대입됨
	String username = request.getParameter("username"); 		// 아이디 값 저장
	log.info("rememberId : " + rememberId);
	log.info("username : " + username);
	
	// 아이디 저장 체크 시 -> 쿠키 생성
	// 쿠키를 생성하는 프로세스 자체는 CustomLoginHandler에서 처리한다.
	if( rememberId != null && rememberId.equals("on") ) {   	// rememberId가 null 이 아닌 동시에 값이 on 이라면,
		Cookie cookie = new Cookie("remember-id", username); 	// 쿠키 이름이 remember-id인 쿠키에 value값을 username 으로 지정한다
		log.info("remember-id : " + cookie.getValue());
		cookie.setMaxAge(60*60*24*10);  // 쿠키가 살아있는 시간은 10일준다
		cookie.setPath("/");
		response.addCookie(cookie);
	}
	else {  // 체크가 되어있지 않다면,
		Cookie cookie = new Cookie ("remember-id", null);	// remember-id라는 이름의 쿠키에 value값을 null로 지정해준다
		cookie.setMaxAge(0);
		cookie.setPath("/");
		response.addCookie(cookie);		// 그리고 응답 객체에 쿠키를 더해준다.
	}
	
	// 인증된 사용자의 정보를 가져와준다 ( 아이디 / 패스워드 / 권한)
	// 스프링 프레임워크 -> 유저디테일에 있는 유저를 가져옴
	User user = (User) authentication.getPrincipal();
	log.info("아이디 : " + user.getUsername());
	log.info("패스워드 : " + user.getPassword()); 	// 보안 상 노출 X
	log.info("권한 : " + user.getAuthorities());	
	
	HttpSession session = request.getSession();
	session.setAttribute("auth", user.getAuthorities());
	// 세션에 auth라고 권한 저장 完
	log.info("auth : " + session.getAttribute("auth"));	
	
	// 로그인한 사용자의 권한 확인
	// String auth = (String) session.getAttribute("auth");
	// log.info("auth : " + auth);
	
	super.onAuthenticationSuccess(request, response, authentication);
	}

	
	
}
