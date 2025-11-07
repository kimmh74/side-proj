package com.kmh.kamco.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class UpdateRequest {
    private Long id; // 사용자를 식별할 ID (현재는 username 기반이지만, 안전하게 ID로 하는 것이 좋음)
    private String username; // 현재 사용자의 username (변경 방지 또는 검증용)
    private String newPassword; // 변경할 새 비밀번호
    private String email;
    private String name;
}