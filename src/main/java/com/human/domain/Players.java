package com.human.domain;

import java.sql.Date;

import lombok.Data;

@Data
public class Players {

	private int playerNo;
	private int parentsMatchNo;
	private String playerName;
	private int payment;
	private Date regDate; 
	
}
