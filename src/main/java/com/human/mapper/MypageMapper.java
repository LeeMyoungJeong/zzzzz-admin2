package com.human.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.human.domain.Users;

@Mapper
public interface MypageMapper {

	// 회원 포인트 조회
	public int pointRead(String userId) throws Exception;
	
	// 회원 정보 수정
	public int update(Users user) throws Exception;
	
}
