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
import com.human.domain.Page;
import com.human.service.BoardService;
import com.human.service.FileService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class BoardController {

	///////   의존주입   ////////////////////////////////////

	@Autowired
	private BoardService boardService;
	
	@Autowired
	private FileService fileService;
	
	//////////////////////////////////////////////////////
	
	// 게시글 목록 - 화면 GET
	@GetMapping("/board/list")
	public String list(Model model, Page page) throws Exception {
		
		List <Board> boardList = boardService.list(page);
		
		log.info("page : " + page);
		model.addAttribute("boardList", boardList);
		model.addAttribute("page", page);
		// 아직 페이징 처리 구현 안되어있음
		
		return "/board/list";
	}
		
	
	// 기본적으로 게시판은 SecurityConfig에 정의되어 있듯이 permitAll() 되어 있다.
	// 다시 말해 접근 권한이 모두에게 있다는 뜻인데,
	// 게시글 쓰기 부터는 접근 권한을 설정해주어야 한다.
	// 접근 권한이 없다면 로그인 화면으로 자동으로 이동할 것이다.
	// 게시글 쓰기 - 화면 GET	
	@GetMapping("/board/insert")
	@PreAuthorize("hasAnyRole('USER','ADMIN')")
	public String insertForm(Model model, Principal principal) throws Exception {
		
		String userId = principal.getName();
		log.info("게시글 쓰기 화면 요청");
		log.info("username(사용자 아이디) : " + userId);
		
		model.addAttribute("userId", userId);
		
		return "/board/insert";
	}
	
	
	// 게시글 쓰기 - 처리 POST
	@PostMapping("/board/insert")
	@PreAuthorize("hasAnyRole('USER','ADMIN')")
	public String insert(Board board) throws Exception {
		
		MultipartFile[] files = board.getFiles();
		
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
		
		int result = boardService.insert(board);
		
		return "redirect:/board/list";		
	}
	
	
	// 게시글 읽기 - 화면 GET
	@GetMapping( path="/board/read" , params = "boardNo")
	@PreAuthorize("hasAnyRole('USER','ADMIN')")
	public String read(Model model, int boardNo, Files file, Principal principal) throws Exception {
		
		System.out.println(boardNo + " : 번 게시글 읽기");
		// log.info(boardNo + " : 번 게시글 읽기");
		
		// 게시글 조회
		Board board = boardService.read(boardNo);
		
		// 그 전에 작성자 본인 확인
		String loginUserId = principal.getName();
		String boardUserId = board.getWriter();
			// Principal 객체를 사용하면 현재 로그인 된 사용자의 정보를 가져올 수 있다.
		
		boolean writer = false; // 작성자 본인 여부 -> 일단 false
		if ( loginUserId.equals(boardUserId) ) {
			writer = true;
		}
		
		
		// 첨부된 파일 목록 조회
		file.setParentTable("board");
		file.setParentNo(boardNo);
		System.out.println(" file정보 : " + file);
		List<Files> fileList = fileService.listByParentNo( file );
		
		log.info("fileList1 : " + fileList); 
		
		model.addAttribute("writer", writer); // 이 과정을 통해 만들어진 writer라는 boolean 변수를 통해서 글쓴이와 글 수정 요청자가 일치하는지 여부가 판단 가능하다.
											  // view에서 writer라는 변수가 true인지 false인지 판단해서 다른 정보를 출력해줄거다.
		model.addAttribute("board", board);
		model.addAttribute("boardNo", boardNo);
		model.addAttribute("fileList", fileList); // 파일리스트를 모델에 등록하지 않아서 파일 목록이 나오지 않았던거임...
												  // 지금은 이미지를 불러오지 못해서 th:alt 속성이 나오고 있는것임
					
		log.info("fileList2 : " + fileList); 
		// 작성자 본인만 수정/삭제할 수 있는 기능 아직 구현 X
		// 파일 첨부 기능 아직 구현 X
		
		return "/board/read";
	}
	
	
	// 게시글 수정 - 화면 GET
	// 게시글 수정 화면 Get 할 때 파일 내용이 안뜨고있네...
	@GetMapping ( path = "/board/update", params = "boardNo")
	public String updateForm(Model model, int boardNo, Files file, Principal principal) throws Exception {
		
		log.info(boardNo + " :  번 게시글 수정화면 요청");
		Board board = boardService.read(boardNo);
		
		// 첨부된 파일 목록 조회
		file.setParentTable("board");
		file.setParentNo(boardNo);
		System.out.println(" file정보 : " + file);
		List<Files> fileList = fileService.listByParentNo( file );
		
		log.info("fileList1 : " + fileList); 
		
		model.addAttribute("board", board);
		model.addAttribute("boardNo", boardNo);
		model.addAttribute("fileList", fileList);
		
		return "/board/update";
	}
	
	// 게시글 수정 - 처리 
	@PostMapping ("/board/update")
	public String update(Board board, Principal principal) throws Exception {
		
		int boardNo = board.getBoardNo();
		
		// 로그인유저아이디와 인증된 사용자 이름이 일치하지 않으면 다른경로로 이동시켜줘야 함
		
		int result = boardService.update(board);
		
		return "redirect:/board/read?boardNo=" + boardNo;
	}
	
	
	// 게시글 삭제 - 처리 (삭제처리 PostMapping으로 하는것임)
	@PostMapping("/board/delete")
	public String delete(int boardNo, Principal principal) throws Exception {
		
		log.info(boardNo + "번 게시글 삭제...");
		
		// 글쓴이와 삭제요청자가 동일한지 파악해야한다 - 기능구현 아직 안되어있음
		
		int result = boardService.delete(boardNo);
		
		return "redirect:/board/list";
	}
	
	
	// About Us로 이동 - 화면 Get
	@GetMapping("/board/aboutUs")
	public String aboutUs(Principal principal) throws Exception{
		return "/board/aboutUs";
	}
	
	
	
	
	
}
