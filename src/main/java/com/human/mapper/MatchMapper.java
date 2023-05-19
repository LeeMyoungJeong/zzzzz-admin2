package com.human.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.human.domain.GroundPic;
import com.human.domain.Match;
import com.human.domain.Players;
import com.human.domain.TotalPlayer;

@Mapper
public interface MatchMapper {

	// 매치 리스트 가져오기
	public List<Match> list() throws Exception;
	
	// 매치 입력
	public int insert(Match match) throws Exception;
	
	// 매치 조회
	public Match read(int matchNo) throws Exception;
	
	// carousel에 들어갈 사진 목록 조회
	public GroundPic readPic(String groundName) throws Exception;
	
	// 매치 삭제
	public int delete(int matchNo) throws Exception;
	
	// 포인트 잔액 조회
	public int matchCheck(String userId) throws Exception;
	
	// 매치 희망 인원을 명단에 등록
	public int insertPlayer(Players player) throws Exception;
	
	// 매치 총원을 구해서 띄운다.
	public int countTotalPlayer(int matchNo) throws Exception;
	
	// 매치 참여자 리스트를 구해온다
	public List<Players> playerList(int matchNo) throws Exception;
	
	// 매치 참여자 포인트 차감하기
	public int pay(Players player) throws Exception;
	
	// 매치 참여 현재인원 최신화
	public int playerUpdate() throws Exception;
	
	// 내가 신청한 매치만 마이페이지에 보여주기
	public List<Match> myMatchList(String userId) throws Exception;
	
//	public int cancelPlayer(String userId, int matchNo) throws Exception;

	// 플레이어가 매치 취소할 때 실행할 메소드
	public int cancelPlayer(Map<String, Object> paramMap);
	
	// 완료된 매치 리스트 가져오기
	public List<Match> endMatchList() throws Exception;
	
	// 완료된 매치 참여자 포인트 환급하기
	public int pointRefund(int matchNo) throws Exception;
	
	// 환급 완료 처리하기
	public int refundCheck(int matchNo) throws Exception;
	
	// 환급 완료한 매치에서 환급여부 조회하기
	public int refundYn(int matchNo) throws Exception;
}
