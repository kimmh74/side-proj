package com.kmh.kamco;


import com.kmh.kamco.model.User;
import com.kmh.kamco.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
@EntityScan(basePackages = "com.kmh.kamco.model")
public class KamcoApplication {

	public static void main(String[] args) {
		SpringApplication.run(KamcoApplication.class, args);
	}

	// 애플리케이션 시작 시 실행되는 코드 (테스트 사용자 등록)
	@Bean
	public CommandLineRunner demoData(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		return args -> {
			if (userRepository.findByUsername("testuser").isEmpty()) {
				User user = new User();
				user.setUsername("testuser");
				user.setPassword(passwordEncoder.encode("1234")); // "1234" 비밀번호를 암호화하여 저장
				userRepository.save(user);
				System.out.println("초기 사용자 'testuser'가 등록되었습니다.");
			}
			if (userRepository.findByUsername("admin").isEmpty()) {
				User admin = new User();
				admin.setUsername("admin");
				admin.setPassword(passwordEncoder.encode("password")); // "password" 비밀번호를 암호화하여 저장
				userRepository.save(admin);
				System.out.println("초기 사용자 'admin'이 등록되었습니다.");
			}
		};
	}
}