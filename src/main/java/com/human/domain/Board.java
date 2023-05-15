package com.human.domain;

import java.sql.Date;
import java.time.LocalDateTime;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class Board {

	// 게시판 객체 정의 
	// ↓ 게시글 1개에 대한 정의
	private int boardNo;
	private String title;
	private String writer;
	private String content;
	private Date regDate;  // 여기가 LocalDateTime이면 html 불러올 때 parsing이 되지 않는다
	private Date updDate;
	
	// 첨부파일
	private MultipartFile[] files;
}
