package com.human.mapper;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MypageMapper {

	// 회원 포인트 조회
	public int pointRead(String userId) throws Exception;
	
	
}
