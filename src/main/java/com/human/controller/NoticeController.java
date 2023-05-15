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

import com.human.domain.Board;
import com.human.domain.Files;
import com.human.domain.Notice;
import com.human.domain.Page;
import com.human.service.FileService;
import com.human.service.NoticeService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class NoticeController {

	///////////////////////////////////////////////////////////////////////
	
	@Autowired
	private NoticeService noticeService;

	@Autowired
	private FileService fileService;
	
	
	///////////////////////////////////////////////////////////////////////
	
	
	// 공지사항 목록 조회 화면 - Get 
	@GetMapping("/notice/list")
	public String list(Model model, Page page, Principal principal) throws Exception {
		
		List<Notice> noticeList = noticeService.list(page);
		
		String userId = principal.getName();
		log.info("userId : " + userId);
		
		log.info("page : " + page);
		model.addAttribute("noticeList", noticeList);
		model.addAttribute("page", page);
		model.addAttribute("userId", userId);
		// 아직 페이징 처리 구현 안되어있음
		
		return "/notice/list";
	}
	
	
	
	// 공지사항 입력 화면 - Get
	@GetMapping("/notice/insert")
	@PreAuthorize("hasRole('ADMIN')")
	public String formNoticeInsert(Model model, Principal principal) throws Exception {
		
		String adminId = principal.getName();
		log.info("공지사항 쓰기 화면 요청");
		log.info("작성요청자 아이디 : " + adminId);
		
		model.addAttribute("adminId", adminId);
		
		return "/notice/insert";
	}
	
	// 공지사항 입력 처리 - Post
	@PostMapping("/notice/insert")
	@PreAuthorize("hasRole('ADMIN')")
	public String noticeInsert(Notice notice) throws Exception{
		
		MultipartFile[] files = notice.getFiles();
		
		// 요청된 첨부파일 확인 ( 조건문으로 파일 존재 여부 확인 후 처리 진행 )
				if( files != null ) {
					
					for (MultipartFile file : files ) {
						// log.info()를 사용해서 안에 파일들이 잘 들어간 후 파일들이 Post 되었는지 여부 확인하는 과정임
						
						String fileName = file.getOriginalFilename(); // getOriginalFilename()은 MultipartFile의 메소드로서 파일의 원래 이름을 가져온다.
						String contentType = file.getContentType();   // getOriginalFilename(), getContentType(), getSize() 모두 MultipartFile[] 메소드임
						long size = file.getSize();
						byte[] fileData = file.getBytes();
						
						// log.info를 통해 안에 담긴 내용이 어떤 내용인지 확인 가능하다.
						log.info("파일 업로드 확인");
						log.info("fileName : " + fileName);
						log.info("contentType : " + contentType);
						log.info("size : " + size);
						log.info("fileData : " + fileData);
					}
				}
				
				int result = noticeService.insert(notice);
				
				return "redirect:/notice/list";	
				
	}
	
	
	// 공지사항 읽기 - 화면 GET
	@GetMapping( path="/notice/read" , params = "noticeNo")
	
	public String read(Model model, int noticeNo, Files file, Principal principal) throws Exception {
		
		System.out.println(noticeNo + " : 번 게시글 읽기");
		// log.info(boardNo + " : 번 게시글 읽기");
		
		// 게시글 조회
		Notice notice = noticeService.read(noticeNo);
		
		// 그 전에 작성자 본인 확인
		String loginUserId = principal.getName();
		String noticeUserId = notice.getWriter();
			// Principal 객체를 사용하면 현재 로그인 된 사용자의 정보를 가져올 수 있다.
		
		boolean writer = false; // 작성자 본인 여부 -> 일단 false
		if ( loginUserId.equals(noticeUserId) ) {
			writer = true;
		}
		
		
		// 첨부된 파일 목록 조회
		file.setParentTable("notice");
		file.setParentNo(noticeNo);
		System.out.println(" file정보 : " + file);
		List<Files> fileList = fileService.listByParentNo( file );
		
		log.info("fileList1 : " + fileList); 
		
		model.addAttribute("writer", writer); // 이 과정을 통해 만들어진 writer라는 boolean 변수를 통해서 글쓴이와 글 수정 요청자가 일치하는지 여부가 판단 가능하다.
											  // view에서 writer라는 변수가 true인지 false인지 판단해서 다른 정보를 출력해줄거다.
		model.addAttribute("notice", notice);
		model.addAttribute("noticeNo", noticeNo);
		model.addAttribute("fileList", fileList); // 파일리스트를 모델에 등록하지 않아서 파일 목록이 나오지 않았던거임...
												  // 지금은 이미지를 불러오지 못해서 th:alt 속성이 나오고 있는것임
					
		log.info("fileList2 : " + fileList); 
		// 작성자 본인만 수정/삭제할 수 있는 기능 아직 구현 X
		// 파일 첨부 기능 아직 구현 X
		
		return "/notice/read";
	}
		
	// 공지사항 수정 - 화면 GET
	@GetMapping ( path = "/notice/update", params = "noticeNo")
	@PreAuthorize("hasRole('ADMIN')")
	public String updateForm(Model model, Files file, int noticeNo, Principal principal) throws Exception {
		
		log.info(noticeNo + " :  번 공지사항 수정화면 요청");
		Notice notice = noticeService.read(noticeNo);
		
		model.addAttribute("notice", notice);
		model.addAttribute("noticeNo", noticeNo);
		
		// 그 전에 작성자 본인 확인
		String loginUserId = principal.getName();
		String noticeUserId = notice.getWriter();
			// Principal 객체를 사용하면 현재 로그인 된 사용자의 정보를 가져올 수 있다.
		
		boolean writer = false; // 작성자 본인 여부 -> 일단 false
		if ( loginUserId.equals(noticeUserId) ) {
			writer = true;
		}		
		
		// 첨부된 파일 목록 조회
		file.setParentTable("notice");
		file.setParentNo(noticeNo);
		System.out.println(" file정보 : " + file);
		List<Files> fileList = fileService.listByParentNo( file );
		
		log.info("fileList1 : " + fileList); 
		
		model.addAttribute("writer", writer); // 이 과정을 통해 만들어진 writer라는 boolean 변수를 통해서 글쓴이와 글 수정 요청자가 일치하는지 여부가 판단 가능하다.
											  // view에서 writer라는 변수가 true인지 false인지 판단해서 다른 정보를 출력해줄거다.
		model.addAttribute("notice", notice);
		model.addAttribute("noticeNo", noticeNo);
		model.addAttribute("fileList", fileList); // 파일리스트를 모델에 등록하지 않아서 파일 목록이 나오지 않았던거임...
												  // 지금은 이미지를 불러오지 못해서 th:alt 속성이 나오고 있는것임
					
		log.info("fileList2 : " + fileList); 
		// 작성자 본인만 수정/삭제할 수 있는 기능 아직 구현 X
		// 파일 첨부 기능 아직 구현 X		
		
		
		return "/notice/update";
	}	

	// 공지사항 수정 - 처리 
	@PostMapping ("/notice/update")
	@PreAuthorize("hasRole('ADMIN')")
	public String update(Notice notice, Principal principal) throws Exception {
		
		int noticeNo = notice.getNoticeNo();
		
		MultipartFile[] files = notice.getFiles();
		
		// 요청된 첨부파일 확인 ( 조건문으로 파일 존재 여부 확인 후 처리 진행 )
				if( files != null ) {
					
					for (MultipartFile file : files ) {
						// log.info()를 사용해서 안에 파일들이 잘 들어간 후 파일들이 Post 되었는지 여부 확인하는 과정임
						
						String fileName = file.getOriginalFilename(); // getOriginalFilename()은 MultipartFile의 메소드로서 파일의 원래 이름을 가져온다.
						String contentType = file.getContentType();   // getOriginalFilename(), getContentType(), getSize() 모두 MultipartFile[] 메소드임
						long size = file.getSize();
						byte[] fileData = file.getBytes();
						
						// log.info를 통해 안에 담긴 내용이 어떤 내용인지 확인 가능하다.
						log.info("파일 업로드 확인");
						log.info("fileName : " + fileName);
						log.info("contentType : " + contentType);
						log.info("size : " + size);
						log.info("fileData : " + fileData);
					}
				}
		
		
		// 로그인유저아이디와 인증된 사용자 이름이 일치하지 않으면 다른경로로 이동시켜줘야 함
		
		int result = noticeService.update(notice);
		
		return "redirect:/notice/read?noticeNo=" + noticeNo;
	}
	
	
	// 공지사항 삭제 - 처리 (삭제처리 PostMapping으로 하는것임)
	@PostMapping("/notice/delete")
	@PreAuthorize("hasRole('ADMIN')")
	public String delete(int noticeNo, Principal principal) throws Exception {
		
		log.info(noticeNo + "번 공지사항 삭제...");
		
		// 글쓴이와 삭제요청자가 동일한지 파악해야한다 - 기능구현 아직 안되어있음
		
		int result = noticeService.delete(noticeNo);
		int result2 = fileService.delete(noticeNo);
		return "redirect:/notice/list";
	}
	
	
	
	
	
	
	
	
	
}
