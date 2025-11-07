// D:\s11_proj\01_sideProj\kamco\src\main\java\com\kmh\kamco\model\User.java

package com.kmh.kamco.model;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

// ★★★ JPA 어노테이션(@Entity, @Table, @Id, @GeneratedValue, @Column 등)을 모두 제거합니다! ★★★
// User 클래스는 순수한 데이터 모델 (POJO) 역할만 해야 합니다.
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class User {
    private Long id;
    private String username;
    private String password;
    private String email; // 추가
    private String name;  // 추가
    private LocalDateTime createdAt = LocalDateTime.now(); // 생성 시점에 현재 시간으로 기본값 설정
}