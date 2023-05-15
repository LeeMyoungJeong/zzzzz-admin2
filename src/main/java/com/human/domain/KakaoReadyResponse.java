package com.human.domain;


import java.util.Date;

import lombok.Data;

@Data
public class KakaoReadyResponse {

	private String tid; // 결제 고유 번호
	private String next_redirect_pc_url; // pc웹일 경우 받는 결제 페이지
	private Date created_at; // 결제 준비 요청 시간
	
}
