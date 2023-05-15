package com.human.domain;

import java.sql.Date;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class Match {

	private int matchNo;
	private Date playDate;
	private String playTime;
	private String groundName;
	private int maxPlayerNo;
	private int players;
	private int fee;
	private String leader;
	private String groundPic; 
	
	// 첨부파일
	private MultipartFile[] files;
	
}
