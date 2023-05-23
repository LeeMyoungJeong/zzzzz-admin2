package com.human.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.boot.autoconfigure.security.SecurityProperties.User;

import com.human.domain.Users;

public interface UserService {

	// 회원 등록 (join)
		public int join(Users user) throws Exception;
	
	// 토큰 인증
		public HttpSession tokenAuthentication(Users user, HttpServletRequest request) throws Exception;

	// 카카오 로그인 시 유저 찾기
		public Users findByUser(Users user) throws Exception;

	// 카카오 로그인 시 기존 회원 등록 안되어있을 때 회원 등록 처리
		public int nonmemberJoin(Users kakaoProfileUser) throws Exception;
		
}
