package com.human.domain;

import java.sql.Date;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class Notice {

	// 공지사항 객체 정의
	// 공지사항 1개에 대한 정의
	
	private int noticeNo;
	private String title;
	private String writer;
	private String content;
	private Date regDate;
	
	// 첨부파일
	private MultipartFile[] files;
	
}
