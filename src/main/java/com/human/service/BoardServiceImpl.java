package com.human.service;

import java.io.File;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import com.human.domain.Board;
import com.human.domain.Files;
import com.human.domain.Page;
import com.human.mapper.BoardMapper;
import com.human.mapper.FileMapper;
import com.human.utils.FileUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class BoardServiceImpl implements BoardService{

	////////////////////////////////////////////////////////
	
	// 업로드 경로
	@Value("${upload.path}")
	private String uploadPath;
	// # application.properties에 정의한 업로드 경로 정보
	// upload.path=C:/KHM/upload/
	
	@Autowired
	private BoardMapper boardMapper;
	
	@Autowired
	private FileMapper fileMapper;
	
	@Autowired
	private FileUtil fileUtil;
	
	////////////////////////////////////////////////////////
	
	// 게시글 목록 조회
	@Override
	public List<Board> list() throws Exception {
		return boardMapper.list();
	}

	// 게시글 입력
	// 게시글 입력 시 첨부된 파일을 업로드 경로에 복사하는 작업이 동시에 진행됨
	// 그러면서 File 객체가 가져야 할 속성들도 함께 여기서 선언됨
	@Override
	public int insert(Board board) throws Exception {
		
		
		// 게시글 등록
		int result =  boardMapper.insert(board);
		
		// 만약 게시글 등록이 되지 않으면 파일을 업로드 할 필요도 없으니 파일 업로드 과정을 생략하고 그대로 return한다.
		if (result == 0 ) return result;
		
		// Board 객체에 업로드된 files들을 get()해서 MultiparFile[] 배열에 저장한다.
		MultipartFile[] files = board.getFiles();
		int numFiles = files.length;
		log.info("files[0] :  " + files[0]);
		log.info("numFiles :  " + numFiles);
		//log.info("files[1] :  " + files[1]);
		if (files.length > 0 ) {
		
			for ( MultipartFile file : files) {
				byte[] fileData = file.getBytes();
				
				UUID uid = UUID.randomUUID(); // 랜덤한 문자열을 생성해주는 객체
				String fileName = file.getOriginalFilename(); // file의 실제 파일 명
				
				// 파일명 중복 방지를 위해 uid객체에 '_' 언더바와 파일이름 붙여서 고유의 파일명을 생성해줌 
				fileName = uid.toString() + "_" + fileName;
				
				// 스프링에서 파일을 직접 복사하는 기능을 제공해 주는데 그걸 사용한다.
				File uploadFile = new File (uploadPath, fileName);
				
				// 스프링 파일 복사 유틸
				FileCopyUtils.copy(fileData, uploadFile);
							// --> 여기까지 하게 되면 insert()메소드 실행 시 uploadPath 안에 업로드한 파일이 저장됨
				
				// 이제 DB와 통신을 해야 함
				Files uploadedFile = new Files();
				uploadedFile.setFileName(fileName);
				uploadedFile.setFilePath(uploadPath+fileName);
				uploadedFile.setParentTable("board");
				
				fileMapper.insert(uploadedFile);
				// 그리고 매퍼에는 uploadedFile을 던져준다.
			}
		} 
		 
		return result;
	  
	}

	// 게시글 1개 조회
	@Override
	public Board read(int boardNo) throws Exception {
		return boardMapper.read(boardNo);
	}

	// 게시글 수정 처리
	@Override
	public int update(Board board) throws Exception {
		return boardMapper.update(board);
	}

	// 게시글 삭제 처리
	@Override
	public int delete(int boardNo) throws Exception {
		
		// 첨부된 파일 목록 조회
		Files file = new Files();
		file.setParentTable("board");
		file.setParentNo(boardNo);
		List<Files> fileList = fileMapper.listByParentNo(file);
		

		// 실제 파일 파일 목록 삭제
		for (Files deleteFile : fileList) {
			
			String filePath = deleteFile.getFilePath();
			int fileNo = deleteFile.getFileNo();
			log.info("삭제 파일 번호 : " + fileNo);
			
			// 실제 파일 삭제
			boolean result = fileUtil.delete(filePath);
			
			if( result ) {
				// DB파일 정보 삭제
				fileMapper.delete(fileNo);
			}
			else {
				log.info("파일 삭제 시, 문제가 발생하였습니다.");
			}
		}
		
		return boardMapper.delete(boardNo);
	}

	
	// 페이징 처리 (게시글 목록 10개씩 조회)
	@Override
	public List<Board> list(Page page) throws Exception {
		
		// totalCount 조회
		int totalCount = boardMapper.count();
		log.info("totalCount : " + totalCount);
		
		// Page 객체에 totalCount 세팅
		page.setTotalCount(totalCount);
		
		// 페이징 처리하여 게시글 목록 조회
		List <Board> boardList = boardMapper.listWithPage(page);
		
		// 지금 boardMapper에 추가해야 할 것은 count와 listWithPage이다.
		
		
		return boardList;
	}

}
