package com.human.service;

import java.util.List;

import com.human.domain.Files;

public interface FileService {

	// 파일 등록
	// FileController에서는 insert()라는 메소드가 없다.
	// BoardController에 있다.
	// 그럼 여기서의 insert는 BoardController에서의 insert메소드인지 알아보도록 하자.
	public int insert(Files file) throws Exception;
	
	// 파일 목록
	public List<Files> listByParentNo (Files file) throws Exception;
	
	// 파일 조회 ( read() 메소드. 다운로드받을때 필요하다 )
	public Files read(int fileNo) throws Exception;
	
	// 파일 삭제
	public int delete(int fileNo) throws Exception;
	
}
