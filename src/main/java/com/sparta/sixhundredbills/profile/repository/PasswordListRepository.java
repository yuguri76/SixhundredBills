package com.sparta.sixhundredbills.profile.repository;

import com.sparta.sixhundredbills.auth.entity.User;
import com.sparta.sixhundredbills.profile.entity.PasswordList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PasswordListRepository extends JpaRepository<PasswordList, Long> {
    // SELECT * FROM PasswordList WHERE user = ? ORDER BY createdAt DESC;
    List<PasswordList> findByUserOrderByCreatedAtDesc(User User);
}
