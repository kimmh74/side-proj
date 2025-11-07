package com.kmh.kamco.dto;

import lombok.Data; // @Getter, @Setter, @ToString, @EqualsAndHashCode를 포함

@Data
public class LoginRequest {
    private String username;
    private String password;
}