package com.kmh.kamco.model;

import jakarta.persistence.*;
import lombok.*; // Lombok 어노테이션 사용

import java.time.LocalDateTime;

import org.springframework.boot.autoconfigure.domain.EntityScan;

@Entity
@Table(name = "userdb") // 매핑할 테이블 이름
@Getter
@Setter
@NoArgsConstructor // 기본 생성자
@AllArgsConstructor // 모든 필드를 인자로 받는 생성자
public class User {
    @Id // 기본 키
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 자동 증가
    private Long id;

    @Column(nullable = false, unique = true) // null 불가능, 유일값
    private String username;

    @Column(nullable = false) // null 불가능
    private String password; // 암호화된 비밀번호가 저장될 것

    @Column(name = "created_at", updatable = false) // created_at 필드, 수정 불가능
    private LocalDateTime createdAt = LocalDateTime.now(); // 생성 시간 기본값

    // Lombok을 사용했으므로 getter/setter, 생성자는 따로 작성하지 않아도 됩니다.
    // 필요에 따라 toString() 등 오버라이드 가능
}