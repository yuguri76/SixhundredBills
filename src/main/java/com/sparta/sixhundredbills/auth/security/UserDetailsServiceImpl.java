package com.sparta.sixhundredbills.auth.security;

import com.sparta.sixhundredbills.auth.entity.User;
import com.sparta.sixhundredbills.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Spring Security의 UserDetailsService 인터페이스를 구현하는 서비스 클래스.
 * 사용자의 로그인 정보를 조회하고 UserDetails 객체를 반환.
 */
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository; // UserRepository 의존성 주입

    /**
     * 주어진 사용자 이름(email)을 기반으로 사용자 정보를 조회하여 UserDetails 객체로 반환한다.
     * 사용자가 존재하지 않을 경우 UsernameNotFoundException을 던짐.
     *
     * @param email 조회할 사용자의 로그인 이름
     * @return UserDetails 객체
     * @throws UsernameNotFoundException 사용자가 존재하지 않는 경우 발생하는 예외
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // UserRepository를 사용하여 사용자 조회
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("사용자가 존재하지 않습니다: " + email));

        // UserDetailsImpl 객체를 사용하여 UserDetails 인터페이스 구현체 반환
        return new UserDetailsImpl(user);
    }
}