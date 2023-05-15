package com.human.service;

import com.human.domain.KakaoPayApproveVO;

public interface MypageService {

	// 잔여 포인트 조회
	public int pointRead(String userId) throws Exception;
	
	// 카카오페이 승인 절차
	// public KakaoPayApproveVO kakaoPayApprove(String pgToken) throws Exception;
	
}
