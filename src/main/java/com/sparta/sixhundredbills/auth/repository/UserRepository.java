package com.sparta.sixhundredbills.auth.repository;

import com.sparta.sixhundredbills.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


// 사용자 저장소
// 해당 인터페이스는 DB에 사용자 관련 정보 조회 및 저장에 사용.


@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // 사용자 이름(username)으로 사용자를 찾는 메서드
    Optional<User> findByUsername(String username);
}
