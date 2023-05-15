package com.human.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.human.domain.Ground;
import com.human.mapper.GroundMapper;

@Service
public class GroundServiceImpl implements GroundService{
	
	//////////////////////////////////////////////////////////////////
	@Autowired
	private GroundMapper groundMapper;
	
	//////////////////////////////////////////////////////////////////

	// 제휴구장 목록 조회
	@Override
	public List<Ground> groundList() throws Exception {

		return groundMapper.groundList();
	}

	
	// 제휴구장 등록
	@Override
	public int register(Ground ground) throws Exception {
		return groundMapper.register(ground);
	}
	

}
