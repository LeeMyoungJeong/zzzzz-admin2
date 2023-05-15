package com.human.controller;

import java.io.File;
import java.io.FileInputStream;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.human.domain.Files;
import com.human.service.FileService;
import com.human.utils.MediaUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/file") // 여기에 클래스 단에서 RequestMapping이 붙는 이유를 잘 모르겠네... 질문하러 가야겠노...
public class FileController {

	/////////////  의존주입  /////////////////////////////////////////////////
	@Autowired
	private FileService fileService;
	
	
	////////////////////////////////////////////////////////////////////////
	
	// read.html에서 파일 이름이 value로 할당된 a태그를 불러오려면 아래의 GetMapping이 필욯마
	@GetMapping("/{fileNo}")
	public void fileDownload(@PathVariable("fileNo") int fileNo 
							, HttpServletResponse response) throws Exception {
		
		log.info("파일 다운..");
		
	}
					
	
	@GetMapping("/img")
	public void showImg(String filePath
						, HttpServletResponse response ) throws Exception {
		
		log.info("filePath", filePath);
		
		File file = new File(filePath);
		FileInputStream fis = new FileInputStream(file);
		ServletOutputStream sos = response.getOutputStream();
		FileCopyUtils.copy(fis, sos);
		
		String ext = filePath.substring(filePath.lastIndexOf(".") + 1);
		MediaType mType = MediaUtil.getMediaType(ext);
		
		// 이미지타입이 아닐 경우 컨텐츠타입을 따로 지정하지 않음
		if (mType == null ) {
			return;
		}
		
		response.setContentType( mType.toString() );
		
	}
	
	// 내친김에 파일 삭제까지 구현해보자
	@PostMapping("/delete") 
	public ResponseEntity<String> fileDelete(@RequestBody Files file) throws Exception {
		
		int fileNo = file.getFileNo();
		
		log.info("파일삭제큣....");
		log.info("fileNo : " + file.getFileNo());
		
		int result = fileService.delete(fileNo);
		
		// 파일 삭제 요청
		return new ResponseEntity<String>("SUCCESS", HttpStatus.OK);
		
	}
	
}
