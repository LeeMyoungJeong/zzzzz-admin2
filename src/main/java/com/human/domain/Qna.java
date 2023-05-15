package com.human.domain;

import java.sql.Date;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class Qna {

	// 질의응답 객체 정의
	// 질의응답 1개에 대한 정의
	
	private int qnaNo;
	private String title;
	private String writer;
	private String content;
	private Date regDate;
	
	// 첨부파일
	private MultipartFile[] files;
	
}
