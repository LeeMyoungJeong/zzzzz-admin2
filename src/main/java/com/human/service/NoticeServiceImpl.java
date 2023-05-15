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
import com.human.domain.Notice;
import com.human.domain.Page;
import com.human.mapper.FileMapper;
import com.human.mapper.NoticeFileMapper;
import com.human.mapper.NoticeMapper;
import com.human.utils.FileUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class NoticeServiceImpl implements NoticeService{

	////////////////////////////////////////////////////
	
	// 업로드 경로
		@Value("${upload.path}")
		private String uploadPath;
		// # application.properties에 정의한 업로드 경로 정보
		// upload.path=C:/KHM/upload/
	
		@Autowired
		private NoticeMapper noticeMapper;
		
		@Autowired
		private NoticeFileMapper fileMapper;
	
		@Autowired
		private FileUtil fileUtil;
	
	
	////////////////////////////////////////////////////
	
	// 페이징 처리 ( 공지사항 목록 10개씩 조회 )
	@Override
	public List<Notice> list(Page page) throws Exception {
		// totalCount 조회
		int totalCount = noticeMapper.count();
		log.info("totalCount : " + totalCount);
				
		// Page 객체에 totalCount 세팅
		page.setTotalCount(totalCount);
				
		// 페이징 처리하여 게시글 목록 조회
		List <Notice> noticeList = noticeMapper.listWithPage(page);
				
		// 지금 boardMapper에 추가해야 할 것은 count와 listWithPage이다.
				
				
		return noticeList;
		
	}
	
	
	
	
	@Override
	public int insert(Notice notice) throws Exception {
		
		// 공지사항 등록
		int result =  noticeMapper.insert(notice);
		
		// 만약 게시글 등록이 되지 않으면 파일을 업로드 할 필요도 없으니 파일 업로드 과정을 생략하고 그대로 return한다.
		if (result == 0 ) return result;
		
		// Board 객체에 업로드된 files들을 get()해서 MultiparFile[] 배열에 저장한다.
		MultipartFile[] files = notice.getFiles();
		
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
				uploadedFile.setParentTable("notice");
				
				fileMapper.insert(uploadedFile);
				// 그리고 매퍼에는 uploadedFile을 던져준다.
			}
		
		return result;
		
	}



	// 공지사항 읽기
	@Override
	public Notice read(int noticeNo) throws Exception {
		return noticeMapper.read(noticeNo);
		
	}

	// 공지사항 수정 - 화면 Get
	@Override
	public Notice updateForm(int noticeNo) throws Exception {
		return noticeMapper.read(noticeNo);
		
	}
	
	
	
	// 공지사항 수정 처리
	@Override
	public int update(Notice notice) throws Exception {
		// 공지사항 등록
		int result =  noticeMapper.update(notice);
		
		// 만약 게시글 등록이 되지 않으면 파일을 업로드 할 필요도 없으니 파일 업로드 과정을 생략하고 그대로 return한다.
		if (result == 0 ) return result;
		
		// Board 객체에 업로드된 files들을 get()해서 MultiparFile[] 배열에 저장한다.
		MultipartFile[] files = notice.getFiles();
		
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
				uploadedFile.setParentTable("notice");
				
				fileMapper.insert(uploadedFile);
				// 그리고 매퍼에는 uploadedFile을 던져준다.
			}
		
		return result;
		
	}



	// 공지사항 삭제 처리
	@Override
	public int delete(int noticeNo) throws Exception {
		// 첨부된 파일 목록 조회
		Files file = new Files();
		file.setParentTable("board");
		file.setParentNo(noticeNo);
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
		
		return noticeMapper.delete(noticeNo);
	}
		
		
	
	
		
	}


