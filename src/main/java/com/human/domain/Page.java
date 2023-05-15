package com.human.domain;

import lombok.Data;

@Data
public class Page {

	private static final int PAGE_NO = 1;				// 현재 번호 -> 기본값
	private static final int ROWS_PER_PAGE = 10; 		// 페이지당 행의 개수 -> 기본값
	private static final int PAGE_COUNT = 10;			// 한 화면에 보여줄 페이지의 개수 1,2,3,...10 

	private int pageNo; 								// 현재 번호
	private int rowsPerPage;							// 페이지 당 행의 개수
	private int pageCount;								// 노출 페이지 개수
	private int totalCount;								// 전체 데이터 개수
														// 전체 데이터의 개수는 데이터베이스에서 조회해서 가져와줘야 함
	
	private int startPage;								// 시작 번호
	private int endPage;								// 끝 번호
	
	private int firstPage;								// 첫 번호
	private int lastPage;								// 마지막 번호
	
	private int prev;									// 이전 번호
	private int next;									// 다음 번호
	
	private int startRowNo;								// 시작 행 번호
	private int endRowNo;								// 끝 행 번호
	
	
	// 생성자
	public Page() { // 매개변수가 주어지지 않고 만들어진 페이지(기본페이지)
		this( PAGE_NO, ROWS_PER_PAGE, PAGE_COUNT, 0);  // 이거의 의도가 궁금하노....
	}
	
	public Page( int pageNo, int rowsPerPage, int pageCount, int totalCount) {
		this.pageNo = pageNo;
		this.rowsPerPage = rowsPerPage;
		this.pageCount = pageCount;
		this.totalCount = totalCount;
		paging();
	}
	
	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
		paging();		// 페이징 처리
	}
	
	// 페이징 처리 수식 메소드
	public void paging() {
		
		// 이전페이지 = 현재페이지 - 1
		this.prev = pageNo - 1;
		
		// 다음페이지 = 현재페이지 + 1
		this.next = pageNo + 1;
		
		// 첫번째 페이지 = 1
		this.firstPage = 1;
		
		// 마지막 페이지 = ( (게시물 총갯수 - 1) / 페이지 갯수(10) )  + 1
		this.lastPage = (totalCount - 1) / rowsPerPage + 1;
		
		// 시작페이지 = ( ( (페이지번호-1) / 노출페이지갯수(10) ) * 노출페이지 갯수(10) ) + 1
		this.startPage = ( (pageNo - 1) / pageCount ) * pageCount + 1;
		
		// 끝페이지 = ( ( (페이지번호 - 1) / 페이지갯수(10) ) + 1 ) * 노출페이지갯수
		this.endPage = ( ( (pageNo - 1) / pageCount ) + 1 ) * pageCount;
		
		
		// 끝 페이지가 마지막 페이지보다 클 때,
		// 실제 데이터 개수를 반영한 끝 페이지로 보정
		
		
		// 이렇게 하면 게시글이 100개 이하일 때 페이지 수가 기본 1~10이던 것이 
		// 10개 이하면 1, 11개 이상이면 2, 21개 이상일때 3개만 형성된다
		if(this.lastPage < 10) {
			this.endPage = (totalCount / rowsPerPage) + 1;
		} else if ( this.lastPage > 1 && this.endPage > this.lastPage) {
			this.endPage = this.lastPage;
		}
		
		
		
		// 첫 행 번호 
		this.startRowNo = ( this.pageNo - 1 ) * this.rowsPerPage + 1;
		
		// 끝 행 번호
		this.endRowNo = this.pageNo * this.rowsPerPage;
	
	}
	
}
