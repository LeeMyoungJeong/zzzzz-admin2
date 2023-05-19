package com.human.service;

import java.util.List;

import com.human.domain.GroundPic;
import com.human.domain.Match;
import com.human.domain.Page;
import com.human.domain.Players;
import com.human.domain.TotalPlayer;

public interface MatchService {

	
	// 매치 조회
	public List<Match> list() throws Exception;
	
	// 매치 등록
	public int insert(Match match) throws Exception;
	
	// 매치 조회
	public Match read(int matchNo) throws Exception;
	
	// 매치 조회 시 carousel에 들어갈 사진 조회 
	public GroundPic readPic(String groundPic) throws Exception;
	
	// 매치 삭제 처리
	public int delete(int matchNo) throws Exception;
	
	// 매치에 참여 인원 명단 등록
	public int insertPlayer(String userId, int matchNo) throws Exception;
	
	// 매치 총인원 산출하기
	public int countTotalPlayer(int matchNo) throws Exception;
	
	// 매치 참여자 리스트
	public List<Players> playerList(int matchNo) throws Exception;
	
	// 매치 신청 후 포인트 차감
	public int pay(String userId, int matchNo) throws Exception;
	
	// 내가 신청한 매치만 가져오기
	public List<Match> myMatchList(String userId) throws Exception; 
	
	// 매치 취소하기
	public int cancelPlayer(String userId, int matchNo) throws Exception;
	
	// 완료된 매치 조회 (정산하기)
	public List<Match> endMatchList() throws Exception;
	
	// 완료된 매치 정산하기
	public int pointRefund(int matchNo) throws Exception;
	
}
