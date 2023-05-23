package com.human.controller;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javax.net.ssl.HttpsURLConnection;
import javax.servlet.http.HttpSession;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.human.domain.GroundPic;
import com.human.domain.KakaoPayApproveVO;
import com.human.domain.KakaoPayReadyVO;
import com.human.domain.Match;
import com.human.domain.Players;
import com.human.domain.Users;
import com.human.mapper.MatchMapper;
import com.human.mapper.UserMapper;
import com.human.service.MatchService;
import com.human.service.MypageService;
//import com.human.service.PayService;

import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

@Controller
@Slf4j
public class MypageController {
	
    // 카카오페이 API 호출을 위한 인증 정보
    private static final String KAKAO_APP_ADMIN_KEY = "66c549c57bca329a6e87fcb146a1c78a";
	
	///////   의존주입   ////////////////////////////////////

	@Autowired
	private PasswordEncoder passwordEncoder;
    
	@Autowired
	private MypageService mypageService;
	
	//@Autowired
	//private PayService payService;
	
	@Autowired
	private MatchService matchService;
	
	@Autowired
	private UserMapper userMapper;
	
	@Autowired
	private MatchMapper matchMapper;
	
	//////////////////////////////////////////////////////
	
	// mypage 메인 화면 출력 - Get
	@GetMapping("/mypage/main")
	@PreAuthorize("hasAnyRole('USER','ADMIN')")
	public String insertForm(Model model, Principal principal) throws Exception {
		
		String userId = principal.getName();
		int point = mypageService.pointRead(userId);
		List <Match> myMatchList = matchService.myMatchList(userId);
		
		log.info("마이페이지 메인화면 요청");
		log.info("username(사용자 아이디) : " + userId);
		log.info("point(사용자 잔여포인트) : " + point);
		log.info("myMatchList : " + myMatchList);
		
		model.addAttribute("myMatchList", myMatchList);
		model.addAttribute("userId", userId);
		model.addAttribute("point", point);
		
		return "/mypage/main";
	}
	
	@GetMapping("/mypage/kakaoPay")
	@PreAuthorize("hasRole('ADMIN')")
	public String mypageView() throws Exception {
		return "/mypage/kakaoPay";
	}
	
	// 마이페이지 -> 수정을 위해 내정보 가져오기
	@GetMapping("/mypage/myInfo")
	@PreAuthorize("hasAnyRole('USER','ADMIN')")
	public String myInfo(Model model, Principal principal) throws Exception {
		
		log.info("마이페이지의 내 정보 요청");
		
		String userId = principal.getName();
		Users user = userMapper.findFromMyinfo(userId);
		
		
		model.addAttribute("user", user);
		model.addAttribute("userId", userId);
		
		return "/mypage/myInfo";
	}
	
	// 마이페이지 -> 내 정보 수정하기
	@PostMapping("/mypage/myInfo")
	@PreAuthorize("hasAnyRole('USER','ADMIN')")
	public String myInfoUpdate(Users user, Principal principal) throws Exception {

		String plainPw = user.getUserPw();
		String encodedPw = passwordEncoder.encode(plainPw);
		user.setUserPw(encodedPw);
		
		int result = mypageService.update(user);
		
		return "redirect:/mypage/main";
	}
	
	
	
	// 종료된 매치 환급하기
	@GetMapping(path="/mypage/endMatchPlayer", params = "matchNo")
	@PreAuthorize("hasAnyRole('ADMIN','USER')")
	public String endMatchPlayer(Model model, int matchNo, String groundName, Principal principal) throws Exception {
		
		
		String userId = principal.getName();
		model.addAttribute("userId", userId);
		
		// 환불여부
		int refundYn = matchMapper.refundYn(matchNo);
		log.info("refundYn : " + refundYn);
		
		List <Players> playerList = matchService.playerList(matchNo);
		model.addAttribute("playerList", playerList);
		log.info("playerList : " + playerList);
		Match match = matchService.read(matchNo);
		int totalPlayer = matchService.countTotalPlayer(matchNo);
		GroundPic groundPic = matchService.readPic(groundName);
		model.addAttribute("totalPlayer", totalPlayer);
		model.addAttribute("groundPic", groundPic);
		model.addAttribute("matchNo", matchNo);
		model.addAttribute("match", match);
		model.addAttribute("refundYn", refundYn);
		
		
		return "/mypage/endMatchPlayer";
		
	}
	
	
	
	
	
	// 매출관리 페이지 - 게임이 끝난 매치 확인하고 포인트 환급 및 정산하기
	@GetMapping("/mypage/sales")
	@PreAuthorize("hasRole('ADMIN')")
	public String sales(Model model, Principal principal) throws Exception {
		
		// 환불여부
		// int refundYn = matchMapper.refundYn(matchNo);
		List <Match> endMatchList = matchService.endMatchList();
		log.info("endMatchList : " + endMatchList);
		String userId = principal.getName();
		model.addAttribute("userId", userId);
		model.addAttribute("endMatchList", endMatchList);
		log.info("매출관리페이지 호출");
		return "/mypage/sales";
	}

	
	
	
	// 결제 페이지 호출
    @GetMapping("/mypage/charge")
    public String chargePage() throws Exception{
    	return "/mypage/charge";
    }
	
    // 카카오페이 결제 모듈
    @RequestMapping("/kakaopay")
    @ResponseBody
    public String kakaopay(@RequestParam Map<String, Object> params)  {
    	
		  try { URL url = new URL("https://kapi.kakao.com/v1/payment/ready");
		  HttpURLConnection connection = (HttpsURLConnection) url.openConnection();
		  connection.setRequestMethod("POST");
		  connection.setRequestProperty("Authorization","KakaoAK 66c549c57bca329a6e87fcb146a1c78a");
		  connection.setRequestProperty("Content-type","application/x-www-form-urlencoded;charset=utf-8");
		  connection.setDoOutput(true); // 이 연결을 통해 뭔가 서버에 전해줄것이 있는지 없는지 여부, 여기선 있으니까 true로 설정 // 기본적으로 false로 되어있기 때문에 true로 바꿔줘야 함
		  
		  String param =
		  "cid=TC0ONETIME&partner_order_id=partner_order_id&partner_user_id=partner_user_id&item_name=10,000 point&quantity=1&total_amount=10000&vat_amount=100&tax_free_amount=0&approval_url=http://localhost:8080/mypage/success&fail_url=http://localhost:8080/mypage/fail&cancel_url=http://localhost:8080/mypage/cancel";
		  OutputStream outputStream = connection.getOutputStream(); 
		  DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
		  
		  dataOutputStream.writeBytes(param); dataOutputStream.flush();
		  dataOutputStream.close();
		  
		  int resultCode = connection.getResponseCode();
		  
		  InputStream inputStream;
		  
		  if(resultCode == 200) { inputStream = connection.getInputStream(); 
		  
		  } else {
		  
			  inputStream = connection.getErrorStream(); } InputStreamReader
		  	  inputStreamReader = new InputStreamReader(inputStream); BufferedReader
		  	  bufferedReader = new BufferedReader(inputStreamReader);
		  
		  String response = bufferedReader.readLine();
		  log.info("response : " + response);
		  

		  
		  return response;
		  
		  } catch (MalformedURLException e) { e.printStackTrace(); } 
		    catch (IOException e) { e.printStackTrace(); } 		 
		  
		  return "{\"result\":\"NO\"}";
		 
    }
	
	// 결제 성공 페이지 호출
    @RequestMapping("/mypage/success")
    public String kakaoPaySuccess(@RequestParam("pg_token") String pgToken, HttpSession session) {
    	
    	return "/mypage/success";
    }
    
    
    
    
}
