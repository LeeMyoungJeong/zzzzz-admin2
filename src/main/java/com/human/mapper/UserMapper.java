package com.human.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.human.domain.UserAuth;
import com.human.domain.Users;

@Mapper
public interface UserMapper {

	// 회원 등록
	public int join(Users user) throws Exception;
	
	// 권한 등록
	public int insertAuth(UserAuth userAuth) throws Exception;
	
	// 아이디 중복 확인
    public int checkUserId(String userId) throws Exception;

    // 카카오로 유저찾기
	public Users findByUser(Users user);

	// 마이페이지에서 내 정보 꺼내오기
	public Users findFromMyinfo(String userId) throws Exception;
	
}
