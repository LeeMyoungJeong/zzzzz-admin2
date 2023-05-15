package com.human.domain;

import lombok.Data;

@Data
public class UserAuth {

	private int authNo;			// 권한번호 ( 단순 시퀀스 번호 )
	private String userId;		// 회원 아이디 ( 회원 가입 시 지정 )
	private String auth;		// 권한 ( ROLE_USER, ROLE_ADMIN )
								// --> auth를 지정하는 클래스가 serviceImpl 단이었나?? 
	
}
