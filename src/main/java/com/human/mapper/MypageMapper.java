package com.human.mapper;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.human.domain.Players;
import com.human.domain.TotalRevenues;
import com.human.domain.Users;

@Mapper
public interface MypageMapper {

	// 회원 포인트 조회
	public int pointRead(String userId) throws Exception;
	
	// 회원 정보 수정
	public int update(Users user) throws Exception;
	
	// 매출 정보 가져오기
	public List<TotalRevenues> getListPlayersDataBetween(Date sdttm1, Date edttm1) throws Exception;
	
}
