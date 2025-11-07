package com.kmh.kamco.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String message;
    private String username; // 로그인 성공 시 사용자 이름 반환 (선택 사항)
    // JWT 토큰 등을 여기에 추가할 수 있습니다.
}
