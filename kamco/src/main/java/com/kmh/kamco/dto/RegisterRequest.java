package com.kmh.kamco.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class RegisterRequest {
    private String username;
    private String password;
    private String email; // ★★★ email 필드 추가 ★★★
    private String name;  // ★★★ name 필드 추가 ★★★
}