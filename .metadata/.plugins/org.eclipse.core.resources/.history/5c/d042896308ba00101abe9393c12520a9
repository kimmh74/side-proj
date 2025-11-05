package com.kmh.kamco.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(localName = "response")
public class KamcoApiResponse {

    @JacksonXmlProperty(localName = "header")
    private Header header;

    // 실제 공매물건 데이터가 들어갈 Body 클래스 (API 문서 참고하여 상세 필드 추가)
    @JacksonXmlProperty(localName = "body")
    private Body body; // 이 부분은 KAMCO API 응답 구조에 따라 변경될 수 있습니다.

    // Getters and Setters
    public Header getHeader() {
        return header;
    }

    public void setHeader(Header header) {
        this.header = header;
    }

    public Body getBody() {
        return body;
    }

    public void setBody(Body body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return "KamcoApiResponse{" +
                "header=" + header +
                ", body=" + body +
                '}';
    }
}