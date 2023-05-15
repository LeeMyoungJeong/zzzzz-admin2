package com.human.service;

import java.util.List;

import com.human.domain.Page;
import com.human.domain.Qna;

public interface QnaService {

	// 질의응답 목록
	public List<Qna> list() throws Exception;
	
	// 질의응답 등록
	public int insert(Qna qna) throws Exception;
	
	// 질의응답 조회
	public Qna read(int qnaNo) throws Exception;
	
	// 질의응답 수정
	public int update(Qna qna) throws Exception;
	
	// 질의응답 삭제
	public int delete(int qnaNo) throws Exception;
	
	// 페이징 처리
	public List<Qna> list(Page page) throws Exception;
}
