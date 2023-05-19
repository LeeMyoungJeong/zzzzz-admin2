package com.human.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/test")
public class RestapiController {

	@GetMapping
	public ResponseEntity<Object> test() {
		Map map = new HashMap<String, Object>();
		map.put("name", "test");
		
		return new ResponseEntity<Object>(map, HttpStatus.OK);
	}
	
	@PostMapping
	public ResponseEntity<Object> testPost( ) {
		Map map = new HashMap<String, Object>();
		map.put("name", "test");
		
		return new ResponseEntity<Object>(map, HttpStatus.OK);
	}
	
}
