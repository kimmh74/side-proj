// D:\s11_proj\01_sideProj\kamco\src\main\java\com\kmh\kamco\service\AuthService.java

package com.kmh.kamco.service;

import com.kmh.kamco.dto.AuthResponse;
import com.kmh.kamco.dto.LoginRequest;
import com.kmh.kamco.dto.RegisterRequest;
import com.kmh.kamco.dto.UpdateRequest; // ★★★ UpdateRequest 임포트 ★★★
import com.kmh.kamco.mapper.UserMapper;
import com.kmh.kamco.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public AuthResponse registerUser(RegisterRequest request) {
        if (userMapper.existsByUsername(request.getUsername())) {
            throw new RuntimeException("이미 존재하는 사용자 이름입니다.");
        }
        // ★★★ 이메일 중복 체크 추가 (필요시) ★★★
        if (request.getEmail() != null && !request.getEmail().isEmpty() && userMapper.existsByEmail(request.getEmail())) {
            throw new RuntimeException("이미 존재하는 이메일입니다.");
        }


        User newUser = new User();
        newUser.setUsername(request.getUsername());
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));
        newUser.setEmail(request.getEmail()); // ★★★ email 설정 ★★★
        newUser.setName(request.getName());   // ★★★ name 설정 ★★★

        userMapper.save(newUser);
        return new AuthResponse("회원가입이 성공적으로 완료되었습니다.", newUser.getUsername());
    }

    public AuthResponse authenticateUser(LoginRequest request) {
        log.info("로그인 시도 - 사용자명: {}", request.getUsername());
        Optional<User> userOptional = userMapper.findByUsername(request.getUsername());

        if (userOptional.isEmpty()) {
            log.warn("로그인 실패 - 사용자 없음: {}", request.getUsername());
            throw new RuntimeException("잘못된 사용자 이름 또는 비밀번호입니다.");
        }

        User user = userOptional.get();
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log.warn("로그인 실패 - 비밀번호 불일치. 사용자명: {}", request.getUsername());
            throw new RuntimeException("잘못된 사용자 이름 또는 비밀번호입니다.");
        }

        log.info("로그인 성공 - 사용자명: {}", request.getUsername());
        return new AuthResponse("로그인 성공!", user.getUsername());
    }

    // ★★★ 사용자 정보 업데이트 메서드 추가 ★★★
    @Transactional
    public AuthResponse updateUser(UpdateRequest request) {
        Optional<User> userOptional = userMapper.findByUsername(request.getUsername());

        if (userOptional.isEmpty()) {
            throw new RuntimeException("사용자를 찾을 수 없습니다.");
        }

        User existingUser = userOptional.get();

        // 요청에서 email이 제공되었고, 기존 email과 다르면서 다른 사용자가 이미 사용 중이라면
        if (request.getEmail() != null && !request.getEmail().isEmpty() &&
            !request.getEmail().equals(existingUser.getEmail())) {
            if (userMapper.existsByEmail(request.getEmail())) {
                throw new RuntimeException("이미 사용 중인 이메일입니다.");
            }
        }

        // 비밀번호 변경 요청이 있다면
        if (request.getNewPassword() != null && !request.getNewPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(request.getNewPassword()));
        }
        
        // 이메일 변경 요청이 있다면
        if (request.getEmail() != null && !request.getEmail().isEmpty()) {
            existingUser.setEmail(request.getEmail());
        }

        // 이름 변경 요청이 있다면
        if (request.getName() != null && !request.getName().isEmpty()) {
            existingUser.setName(request.getName());
        }
        
        // 업데이트
        userMapper.updateUser(existingUser);
        return new AuthResponse("사용자 정보가 성공적으로 업데이트되었습니다.", existingUser.getUsername());
    }

    // 사용자 정보 가져오기 (프로필 페이지에서 현재 정보 표시용)
    public Optional<User> getUserDetails(String username) {
        return userMapper.findByUsername(username);
    }
}