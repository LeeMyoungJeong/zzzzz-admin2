package com.human.service;

import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.human.domain.KakaoPayReadyVO;

@Service
public class PayService {

	public KakaoPayReadyVO kakaoPay(String pgToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "KakaoAk 66c549c57bca329a6e87fcb146a1c78a");
		headers.set("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
		
		MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();
		
		map.add("cid", "TC0ONETIME");
		map.add("partner_order_id", "partner_order_id");
        map.add("tid", kakaoPayReadyVO.getTid());
        map.add("partner_order_id", kakaoPayReadyVO.getPartner_order_id());
        map.add("partner_user_id", kakaoPayReadyVO.getPartner_user_id());
        map.add("pg_token", pgToken);
		
		// 카카오페이 결제준비 api 요청
		HttpEntity<Map> request = new HttpEntity<Map>(payParams, headers);
		
		RestTemplate template = new RestTemplate();
		String url = "https://kapi.kakao.com/v1/payment/ready";
		
		// 요청결과
		KakaoPayReadyVO kakaoPayApprovalVO = template.postForObject(url,  request,  KakaoPayReadyVO.class);
		
		return res;
	}
	
}
