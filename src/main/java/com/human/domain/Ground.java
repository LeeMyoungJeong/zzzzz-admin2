package com.human.domain;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class Ground {

	// 제휴구장 1개 객체에 대한 정의
	private int groundNo;  			// 구장 번호
	private String groundName;  	// 구장 이름
	private String groundAddress;	// 구장 주소
	private String maxPlayer; 		// 모집 정원
	private String firstGp;			// 운동장 사진 1
	private String secondGp;			// 운동장 사진 1
	private String thirdGp;			// 운동장 사진 1
	
	
	// 운동장 사진 3장
	private MultipartFile[] files;
	
	
}
