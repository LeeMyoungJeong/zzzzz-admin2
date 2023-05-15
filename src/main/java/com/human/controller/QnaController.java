package com.human.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;

import com.human.domain.Files;
import com.human.domain.Page;
import com.human.domain.Qna;
import com.human.service.FileService;
import com.human.service.QnaService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class QnaController {

	//////////////////////////////////////////////////////////////////////////
	
	@Autowired
	private QnaService qnaService;
	
	@Autowired
	private FileService fileService;
	
	//////////////////////////////////////////////////////////////////////////
	
	// 질의응답 목록
	@GetMapping("/qna/list")
	public String list(Model model, Page page) throws Exception {
		
		List<Qna> qnaList = qnaService.list(page);
		
		log.info("page : " + page);
		model.addAttribute("page", page);
		model.addAttribute("qnaList", qnaList);
		
		return "/qna/list";		// board/list.html
	}
	
	
	// 질의응답 쓰기 - 화면 Get
	@GetMapping("/qna/insert")
	// @PreAuthorize("hasRole('USER')")
	@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
	public String insertForm(Principal principal, Model model) {
		
		String userId = principal.getName();	// 아이디
		log.info("userId : " + userId);
		
		model.addAttribute("userId", userId);
		
		return "/qna/insert";		// board/insert.html
	}
	
	// 질의응답 쓰기 - 처리 Post
	@PostMapping("/qna/insert")
	@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
	public String insert(Qna qna) throws Exception {
		//  MultipartFile : 전송된 파일데이터를 다루는 인터페이스
		
		
		MultipartFile[] files = qna.getFiles();
		
		if( files != null || files.length == 0 ) {
			// 요청된 첨부파일 확인
			for (MultipartFile file : files) {
				String fileName = file.getOriginalFilename();		// 파일 명
				String contentType = file.getContentType();			// 파일 타입(확장자)
				long size = file.getSize();							// 용량
				byte[] fileData = file.getBytes();					// 파일 데이터
				
				log.info("fileName : " + fileName);
				log.info("contentType : " + contentType);
				log.info("size : " + size);
			}
		}
		
		int result = qnaService.insert(qna);
		
		return "redirect:/qna/list"; 	// 일단 여기로 옮겨가도록 조치해놓자
		//return "redirect:/qna/list";		// 게시글 쓰기 처리 후, 게시글 목록으로 리다이렉트
	}	

	
	// 질의응답 읽기 - 화면 Get
	@GetMapping(path = "/qna/read", params = "qnaNo")
	@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
	public String read(Model model, int qnaNo, Files file, Principal principal) throws Exception {
		// 게시글 번호로 게시글 정보를 조회
		System.out.println("질의응답 조회...");
		System.out.println("qnaNo : " + qnaNo);
		
		// 게시글 조회
		Qna qna = qnaService.read(qnaNo);
		
		// 작성자 본인 확인
		String loginUserId = principal.getName();
		String boardUserId = qna.getWriter();
		
		boolean writer = false;		// 작성자 본인 여부
		if( loginUserId.equals(boardUserId) ) {
			writer = true;
		}
		
		// 파일 목록 조회
		file.setParentTable("qna");
		file.setParentNo(qnaNo);
		// System.out.println(" file정보 : " + file);
		List<Files> fileList = fileService.listByParentNo( file );
		
		
		model.addAttribute("writer", writer);		
		model.addAttribute("qnaNo", qnaNo);
		model.addAttribute("qna", qna);
		model.addAttribute("fileList", fileList);
		return "/qna/read";
	}	
	
	// 질의응답 수정 - 화면 Get
	@GetMapping(path = "/qna/update", params = "qnaNo")
	@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
	public String updateForm(Model model, int qnaNo, Files file, Principal principal) throws Exception {
		// 게시글 번호로 게시글 정보를 조회
		System.out.println("질의응답 조회(수정화면)...");
		System.out.println("qnaNo : " + qnaNo);
		
		Qna qna = qnaService.read(qnaNo);
		
		// 작성자 본인 확인
		String loginUserId = principal.getName();
		String qnaUserId = qna.getWriter();
		
		boolean writer = false;		// 작성자 본인 여부
		if( loginUserId.equals(qnaUserId) ) {
			writer = true;
		}
		
		// 파일 목록 조회
		file.setParentTable("qna");
		file.setParentNo(qnaNo);
		List<Files> fileList = fileService.listByParentNo( file );
		
		model.addAttribute("writer", writer);
		model.addAttribute("qnaNo", qnaNo);
		model.addAttribute("qna", qna);
		model.addAttribute("fileList", fileList);
		return "/qna/update";
	}	
	
	// 질의응답 수정 - 처리 Post
	@PostMapping("/qna/update")
	@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
	public String updatePost(Qna qna, Principal principal) throws Exception {
		// 요청 파라미터명과 일치하는 변수명을 가지고 있는 객체를 사용하여 여러 파라미터를 가져올 수 있다.
		int qnaNo = qna.getQnaNo();
		String title = qna.getTitle();
		String writer = qna.getWriter();
		String content = qna.getContent();
		
		// 작성자 본인 아닐 때, 다시 수정페이지로 리다이렉트
		String loginUserId = principal.getName();
		if( !writer.equals(loginUserId) ) {
			return "redirect:/qna/update?qnaNo=" + qnaNo;
		}
		
		// 질의응답 수정 요청
		int result = qnaService.update(qna);
		
		System.out.println("title : " + title);
		System.out.println("writer : " + writer);
		System.out.println("content : " + content);
		System.out.println("qnaNo : " + qnaNo);
		return "redirect:/qna/read?qnaNo=" + qnaNo;
	}	
	
	
	// 질의응답 삭제 - 처리 Post
	
	@PostMapping("/qna/delete")
	@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
	public String deletePost(int qnaNo, Principal principal) throws Exception {
		// 게시글 번호로 게시글 삭제
		System.out.println("qnaNo : " + qnaNo);
		Qna qna = qnaService.read(qnaNo);
		String loginUserId = principal.getName();
		String qnaUserId = qna.getWriter();
		
		// 작성자 본인 아닐 때,
		if( !qnaUserId.equals(loginUserId) ) {
			return "redirect:/qna/read?qnaNo=" + qnaNo;
		}
		
		int result = qnaService.delete(qnaNo);
		
		return "redirect:/qna/list";
	}	
	
	
	
	
	
}
