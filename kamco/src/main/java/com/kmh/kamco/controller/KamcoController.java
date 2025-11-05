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
    private static final int API_NUM_OF_ROWS = 100; // API가 최대로 100건을 줄 수 있음
    // UI 페이지 그룹 당 보여줄 페이지 번호 개수 (예: 1 2 3 4 5)
    private static final int PAGE_GROUP_SIZE = 5;

    // UI 페이지 하나를 커버하기 위해 필요한 API 호출 횟수 (API_NUM_OF_ROWS / UI_PAGE_SIZE)
    private static final int API_UI_PAGE_MULTIPLIER = API_NUM_OF_ROWS / UI_PAGE_SIZE; // 100 / 50 = 2

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
        
        // 검색이 실행되면 uiPageNo를 1로 초기화하여 검색 결과의 첫 페이지부터 시작하도록 유도합니다.
        // 페이지네이션 클릭 시에는 기존 uiPageNo 유지.
        int currentPageNumber = uiPageNo;
        if (searchParams.isAnyFieldPresent() && uiPageNo == 1 && model.getAttribute("isSearch") == null) {
            // isSearch 속성으로 첫 검색임을 명확히 합니다.
            model.addAttribute("isSearch", true); 
        }

        // API 호출을 위한 페이지 번호 계산
        // uiPageNo 1,2 -> apiPageNo 1 (API_UI_PAGE_MULTIPLIER = 2이므로)
        // uiPageNo 3,4 -> apiPageNo 2
        int apiPageNo = ((currentPageNumber - 1) / API_UI_PAGE_MULTIPLIER) + 1;
        
        KamcoApiResponse response = kamcoApiService.getAuctionItems(
                String.valueOf(apiPageNo),
                String.valueOf(API_NUM_OF_ROWS) // 항상 API_NUM_OF_ROWS (100) 만큼 요청
        );

        boolean hasNextPage = false;
        List<Item> fullyFilteredItems = new ArrayList<>();

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

                    // 현재 API 호출 범위 내에서 보여줄 UI 페이지의 시작/끝 인덱스 계산
                    int startIndexForUiPage = ((currentPageNumber - 1) % API_UI_PAGE_MULTIPLIER) * UI_PAGE_SIZE;
                    int endIndexForUiPage = startIndexForUiPage + UI_PAGE_SIZE;

                    if (startIndexForUiPage < fullyFilteredItems.size()) {
                        int actualEndIndex = Math.min(endIndexForUiPage, fullyFilteredItems.size());
                        displayedItems = fullyFilteredItems.subList(startIndexForUiPage, actualEndIndex);

                        // 다음 페이지 존재 여부 판단 (좀 더 정확하게 추정)
                        // 1. 현재 UI 페이지가 꽉 찼고
                        // 2. 현재 API 응답 내에 다음 UI 페이지를 만들 수 있는 데이터가 더 있거나 (endIndexForUiPage < fullyFilteredItems.size())
                        // 3. 또는, 현재 UI 페이지가 API 응답의 마지막 UI 페이지 (예: 2페이지)이면서 API 응답 100건이 모두 차 있다면 다음 API 페이지가 있을 수 있음.
                        hasNextPage = displayedItems.size() == UI_PAGE_SIZE && // 현재 페이지가 꽉 찼고
                                (endIndexForUiPage < fullyFilteredItems.size() || // 현재 API 응답에 아직 데이터가 남았거나
                                 (fullyFilteredItems.size() == API_NUM_OF_ROWS && // 현재 API 응답이 최대치 100건이고
                                  (currentPageNumber % API_UI_PAGE_MULTIPLIER == 0 || API_UI_PAGE_MULTIPLIER == 1))); // 현재 UI 페이지가 해당 API 응답의 마지막 UI 페이지일 때
                                // 위에 (currentPageNumber % API_UI_PAGE_MULTIPLIER == 0) 조건은 UI 2,4,6 페이지일 때 다음 API 요청이 필요하단 의미.
                    } else {
                        // 현재 UI 페이지에 보여줄 데이터가 없으면, 다음 페이지도 없음.
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

        // --- 페이지네이션 그룹 계산 로직 수정 ---
        int startPage = ((currentPageNumber - 1) / PAGE_GROUP_SIZE) * PAGE_GROUP_SIZE + 1;
        // endPage는 PAGE_GROUP_SIZE 기준으로 일단 계산하되, 실제 nextData 유무에 따라 조정
        int endPage = startPage + PAGE_GROUP_SIZE - 1;
        
        // 마지막 페이지 그룹에서 존재하지 않는 페이지 번호를 보여주지 않기 위해 조정
        // 이 부분은 총 페이지 수를 알 수 없으므로 다음 페이지 존재 여부로 간접적으로만 조정 가능.
        // 현재 로직에서 hasNextPage가 false라면 endPage는 현재 페이지를 넘어가지 않도록 하는 것이 합리적.
        // 단, 이는 "5이하는 있는 페이지만 보여줘" 의 모든 케이스를 커버하진 못함. (총 페이지를 알 수 없기 때문)
        // 가장 안전한 방법은 현재 그룹에 있는 페이지 중, currentPage + (현재 표시된 item 수 < UI_PAGE_SIZE) 인 경우의 페이지까지만 표시.
        if(!hasNextPage && currentPageNumber < endPage) {
            endPage = currentPageNumber; // 다음 페이지가 없으면 현재 페이지가 해당 그룹의 마지막일 수 있음
        }


        model.addAttribute("currentPage", currentPageNumber);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("hasNextPage", hasNextPage); // 다음 버튼 활성화 여부
        model.addAttribute("searchParams", searchParams);
        
        return "auctionList";
    }
}