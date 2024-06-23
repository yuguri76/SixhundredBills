package com.sparta.sixhundredbills.auth.security;

import com.sparta.sixhundredbills.auth.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * Spring Security UserDetails 인터페이스를 구현하는 클래스.
 * 사용자 정보를 제공하여 인증 및 권한 부여를 위한 메서드들을 구현.
 */
public class UserDetailsImpl implements UserDetails {

    private final User user; // UserDetailsImpl 클래스는 User 엔티티를 사용.

    /**
     * UserDetailsImpl 객체를 생성하는 생성자.
     *
     * @param user UserDetailsImpl에 연결할 User 객체
     */
    public UserDetailsImpl(User user) {
        this.user = user;
    }

    /**
     * 사용자 정보를 반환하는 메서드
     *
     * @return user 객체의 정보
     */
    public User getUser() {
        return user;
    }

    /**
     * 사용자가 가지고 있는 권한 목록을 반환한다.
     *
     * @return 사용자의 권한 목록 (현재는 빈 리스트를 반환)
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    /**
     * 사용자의 비밀번호를 반환한다.
     *
     * @return 사용자의 비밀번호
     */
    @Override
    public String getPassword() {
        return user.getPassword();
    }

    /**
     * 사용자의 로그인 이름을 반환한다.
     *
     * @return 사용자의 이메일 (사용자의 고유 식별자로 사용)
     */
    @Override
    public String getUsername() {
        return user.getEmail();
    }

    /**
     * 사용자 계정이 만료되지 않았는지 여부를 반환한다.
     *
     * @return 사용자 계정의 만료 여부 (항상 true)
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * 사용자 계정이 잠기지 않았는지 여부를 반환한다.
     *
     * @return 사용자 계정의 잠금 여부 (항상 true)
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * 사용자 자격 증명(비밀번호)이 만료되지 않았는지 여부를 반환한다.
     *
     * @return 사용자 자격 증명의 만료 여부 (항상 true)
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * 사용자 계정이 활성화되어 있는지 여부를 반환한다.
     *
     * @return 사용자 계정의 활성화 여부 (항상 true)
     */
    @Override
    public boolean isEnabled() {
        return true;
    }

}



