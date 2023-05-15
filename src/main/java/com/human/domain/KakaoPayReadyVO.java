package com.human.domain;

import lombok.Data;

@Data
public class KakaoPayReadyVO {

	private String tid; // 결제 고유번호
	private String next_redirect_mobile_url; // 요청한 클라이언트가 모바일 웹
	private String next_redirect_pc_url; // 요청한 클라이언트가 pc 웹
	private String partner_order_id;	// 가맹점 주문번호
}
