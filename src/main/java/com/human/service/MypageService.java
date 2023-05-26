package com.human.service;

import java.util.List;

import com.human.domain.KakaoPayApproveVO;
import com.human.domain.Players;
import com.human.domain.TotalRevenues;
import com.human.domain.Users;

public interface MypageService {

	// 잔여 포인트 조회
	public int pointRead(String userId) throws Exception;
	
	// 카카오페이 승인 절차
	// public KakaoPayApproveVO kakaoPayApprove(String pgToken) throws Exception;
	
	// 내 정보 수정
	public int update(Users user) throws Exception;
	
	// 매출 통계를 위해 Players 정보 리스트 형태로 가져오기
	public List<TotalRevenues> getListPlayersDataBetween (String sdttm, String edttm)throws Exception;
}
