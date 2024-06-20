package com.sparta.sixhundredbills.auth.service;

import com.sparta.sixhundredbills.auth.entity.Role;
import com.sparta.sixhundredbills.auth.entity.User;
import com.sparta.sixhundredbills.auth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

// 사용자 서비스

// 사용자 등록 및 로그인 로직 처리 & DB 상호작용 => 사용자 관리 기능의 클래스
// @Service 애노테이션을 통해 이 클래스는 Spring의 서비스로 등록 및 IoC 컨테이너에 의해 관리.



// 수정 사항 :
// 잘못된 'signupUser' 메서드 제거, 올바른 메서드 구현 및 사용


@Service // 서비스로 등록하여 관리
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }


    // 사용자 등록 메서드
    public void signupUser(String username, String password, Role role) {

        // 이미 존재하는 username인지 확인
        if (userRepository.findByUsername(username).isPresent()) {
            // 이미 존재할 시 예외 처리
            throw new RuntimeException("사용자가 이미 존재합니다.");
        }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(password);

        // 사용자 엔티티 생성 및 설정
        User user = new User();
        user.setUsername(username);
        user.setPassword(encodedPassword);
        user.setRole(role);

        // 사용자 저장 후 반환
        userRepository.save(user);
    }

    // 사용자 로그인 메서드
    public User loginUser(String username, String password) {
        // username으로 사용자 조회
        User user = userRepository.findByUsername(username)
                // 사용자가 존재하지 않을 시 예외 처리
                .orElseThrow(() -> new RuntimeException("사용자 이름이 존재하지 않습니다."));

        // 비밀번호 일치 여부 확인
        if (!passwordEncoder.matches(password, user.getPassword())) {
            // 비밀번호 일치하지 않을 시 예외 처리
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }

        // 사용자 반환
        return user;
    }
}
