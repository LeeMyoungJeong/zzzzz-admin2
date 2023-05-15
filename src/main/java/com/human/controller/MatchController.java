package com.human.controller;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.human.domain.Files;
import com.human.domain.GroundPic;
import com.human.domain.Match;
import com.human.domain.Page;
import com.human.domain.Players;
import com.human.domain.TotalPlayer;
import com.human.mapper.MatchMapper;
import com.human.service.FileService;
import com.human.service.MatchService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class MatchController {
	
	////////////////////////////////////////////////////////////
	@Autowired
	private FileService fileService;	
	
	@Autowired
	private MatchService matchService;
	
	@Autowired
	private MatchMapper matchMapper;
	
	////////////////////////////////////////////////////////////
	
	
	// 매치 리스트 가져오기 화면 - Get
	@GetMapping("/match/list")
	public String list(Model model, Principal principal) throws Exception {
		
		String userId = principal.getName();
		model.addAttribute("userId", userId);
		
		List <Match> matchList = matchService.list();
		int result = matchMapper.playerUpdate();
		//int totalPlayer = matchMapper.totalPlayer();
		log.info("result가 1이어야 좋은것 : " + result);
		model.addAttribute("matchList", matchList);
		//model.addAttribute("totalPlayer", totalPlayer);
		
		// log.info("matchList : " + matchList);
		return "/match/list";
	}
	
	// 매치 등록하기 - 화면 Get
	@GetMapping("/match/insert")
	@PreAuthorize("hasRole('ADMIN')")
	public String insertForm(Model model, Principal principal) throws Exception {
		
		String userId = principal.getName();
		log.info("게시글 쓰기 화면 요청");
		log.info("username(사용자 아이디) : " + userId);
		
		model.addAttribute("userId", userId);
		
		return "/match/insert";
	}
	
	// 매치 등록하기 - 처리 POST
	@PostMapping("/match/insert")
	@PreAuthorize("hasRole('ADMIN')")
	public String insert(Match match) throws Exception {
		
		int result = matchService.insert(match);
		
		return "redirect:/match/list";		
	}
	

	// 매치 조회하기 - Get
	@GetMapping(path="/match/read" , params = "matchNo")
	@PreAuthorize("hasAnyRole('ADMIN','USER')")
	public String read(Model model, int matchNo, Files file, String groundName, Principal principal) throws Exception {
		
		System.out.println(matchNo + "번 매치 확인");
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		//String role = auth.getAuthorities();
		log.info("auth : " + auth);
		//log.info("username : " + username);
		
		String userId = principal.getName();
		model.addAttribute("userId", userId);
		
		List <Players> playerList = matchService.playerList(matchNo);
		log.info("playerList : " + playerList);
		
		
		Match match = matchService.read(matchNo);
		int totalPlayer = matchService.countTotalPlayer(matchNo);
		
		
		GroundPic groundPic = matchService.readPic(groundName);
		log.info("groundPic : " + groundPic);
		// 첨부된 파일 조회
		file.setParentTable("match");
		file.setParentNo(matchNo);
		List<Files> fileList = fileService.listByParentNo( file );
		System.out.println(" file정보 : " + file);
		
		log.info("fileList1 : " + fileList); 
		
		model.addAttribute("playerList", playerList);
		model.addAttribute("totalPlayer", totalPlayer);
		log.info("totalPlayer : " + totalPlayer); 
		model.addAttribute("match", match);
		model.addAttribute("groundPic", groundPic);
		model.addAttribute("matchNo", matchNo);
		model.addAttribute("fileList", fileList); // 파일리스트를 모델에 등록하지 않아서 파일 목록이 나오지 않았던거임...
												  // 지금은 이미지를 불러오지 못해서 th:alt 속성이 나오고 있는것임
					
		log.info("fileList2 : " + fileList); 
		// 작성자 본인만 수정/삭제할 수 있는 기능 아직 구현 X
		// 파일 첨부 기능 아직 구현 X
		
		return "/match/read";		
	}


	// 매치 삭제하기
	@PostMapping("/match/delete")
	public String delete(int matchNo) throws Exception {
		System.out.println("매치 삭제");
		log.info(matchNo + "번 매치 삭제...");
		Match match = matchService.read(matchNo);
		
		// 글쓴이와 삭제요청자가 동일한지 파악
		
		// String loginUserId = principal.getName();
		
		int result = matchService.delete(matchNo);
		
		
		return "redirect:/match/list";
	}

	
	// 매치 신청하기 - 확인창 띄우기
	
	@GetMapping("/checkPoints")
	@ResponseBody
	public Map<String, Object> matchCheck(@RequestParam("userId") String userId, @RequestParam("matchNo") int matchNo, Model model) throws Exception {
		
		System.out.println("포인트 조회하기 ㄱㄱ");
		
		int result = matchMapper.matchCheck(userId);
		log.info("result : " + result );

		 Map<String, Object> response = new HashMap<>();
		
		if (result < 10000) {
			response.put("result", result);
			return response;
		} else {
			response.put("result", "success");
		}
			matchService.insertPlayer(userId, matchNo);
			matchService.pay(userId, matchNo);
		
	    return response;
	}

	/*
	 * @GetMapping("/checkPlayer")
	 * 
	 * @ResponseBody public Map<String, Object> playerCheck(@RequestParam("matchNo")
	 * String matchNo) throws Exception {
	 * 
	 * System.out.println("매치 인원 추가 가능여부 조회하기");
	 * 
	 * int result = matchMapper.playerCheck(matchNo); log.info("result : " + result
	 * );
	 * 
	 * Map<String, Object> response = new HashMap<>();
	 * 
	 * if (result )
	 * 
	 * }
	 */
	// 매치 삭제하기
	@GetMapping("/deletePlayer")
	@ResponseBody
	public String cancelPlayer(@Param("userId") String userId, @Param("matchNo") int matchNo) throws Exception {
		System.out.println(userId + "의" + matchNo + "번 매치 취소(컨트롤러)");
		log.info(userId + "의" + matchNo + "번 매치 취소(컨트롤러)");
		
		int result = matchService.cancelPlayer(userId, matchNo);
		log.info("result(매치삭제결과)" + result);
		
		// 글쓴이와 삭제요청자가 동일한지 파악
		
		// String loginUserId = principal.getName();
		
		if (result == 1) {
	        return "success";
	    } else {
	        return "fail";
	    }
	}



}



