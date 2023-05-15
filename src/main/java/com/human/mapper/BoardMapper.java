package com.human.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.human.domain.Board;
import com.human.domain.Page;

@Mapper
public interface BoardMapper {

	// 게시글 목록
	public List<Board> list() throws Exception;
	
	// 게시글 입력
	public int insert(Board board) throws Exception;

	// 게시글 1개 읽기 
	public Board read(int boardNo) throws Exception;
	
	// 게시글 수정
	public int update(Board board) throws Exception;

	// 게시글 삭제
	public int delete(int boardNo) throws Exception;
	
	// 게시글 갯수 조회
	public int count() throws Exception;
	
	// [페이지] 내부의 게시글 목록 (한 페이지 당 10개씩)
	public List<Board> listWithPage(Page page) throws Exception;
	
	
}
