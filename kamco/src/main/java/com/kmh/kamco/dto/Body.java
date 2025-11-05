package com.kmh.kamco.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import java.util.List;

public class Body {
    // API 문서에 따라 item이 list 형태로 올 경우
    @JacksonXmlElementWrapper(localName = "items") // <items> 태그로 묶여있다면 사용
    @JacksonXmlProperty(localName = "item") // 각 개별 아이템 태그 이름
    private List<Item> itemList;

    // 기타 페이징 정보 등이 있다면 여기에 추가

    public List<Item> getItemList() {
        return itemList;
    }

    public void setItemList(List<Item> itemList) {
        this.itemList = itemList;
    }

    @Override
    public String toString() {
        return "Body{" +
                "itemList=" + itemList +
                '}';
    }
}