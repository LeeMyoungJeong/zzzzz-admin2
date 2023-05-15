package com.human.domain;

import lombok.Data;

@Data
public class KakaoPayApproveVO {

	private String aid;
	private String tid;
	private String partner_order_id;
	private String partner_user_id;
	private String item_name;
	private String quantity;
	private String total_amount;
	private String vat_amount;
	private String tax_free_amount;
	
}
