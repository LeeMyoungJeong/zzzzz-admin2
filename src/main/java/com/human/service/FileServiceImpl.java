package com.human.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.human.domain.Files;
import com.human.mapper.FileMapper;
import com.human.utils.FileUtil;

@Service
public class FileServiceImpl implements FileService {

	////////////의존주입  /////////////////////////////////////////
	
	@Autowired
	private FileMapper fileMapper;
	
	@Autowired
	private FileUtil fileUtil;
	
	//////////////////////////////////////////////////////////////

	// 파일 등록
	@Override
	public int insert(Files file) throws Exception {
		return fileMapper.insert(file);
	}
	
	// 파일 목록
	@Override
	public List<Files> listByParentNo(Files file) throws Exception {
		return fileMapper.listByParentNo(file);
	}

	// 파일 조회
	public Files read(int fileNo) throws Exception {
		return fileMapper.read(fileNo);
	}
	
	
	// 파일 삭제
	@Override
	public int delete(int fileNo) throws Exception {

		// 실제 파일 삭제
		Files file = fileMapper.read(fileNo);
		
		String filePath = file.getFilePath();
		
		boolean result = fileUtil.delete(filePath);
		
		if (result ) {
			return fileMapper.delete(fileNo);
		}
		
		return 0;
	}

	

	
	
}
