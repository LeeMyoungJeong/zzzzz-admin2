package com.human.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.human.domain.GroundPic;
import com.human.domain.Match;
import com.human.domain.Players;
import com.human.mapper.FileMapper;
import com.human.mapper.MatchMapper;
import com.human.utils.FileUtil;

import lombok.extern.slf4j.Slf4j;
@Slf4j
@Service
public class MatchServiceImpl implements MatchService{

	////////////////////////////////////////////////////////////////////////
	@Autowired
	private MatchMapper matchMapper;
	
	@Autowired
	private FileMapper fileMapper;	
	
	@Autowired
	private FileUtil fileUtil;

	// 업로드 경로
	@Value("${upload.path}")
	private String uploadPath;
	
	////////////////////////////////////////////////////////////////////////

	
	// 매치 리스트 가져오기
	@Override
	public List<Match> list() throws Exception {
		return matchMapper.list();
	}
	
	@Override
	public int insert(Match match) throws Exception {
		
		// 매치 등록
		int result = matchMapper.insert(match);
		// 만약 매치 등록이 되지 않으면 파일을 등록할 필요도 없으니 리턴한다.
		if (result == 0 ) return result;
		// 매치 객체에 업로드된 파일들을 get해서 multipartfile 배열에 저장한다.
		
		return result;		
	}

	@Override
	public Match read(int matchNo) throws Exception {
		return matchMapper.read(matchNo);
	}


	@Override
	public GroundPic readPic(String groundPic) throws Exception {
		return matchMapper.readPic(groundPic);
	}


	// 매치 삭제 처리
	@Override
	public int delete(int matchNo) throws Exception {

		return matchMapper.delete(matchNo);
	}

	// 매치 참여 인원 명단에 등록
	@Override
	public int insertPlayer(String userId, int matchNo) throws Exception {
	
		// DB와 통신을 해야 한다.
		Players player = new Players();
		player.setParentsMatchNo(matchNo);
		player.setPlayerName(userId);
		player.setPayment(10000); // 선수 등록 시 지불한 금액 표시
		
		return matchMapper.insertPlayer(player);
	}

	// 매치 총 참여인원
	@Override
	public int countTotalPlayer(int matchNo) throws Exception {
		int totalCount = matchMapper.countTotalPlayer(matchNo);
		return totalCount;
	}

	// 매치 참여자 목록 조회
	@Override
	public List<Players> playerList(int matchNo) throws Exception {
		return matchMapper.playerList(matchNo);
	}

	// 매치 신청자 포인트 차감
	@Override
	public int pay(String userId, int matchNo) throws Exception {
		
		// DB와 통신을 해야 한다.
		Players player = new Players();
		player.setPlayerName(userId);
		log.info("userId : " + userId);
		player.setPayment(10000); // 선수 등록 시 지불한 금액 표시
		log.info("player : " + player);
		return matchMapper.pay(player);
	}

	// 내가 신청한 매치만 가져오기
	@Override
	public List<Match> myMatchList(String userId) throws Exception {
		return matchMapper.myMatchList(userId);
	}

	// 매치 취소하기
	@Override
	public int cancelPlayer(String userId, int matchNo) throws Exception {
		log.info("userId (서비스임플) : " + userId);
		log.info("matchNo (서비스임플) : " + matchNo);
		
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("userId", userId);
		paramMap.put("matchNo", matchNo);
		
		return matchMapper.cancelPlayer(paramMap);
	}

	
	
	
}
