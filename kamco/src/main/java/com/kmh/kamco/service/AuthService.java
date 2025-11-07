package com.kmh.kamco.service;

import com.kmh.kamco.dto.AuthResponse;
import com.kmh.kamco.dto.LoginRequest;
import com.kmh.kamco.dto.RegisterRequest;
import com.kmh.kamco.model.User;
import com.kmh.kamco.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.slf4j.Logger; // Logger 임포트
import org.slf4j.LoggerFactory; // LoggerFactory 임포트

import java.util.Optional;

@Service
public class AuthService {
	
    private static final Logger log = LoggerFactory.getLogger(AuthService.class); // 로거 생성

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; // 비밀번호 암호화를 위한 인코더

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional // 트랜잭션 처리 (데이터 일관성 보장)
    public AuthResponse registerUser(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("이미 존재하는 사용자 이름입니다.");
        }

        User newUser = new User();
        newUser.setUsername(request.getUsername());
        newUser.setPassword(passwordEncoder.encode(request.getPassword())); // 비밀번호 암호화
        
        userRepository.save(newUser);
        return new AuthResponse("회원가입이 성공적으로 완료되었습니다.", newUser.getUsername());
    }

    public AuthResponse authenticateUser(LoginRequest request) {
        log.info("로그인 시도 - 사용자명: {}", request.getUsername()); // 로그인 시도 로그
        Optional<User> userOptional = userRepository.findByUsername(request.getUsername());

        if (userOptional.isEmpty()) {
            log.warn("로그인 실패 - 사용자 없음: {}", request.getUsername()); // 사용자 없음 로그
            throw new RuntimeException("잘못된 사용자 이름 또는 비밀번호입니다.");
        }

        User user = userOptional.get();
        log.info("사용자 찾음 - DB 비밀번호: {}, 입력 비밀번호: {}", user.getPassword(), request.getPassword()); // 비밀번호 로그 (디버그 시에만 사용)
        
        // BCryptPasswordEncoder.matches() 메서드 호출
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log.warn("로그인 실패 - 비밀번호 불일치. 사용자명: {}", request.getUsername()); // 비밀번호 불일치 로그
            throw new RuntimeException("잘못된 사용자 이름 또는 비밀번호입니다.");
        }

        log.info("로그인 성공 - 사용자명: {}", request.getUsername()); // 로그인 성공 로그
        return new AuthResponse("로그인 성공!", user.getUsername());
    }
}