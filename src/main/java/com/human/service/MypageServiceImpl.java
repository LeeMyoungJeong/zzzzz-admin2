package com.human.service;

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
import com.human.mapper.MypageMapper;

import okhttp3.internal.http.HttpMethod;

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
