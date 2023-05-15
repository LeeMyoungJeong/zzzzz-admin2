package com.human.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.human.domain.Ground;

@Mapper
public interface GroundMapper {

	// 운동장 목록
	public List<Ground> groundList() throws Exception;
	
	// 제휴 구장 등록
	public int register (Ground ground) throws Exception;


}	
