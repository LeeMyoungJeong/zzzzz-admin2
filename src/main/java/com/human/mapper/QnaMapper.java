package com.human.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.human.domain.Page;
import com.human.domain.Qna;

@Mapper
public interface QnaMapper {

	// 질의응답 등록
	public int insert(Qna qna) throws Exception;
	
	// 질의응답 조회
	public Qna read(int qnaNo) throws Exception;
	
	// 질의응답 수정
	public int update(Qna qna) throws Exception;

	// 질의응답 삭제
	public int delete(@Param("qnaNo") int qnaNo) throws Exception;
	
	// 질의응답 개수 조회
	public int count() throws Exception;
	
	// [페이지] 게시글 목록
	public List<Qna> listWithPage(Page page) throws Exception;
	
}
