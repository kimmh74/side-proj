package com.kmh.kamco.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration // 이 클래스가 스프링 설정 클래스임을 명시합니다.
public class AppConfig {

    @Bean // 이 메서드가 반환하는 객체를 스프링 빈으로 등록하겠다는 의미입니다.
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
