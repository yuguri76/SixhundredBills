package com.sparta.sixhundredbills.profile.repository;

import com.sparta.sixhundredbills.profile.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
