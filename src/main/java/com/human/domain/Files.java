package com.human.domain;

import java.time.LocalDateTime;

import lombok.Data;


/**
 * 첨부파일 정의
 * @author 휴먼교육센터
 *
 */
@Data
public class Files {

	private int fileNo;
	private String parentTable;
	private int parentNo;
	private String fileName;
	private String filePath;
	private LocalDateTime regDate;
	private LocalDateTime updDate;
	
}
