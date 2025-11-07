// D:\s11_proj\01_sideProj\kamco\src\main\java\com\kmh\kamco\mapper\UserMapper.java

package com.kmh.kamco.mapper;

import com.kmh.kamco.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param; // @Param 사용 시 임포트
import java.util.Optional;

@Mapper
public interface UserMapper {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    void save(User user);

    // ★★★ 사용자 정보 업데이트 메서드 추가 ★★★
    void updateUser(User user);

    // email로 사용자 찾기 (유효성 검사에 필요할 수 있음)
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
}