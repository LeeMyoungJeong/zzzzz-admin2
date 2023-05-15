package com.human.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Service;

import com.human.domain.UserAuth;
import com.human.domain.Users;
import com.human.mapper.UserMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

	
	///////////////////////////////////////////////////////////////
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private UserMapper userMapper;
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	///////////////////////////////////////////////////////////////
	
	@Override
	public int join(Users user) throws Exception {
		
		// Users 객체로부터 받아오는 userPw 암호화
		String plainPw = user.getUserPw();
		String encodedPw = passwordEncoder.encode(plainPw);
		user.setUserPw(encodedPw);

		// 회원 등록
		int result = userMapper.join(user);
		log.info("매퍼 - user : " + user);
		String userId = user.getUserId();
		
		// 권한 등록
		if (result > 0) {
			UserAuth userAuth = new UserAuth();
			userAuth.setUserId ( userId );
			userAuth.setAuth("ROLE_USER");
			userMapper.insertAuth(userAuth);
		}
		
		return result;
	}

	
	// 인증된 사용자를 회원가입 후 바로 로그인시켜줄 때 필요한 기능인가....
	// 세션 유지??
	@Override
	public HttpSession tokenAuthentication(Users user, HttpServletRequest request) throws Exception {
	
		String username = user.getUserId();
		String password = user.getUserPwChk();  
		
		log.info("username : " + username);
		log.info("password : " + password);
		
		// 이거의 쓰임새를 모르겠다.
		HttpSession session = request.getSession();
		
		// 아이디, 패스워드로 인증토큰 생성
		// 이 객체는 아이디와 비밀번호를 username, password라는 이름으로 사용한다.
		UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, password); 
		
		// 토큰에 요청정보를 등록. request로부터 온 요청임을 토큰에 알려준다.
		token.setDetails( new WebAuthenticationDetails(request));
		
		// 이 토큰을 가지고 인증. 이게 제일 중요하다. 위에서 받아온 토큰을 가지고 이제 인증처리를 한다.
		Authentication authentication = authenticationManager.authenticate(token);
		
		User authUser = (User) authentication.getPrincipal();
		log.info("인증된 사용자 아이디 : " + authUser.getUsername());
		
		SecurityContextHolder.getContext().setAuthentication(authentication);
		
		return session;
	}




	


}
