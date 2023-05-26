package com.human.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.human.domain.KakaoPayApproveVO;
import com.human.domain.Players;
import com.human.domain.TotalRevenues;
import com.human.domain.Users;
import com.human.mapper.MypageMapper;

import lombok.extern.slf4j.Slf4j;
import okhttp3.internal.http.HttpMethod;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


@Slf4j
@Service
public class MypageServiceImpl implements MypageService{

	////////////////////////////////////////////////////////

	@Autowired
	private MypageMapper mypageMapper;
	
	
	////////////////////////////////////////////////////////	
	
	
	@Override
	public int pointRead(String userId) throws Exception {
		return mypageMapper.pointRead(userId);
	}

	
	// 내 정보 수정하기
	@Override
	public int update(Users user) throws Exception {
		return mypageMapper.update(user);
	}


	@Override
	public List<TotalRevenues> getListPlayersDataBetween(String sdttm, String edttm) throws Exception {
		
		log.info("sdttm (서비스) : " + sdttm);
		log.info("edttm (서비스) : " + edttm);

		Date sdttm1 = null;
		Date edttm1 = null;
		
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		try {
			sdttm1 = dateFormat.parse(sdttm);
			edttm1 = dateFormat.parse(edttm);
		    // date 객체를 mapper에 전달하면 됩니다.
		} catch (ParseException e) {
		    e.printStackTrace();
		}

		log.info("sdttm1 (서비스) : " + sdttm1);
		log.info("edttm1 (서비스) : " + edttm1);
		
		return mypageMapper.getListPlayersDataBetween(sdttm1, edttm1);
	}


	/*
	 * @Override public KakaoPayApproveVO kakaoPayApprove(String pgToken) throws
	 * Exception {
	 * 
	 * HttpHeaders headers = new HttpHeaders(); headers.set("Authorization",
	 * "KakaoAK 66c549c57bca329a6e87fcb146a1c78a"); headers.set("Content-type",
	 * "application/x-www-form-urlencoded;charset=utf-8");
	 * 
	 * MultiValueMap<String, Object> payParams = new LinkedMultiValueMap<String,
	 * Object>();
	 * 
	 * String tid = ""; payParams.add("cid", "TC0ONETIME"); payParams.add("tid",
	 * tid); payParams.add("partner_order_id", "partner_order_id");
	 * payParams.add("partner_user_id", "partner_user_id");
	 * payParams.add("pg_token", pgToken);
	 * 
	 * // 카카오페이 결제요청 api 요청 HttpEntity<Map> request = new HttpEntity<Map>(payParams,
	 * headers);
	 * 
	 * RestTemplate template = new RestTemplate(); String url =
	 * "https://kapi.kakao.com/v1/payment/approve";
	 * 
	 * 
	 * // 요청결과 KakaoPayApproveVO res = template.postForObject(url, request,
	 * KakaoPayApproveVO.class);
	 * 
	 * return res; }
	 */
}
