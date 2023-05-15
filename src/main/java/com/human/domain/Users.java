package com.human.domain;

import java.time.LocalDateTime;

import lombok.Data;


// 나중에 주소, 성별도 입력 받아야할거같은데, 일단 지금은 배웠던 내용들로만 구성해보자.
// 주소, 성별에 null값이 입력되면 어떤 예외가 발생할지 모르겠노...
@Data 
public class Users { // 관리자도 Users로 통일하자. 관리자는 회원번호와 권한을 DB에서 세팅하여 차이를 주면 된다.
					 // 어찌보면 관리자도 user이다. 지금은 일단 코드의 통일성을 위해 User로 단일화 시켜 놓으면 좋을것같다.
	
	private int userNo;				// 관리자번호  (관리자도 번호가 있어야 접근권한을 분리할 수 있다)
	private String userId;			// 관리자아이디 (관리자도 아이디 있어야 로그인을 할거아이가)
	private String userPw; 			// 비밀번호
	private String userPwChk;		// 비밀번호 확인
	private String name;			// 이름
	private String email; 			// 이메일
	private int enabled;			// 활성화여부(휴먼여부) --> 0 : 휴면, 1 : 비휴면
	private LocalDateTime regDate; 	// 등록일자
	private String phone;
	private int point;				// 회원 포인트
 // private LocalDateTime updDate;	// 수정일자 --> 수정일자 굳이 지금 당장 필요 없을거같다.
	
	
}
