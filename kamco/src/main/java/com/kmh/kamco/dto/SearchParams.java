// src/main/java/com/kmh/kamco/dto/SearchParams.java
package com.kmh.kamco.dto;

import lombok.Data;
import org.springframework.util.StringUtils; // Spring의 유틸리티 클래스 임포트

@Data
public class SearchParams {
    private String cltrNm;
    private String ldnmAdrs;
    private String ctgrFullNm;
    private String cltrMnmtNo;
    private String dpslMtdNm;
    private String minBidPrc;
    private String apslAsesAvgAmt;
    private String pbctBegnDtm;
    private String pbctCltrStatNm;
    private String goodsNm;

    // 검색 파라미터 중 하나라도 값이 있으면 true를 반환하는 유틸리티 메서드
    public boolean isAnyFieldPresent() {
        return !StringUtils.isEmpty(cltrNm) ||
               !StringUtils.isEmpty(ldnmAdrs) ||
               !StringUtils.isEmpty(ctgrFullNm) ||
               !StringUtils.isEmpty(cltrMnmtNo) ||
               !StringUtils.isEmpty(dpslMtdNm) ||
               !StringUtils.isEmpty(minBidPrc) ||
               !StringUtils.isEmpty(apslAsesAvgAmt) ||
               !StringUtils.isEmpty(pbctBegnDtm) ||
               !StringUtils.isEmpty(pbctCltrStatNm) ||
               !StringUtils.isEmpty(goodsNm);
    }
}