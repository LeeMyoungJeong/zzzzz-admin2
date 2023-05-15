package com.human.service;

import java.util.List;

import com.human.domain.Ground;

public interface GroundService {

	// 제휴구장 목록 조회
	public List <Ground> groundList() throws Exception;
	
	// 제휴구장 등록
	public int register (Ground ground) throws Exception;
	
}
