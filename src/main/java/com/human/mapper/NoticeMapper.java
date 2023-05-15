package com.human.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.human.domain.Board;
import com.human.domain.Notice;
import com.human.domain.Page;

@Mapper
public interface NoticeMapper {

	
	// 공지사항 입력
	public int insert(Notice notice) throws Exception;
	
	// 공지사항 갯수 조회
	public int count() throws Exception;
	
	// [페이지] 내부의 게시글 목록 (한 페이지 당 10개씩)
	public List<Notice> listWithPage(Page page) throws Exception;
	
	// 공지사항 1개 조회 
	public Notice read(int noticeNo) throws Exception;
	
	// 게시글 수정
	public int update(Notice notice) throws Exception;
	
	// 게시글 삭제
	public int delete(int noticeNo) throws Exception;
	
	
}
