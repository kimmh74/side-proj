// src/main/java/com/kmh/kamco/controller/KamcoController.java
package com.kmh.kamco.controller;

import com.kmh.kamco.dto.KamcoApiResponse;
import com.kmh.kamco.dto.Item;
import com.kmh.kamco.dto.SearchParams;
import com.kmh.kamco.service.KamcoApiService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class KamcoController {

    private final KamcoApiService kamcoApiService;

    // UI에서 한 번에 보여줄 아이템 수 (HTML 페이지 당)
    private static final int UI_PAGE_SIZE = 50;
    // KAMCO API 한 번 호출로 받아올 아이템 수
    private static final int API_NUM_OF_ROWS = 100;
    // UI 페이지 그룹 당 보여줄 페이지 번호 개수 (예: 1 2 3 4 5)
    private static final int PAGE_GROUP_SIZE = 5;

    // UI 페이지 하나를 커버하기 위해 필요한 API 호출 횟수 (100 / 50 = 2)
    private static final int API_CALLS_PER_UI_PAGE_GROUP = API_NUM_OF_ROWS / UI_PAGE_SIZE;


    public KamcoController(KamcoApiService kamcoApiService) {
        this.kamcoApiService = kamcoApiService;
    }

    @GetMapping("/")
    public String index(Model model) {
        return "redirect:/auction";
    }

    @GetMapping("/auction")
    public String getAuctionList(
            @RequestParam(value = "pageNo", defaultValue = "1") int uiPageNo, // UI 상의 현재 페이지 번호
            @ModelAttribute SearchParams searchParams, // 검색 파라미터를 SearchParams DTO로 받습니다.
            Model model) {

        List<Item> displayedItems = new ArrayList<>();
        
        // 검색이 들어오면 페이지를 1로 초기화 (새로운 검색 결과는 첫 페이지부터 시작)
        // 단, 이미 검색 중일 때 페이지네이션 이동은 그대로 페이지 번호를 유지해야 함
        if (searchParams.isAnyFieldPresent() && uiPageNo == 1) { // 검색어가 있고 첫 페이지를 요청한 경우
             // uiPageNo = 1; // @RequestParam defaultValue로 이미 1이므로 명시적으로 초기화는 필요 없을 수 있습니다.
        }

        // API 호출을 위한 페이지 번호 계산
        int apiPageNo = ((uiPageNo - 1) / API_CALLS_PER_UI_PAGE_GROUP) + 1;
        
        // KamcoApiService 호출 (검색 파라미터는 API로 전달하지 않음)
        KamcoApiResponse response = kamcoApiService.getAuctionItems(String.valueOf(apiPageNo), String.valueOf(API_NUM_OF_ROWS));

        boolean hasNextPage = false;
        List<Item> fullyFilteredItems = new ArrayList<>(); // RNUM 중복 제거 & 검색 조건 필터링 완료된 아이템 리스트

        if (response != null && response.getHeader() != null) {
            if ("00".equals(response.getHeader().getResultCode())) {
                if (response.getBody() != null && response.getBody().getItemList() != null && !response.getBody().getItemList().isEmpty()) {
                    Map<String, Item> itemMapByRNUM = new LinkedHashMap<>();
                    
                    for (Item item : response.getBody().getItemList()) {
                        if (item != null && item.getRNUM() != null && !item.getRNUM().isEmpty()) {
                            itemMapByRNUM.putIfAbsent(item.getRNUM(), item); // RNUM 중복 제거
                        }
                    }

                    List<Item> deduplicatedItems = new ArrayList<>(itemMapByRNUM.values());

                    // --- 2차 필터링: 검색 조건(contains) 적용 ---
                    fullyFilteredItems = deduplicatedItems.stream()
                        .filter(item -> {
                            boolean match = true;
                            // 각 검색 필드에 대해 "contains" 조건 적용 (대소문자 구분 없이)
                            if (searchParams.getCltrNm() != null && !searchParams.getCltrNm().isEmpty()) {
                                if (item.getCLTR_NM() == null || !item.getCLTR_NM().toLowerCase().contains(searchParams.getCltrNm().toLowerCase())) match = false;
                            }
                            if (searchParams.getLdnmAdrs() != null && !searchParams.getLdnmAdrs().isEmpty()) {
                                if (item.getLDNM_ADRS() == null || !item.getLDNM_ADRS().toLowerCase().contains(searchParams.getLdnmAdrs().toLowerCase())) match = false;
                            }
                            if (searchParams.getCtgrFullNm() != null && !searchParams.getCtgrFullNm().isEmpty()) {
                                if (item.getCTGR_FULL_NM() == null || !item.getCTGR_FULL_NM().toLowerCase().contains(searchParams.getCtgrFullNm().toLowerCase())) match = false;
                            }
                            if (searchParams.getCltrMnmtNo() != null && !searchParams.getCltrMnmtNo().isEmpty()) {
                                if (item.getCLTR_MNMT_NO() == null || !item.getCLTR_MNMT_NO().toLowerCase().contains(searchParams.getCltrMnmtNo().toLowerCase())) match = false;
                            }
                            if (searchParams.getDpslMtdNm() != null && !searchParams.getDpslMtdNm().isEmpty()) {
                                if (item.getDPSL_MTD_NM() == null || !item.getDPSL_MTD_NM().toLowerCase().contains(searchParams.getDpslMtdNm().toLowerCase())) match = false;
                            }
                            // 숫자나 날짜 필드는 'contains' 대신 'equals'나 'startsWith'가 더 자연스러울 수 있으나,
                            // 'contains' 요청에 맞춰 동일하게 처리합니다.
                            if (searchParams.getMinBidPrc() != null && !searchParams.getMinBidPrc().isEmpty()) {
                                if (item.getMIN_BID_PRC() == null || !item.getMIN_BID_PRC().toLowerCase().contains(searchParams.getMinBidPrc().toLowerCase())) match = false;
                            }
                            if (searchParams.getApslAsesAvgAmt() != null && !searchParams.getApslAsesAvgAmt().isEmpty()) {
                                if (item.getAPSL_ASES_AVG_AMT() == null || !item.getAPSL_ASES_AVG_AMT().toLowerCase().contains(searchParams.getApslAsesAvgAmt().toLowerCase())) match = false;
                            }
                            if (searchParams.getPbctBegnDtm() != null && !searchParams.getPbctBegnDtm().isEmpty()) {
                                if (item.getPBCT_BEGN_DTM() == null || !item.getPBCT_BEGN_DTM().toLowerCase().contains(searchParams.getPbctBegnDtm().toLowerCase())) match = false;
                            }
                            if (searchParams.getPbctCltrStatNm() != null && !searchParams.getPbctCltrStatNm().isEmpty()) {
                                if (item.getPBCT_CLTR_STAT_NM() == null || !item.getPBCT_CLTR_STAT_NM().toLowerCase().contains(searchParams.getPbctCltrStatNm().toLowerCase())) match = false;
                            }
                            if (searchParams.getGoodsNm() != null && !searchParams.getGoodsNm().isEmpty()) {
                                if (item.getGOODS_NM() == null || !item.getGOODS_NM().toLowerCase().contains(searchParams.getGoodsNm().toLowerCase())) match = false;
                            }
                            return match;
                        })
                        .collect(Collectors.toList());

                    // --- UI 페이지네이션 적용 (필터링된 결과에 대해) ---
                    // 현재 API 응답 내에서 UI 페이지를 위한 시작 인덱스와 끝 인덱스 계산
                    int startIndexForUI = ((uiPageNo - 1) % API_CALLS_PER_UI_PAGE_GROUP) * UI_PAGE_SIZE; // api 응답 내에서 시작
                    int endIndexForUI = startIndexForUI + UI_PAGE_SIZE;

                    if (startIndexForUI < fullyFilteredItems.size()) {
                        int actualEndIndex = Math.min(endIndexForUI, fullyFilteredItems.size());
                        displayedItems = fullyFilteredItems.subList(startIndexForUI, actualEndIndex);
                        
                        // 다음 페이지 존재 여부 판단 (추정)
                        // 현재 표시된 아이템의 수가 UI_PAGE_SIZE와 같으면 다음 페이지가 있을 것으로 추정
                        hasNextPage = displayedItems.size() == UI_PAGE_SIZE;
                    } else {
                        displayedItems = new ArrayList<>();
                        hasNextPage = false;
                    }

                    model.addAttribute("items", displayedItems);
                } else {
                    model.addAttribute("message", "조회된 공매 물건이 없습니다.");
                    hasNextPage = false;
                }
            } else if ("99".equals(response.getHeader().getResultCode())) {
                model.addAttribute("message", response.getHeader().getResultMsg());
                hasNextPage = false;
            } else {
                model.addAttribute("message", "API 호출 실패: " + response.getHeader().getResultMsg());
                hasNextPage = false;
            }
        } else {
            model.addAttribute("message", "API 호출 실패: 응답을 받지 못했습니다. (네트워크 문제 또는 예상치 못한 서비스 오류)");
            hasNextPage = false;
        }

        // 페이지네이션 그룹 계산
        int startPage = ((uiPageNo - 1) / PAGE_GROUP_SIZE) * PAGE_GROUP_SIZE + 1;
        int endPage = startPage + PAGE_GROUP_SIZE - 1;

        model.addAttribute("currentPage", uiPageNo);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("hasNextPage", hasNextPage);
        model.addAttribute("searchParams", searchParams); // 검색 파라미터를 모델에 추가하여 검색 폼에 유지하고 페이지네이션 링크에도 사용
        
        return "auctionList";
    }
}