package com.human.service;

import java.util.List;

import com.human.domain.Board;
import com.human.domain.Page;

public interface BoardService {

	// 게시글 목록 조회
	public List <Board> list() throws Exception; 
 	
	// 게시글 등록
	public int insert(Board board) throws Exception;
	
	// 게시글 1개 조회
	public Board read(int boardNo) throws Exception;
	
	// 게시글 수정 처리
	public int update(Board board) throws Exception;
	
	// 게시글 삭제 처리
	public int delete(int boardNo) throws Exception; 
	
	// 페이징 처리
	public List<Board> list(Page page) throws Exception;
}
