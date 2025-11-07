package com.kmh.kamco.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.Collections;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // CSRF 보호 비활성화
            .authorizeHttpRequests(authorize -> authorize
                // '/favicon.ico' 경로도 인증 없이 접근 가능하도록 추가
                // 또한, '/' 경로와 모든 정적 자원도 허용 (프론트엔드가 백엔드에 직접 제공될 때 필요)
                .requestMatchers("/", "/index.html", "/static/**", "/*.ico", "/*.png", "/*.js", "/*.css").permitAll()
                .requestMatchers("/favicon.ico").permitAll() // <<< 이 줄을 명시적으로 추가합니다.
                .requestMatchers("/auction/**", "/api/auth/**").permitAll() // API 인증 경로 허용
                .anyRequest().authenticated() // 그 외 모든 요청은 인증 필요
            );
        // .httpBasic(httpBasic -> {}); // HTTP Basic 인증은 이제 필요하지 않을 수 있습니다.
        return http.build();
    }

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOrigins(Collections.singletonList("http://localhost:5173")); // React 개발 서버 주소
        config.setAllowedHeaders(Arrays.asList("Origin", "Content-Type", "Accept", "Authorization"));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}