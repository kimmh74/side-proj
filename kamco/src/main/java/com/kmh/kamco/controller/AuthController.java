// D:\s11_proj\01_sideProj\kamco\src\main\java\com\kmh\kamco\controller\AuthController.java

package com.kmh.kamco.controller;

import com.kmh.kamco.dto.AuthResponse;
import com.kmh.kamco.dto.LoginRequest;
import com.kmh.kamco.dto.RegisterRequest;
import com.kmh.kamco.dto.UpdateRequest; // ★★★ UpdateRequest 임포트 ★★★
import com.kmh.kamco.model.User; // User 모델 임포트
import com.kmh.kamco.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> registerUser(@RequestBody RegisterRequest request) {
        try {
            AuthResponse response = authService.registerUser(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new AuthResponse(e.getMessage(), null));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> authenticateUser(@RequestBody LoginRequest request) {
        try {
            AuthResponse response = authService.authenticateUser(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new AuthResponse(e.getMessage(), null));
        }
    }

    // ★★★ 사용자 정보 업데이트 API 엔드포인트 추가 ★★★
    @PutMapping("/update") // PUT 요청으로 변경, 일반적으로 수정은 PUT
    public ResponseEntity<AuthResponse> updateUser(@RequestBody UpdateRequest request) {
        try {
            // 사용자 ID는 User 객체 안에 있으므로 DTO에 id 필드를 추가하여 받거나,
            // JWT 토큰 등에서 사용자 정보를 가져와 검증하는 로직이 필요합니다.
            // 여기서는 요청에 포함된 username을 사용하여 찾도록 구현합니다.
            AuthResponse response = authService.updateUser(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new AuthResponse(e.getMessage(), null));
        }
    }

    // ★★★ 사용자 정보 조회 API 엔드포인트 추가 (프로필 페이지에서 정보 가져오기용) ★★★
    @GetMapping("/profile/{username}")
    public ResponseEntity<?> getUserProfile(@PathVariable String username) {
        Optional<User> userOptional = authService.getUserDetails(username);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            // 민감한 정보(비밀번호)는 제외하고 반환
            Map<String, Object> profile = new HashMap<>();
            profile.put("username", user.getUsername());
            profile.put("email", user.getEmail());
            profile.put("name", user.getName());
            profile.put("createdAt", user.getCreatedAt());
            return ResponseEntity.ok(profile);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("사용자를 찾을 수 없습니다.");
        }
    }
}