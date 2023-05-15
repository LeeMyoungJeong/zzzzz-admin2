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
import com.human.domain.Qna;
import com.human.mapper.FileMapper;
import com.human.mapper.QnaMapper;
import com.human.utils.FileUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class QnaServiceImpl implements QnaService{
	
	//////////////////////////////////////////////////////////////
	// 업로드 경로
	@Value("${upload.path}")			// application.properties 의 속성값을 지정
	private String uploadPath; 
	
	@Autowired
	private FileMapper fileMapper;	
	
	@Autowired
	private QnaMapper qnaMapper;
	
	@Autowired
	private FileUtil fileUtil;

	//////////////////////////////////////////////////////////////

	// 질의응답 등록
	@Override
	public int insert(Qna qna) throws Exception {
			
		int result = qnaMapper.insert(qna);
	
		if( result == 0 ) return result;		// 게시글 등록이 안 되면, 파일도 업로드X

		// 실제 파일을 업로드 경로에 복사
		MultipartFile[] files = qna.getFiles();
		
		for (MultipartFile file : files) {
			byte[] fileData = file.getBytes();			// 파일 데이터
			
			if(file.isEmpty()) continue;	// 없는 파일은 패스 
 			
			// 파일명 중복 방지
			// 고유한 랜덤 문자열을 생성하는 객체
			UUID uid = UUID.randomUUID();
			String fileName = file.getOriginalFilename(); 	// 실제 파일 명
			// (UID)_(실제파일명)	- ex) UID_강아지.png
			fileName = uid.toString() + "_" + fileName;

			// 업로드할 파일
			File uploadFile = new File( uploadPath, fileName );		// 파일경로, 파일명
			
			// 스프링 파일 복사 유틸
			FileCopyUtils.copy(fileData, uploadFile);
			
			// 아래는 FileCopyUtils 로 대체
//			FileOutputStream fos = new FileOutputStream( uploadFile );
//			fos.write(fileData);
//			fos.close();

			
			// 업로드된 파일 정보를 DB 에 등록
			// 업로드된 파일 정보
			Files uploadedFile = new Files();
			uploadedFile.setFileName(fileName);
			// C:/KHM/upload/ + UID_강아지.png
			uploadedFile.setFilePath(uploadPath + fileName);
			uploadedFile.setParentTable("qna");
			// parentNo(boardNo) 는 게시글 등록 시, 증가한 시퀀스 번호로 세팅
			
			fileMapper.insert(uploadedFile);
			
		}
		
		return result;
	}

	// 질의응답 읽기
	@Override
	public Qna read(int qnaNo) throws Exception {
		return qnaMapper.read(qnaNo);
	}

	// 질의응답 수정
	@Override
	public int update(Qna qna) throws Exception {
		return qnaMapper.update(qna);
	}
	
	// 질의응답 삭제
	@Override
	public int delete(int qnaNo) throws Exception {

		// 첨부된 파일 목록 조회
		Files file = new Files();
		file.setParentTable("qna");
		file.setParentNo(qnaNo);
		List<Files> fileList = fileMapper.listByParentNo( file );
		
		// 파일 목록 삭제
		for (Files deleteFile : fileList) {
			
			String filePath = deleteFile.getFilePath();
			int fileNo = deleteFile.getFileNo();
			log.info("삭제 파일 번호 : " + fileNo);
			
			// 실제 파일 삭제
			boolean result = fileUtil.delete(filePath);
			
			if( result ) {
				// DB 파일 정보 삭제
				fileMapper.delete(fileNo);
			}
			// 파일 삭제 시, 문제 발생
			else {
				log.info("파일 삭제 시, 문제가 발생하였습니다.");
			}
		}
		
		return qnaMapper.delete(qnaNo);		
	
	}

	@Override
	public List<Qna> list() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Qna> list(Page page) throws Exception {
		// totalCount 조회
		int totalCount = qnaMapper.count();
		log.info("totalCount : " + totalCount);
		
		// Page 객체에 totalCount 세팅
		page.setTotalCount(totalCount);
		
		// 페이징 처리하여 게시글 목록 조회
		List<Qna> qnaList = qnaMapper.listWithPage(page);
		
		
		return qnaList;
	}		
		
	
	

	
	
}
