package com.human.service;

import java.util.List;

import com.human.domain.Notice;
import com.human.domain.Page;

public interface NoticeService {

	// 공지사항 목록 조회 (페이징 처리)
	public List <Notice> list(Page page) throws Exception; 
	
	// 공지사항 등록
	public int insert(Notice notice) throws Exception;
	
	// 공지사항 읽기
	public Notice read(int noticeNo) throws Exception;
	
	// 공지사항 수정 화면 get
	public Notice updateForm(int noticeNo) throws Exception;
	
	// 공지사항 수정 
	public int update(Notice notice) throws Exception;
	
	// 공지사항 삭제
	public int delete(int noticeNo) throws Exception; 
}
