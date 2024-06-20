package com.sparta.sixhundredbills.profile.repository;

import com.sparta.sixhundredbills.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// 경민님 파일의 충돌방지와 코드 작성을 위한 임시 구현
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}
