// D:\s11_proj\01_sideProj\kamco\src\main\java\com\kmh\kamco\model\Data.java

package com.kmh.kamco.model;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class Data {
    private Integer rnum; // 순번
    private String plnmNo; // 공고번호
    private String pbctCdtnNo; // 공고조건번호
    private String cltrNo; // 물건관리번호
    private String cltrHstrNo; // 물건이력번호
    private String ctgrFullNm; // 카테고리명
    private String bidMnmtNo; // 입찰물건관리번호
    private String cltrNm; // 물건이름
    private String cltrMnmtNo; // 물건관리번호 (요청대로 중복 필드 추가)
    private String ldnmAdrs; // 지번주소
    private String nmrdAdrs; // 도로명주소
    private String ldnmPnu; // 지번PNU
    private String dpslMtdCd; // 처분방식코드
    private String dpslMtdNm; // 처분방식명
    private BigDecimal minBidPrc; // 최저입찰가격
    private BigDecimal apslAsesAvgAmt; // 감정평가평균액
    private BigDecimal feeRate; // 수수료율
    private LocalDateTime pbctBegnDtm; // 공고개시일시
    private LocalDateTime pbctClsDtm; // 공고마감일시
    private String pbctCltrStatNm; // 공매물건상태명
    private Integer uscbdCnt; // 이용된 입찰건수
    private Integer iqryCnt; // 조회수
    private String goodsNm; // 상품명
    private LocalDateTime createdAt = LocalDateTime.now(); // 내부 사용 목적의 생성일시
}