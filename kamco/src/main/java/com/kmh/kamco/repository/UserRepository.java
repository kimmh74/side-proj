package com.kmh.kamco.repository;


import com.kmh.kamco.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username); // username으로 사용자 찾기
    boolean existsByUsername(String username); // username 존재 여부 확인
}
