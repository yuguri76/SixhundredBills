package com.sparta.sixhundredbills.auth.entity;

import com.sparta.sixhundredbills.auth.dto.SignupRequestDto;
import com.sparta.sixhundredbills.profile.dto.ProfileRequestDto;
import com.sparta.sixhundredbills.timestamp.TimeStamp;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

/**
 * User 엔티티 클래스
 */
@Entity
@Getter
@Table(name = "users")
@NoArgsConstructor
public class User extends TimeStamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 사용자 ID

    @Column(name = "EMAIL", nullable = false, length = 50)
    private String email; // 이메일

    @Column(name = "PASSWORD", nullable = false, length = 60)
    private String password;  // 비밀번호

    @Column(name = "NAME", nullable = false, length = 40)
    private String name; // 사용자 이름

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserStatusEnum userStatus; // 사용자 상태

    @Column(name = "ROLE", nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role; // 사용자 역할

    @Column(name = "REFRESH_TOKEN", length = 255)
    private String refreshToken; // 리프레시 토큰

    @CreatedDate
    @LastModifiedDate
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "USER_STATUS_TIME", nullable = false)
    private LocalDateTime userStatusTime; // 사용자 상태 변경 시간

    /**
     * User 엔티티 생성자
     * @param email 이메일
     * @param password 비밀번호
     * @param name 사용자 이름
     * @param userStatusEnum 사용자 상태
     * @param role 사용자 역할
     * @param refreshToken 리프레시 토큰
     * @param userStatusTime 사용자 상태 변경 시간
     */
    @Builder
    public User(String email, String password, String name, UserStatusEnum userStatusEnum, Role role, String refreshToken, LocalDateTime userStatusTime) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.userStatus = userStatusEnum != null ? userStatusEnum : UserStatusEnum.USER_NORMAL;
        this.role = role != null ? role : Role.USER;
        this.refreshToken = refreshToken;
        this.userStatusTime = userStatusTime != null ? userStatusTime : LocalDateTime.now();
    }

    /**
     * SignupRequestDto를 이용한 User 엔티티 생성자
     * @param signupRequestDto 회원가입 요청 DTO
     */
    public User(SignupRequestDto signupRequestDto) {
        this.email = signupRequestDto.getEmail();
        this.password = signupRequestDto.getPassword();
        this.name = signupRequestDto.getName();
        this.userStatus = UserStatusEnum.USER_NORMAL;
        this.role = Role.USER;
        this.userStatusTime = LocalDateTime.now();
    }

    /**
     * User 클래스의 일반 생성자.
     * @param email 사용자 이메일
     * @param password 사용자 비밀번호
     * @param name 사용자 이름
     * @param userStatusEnum 사용자 상태
     * @param role 사용자 역할
     */
    public User(String email, String password, String name, UserStatusEnum userStatusEnum, Role role) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.userStatus = userStatusEnum != null ? userStatusEnum : UserStatusEnum.USER_NORMAL;
        this.role = role != null ? role : Role.USER;
        this.userStatusTime = LocalDateTime.now();
    }

    /**
     * 사용자 역할 반환 메서드.
     * @return 사용자 역할
     */
    public String getRole() {
        return this.role.name();
    }

    /**
     * 리프레시 토큰 설정 메서드.
     * @param refreshToken 리프레시 토큰
     */
    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    /**
     * 프로필 업데이트 메서드.
     * @param user 사용자 정보 DTO
     * @param newPassword 새로운 비밀번호
     */
    public void updateProfile(ProfileRequestDto user, String newPassword) {
        this.name = user.getName();
        this.password = newPassword;
    }


    /**
     * 사용자 ID 설정 메서드.
     * @param id 사용자 ID
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 사용자 이메일 설정 메서드.
     * @param email 사용자 이메일
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * 사용자 비밀번호 설정 메서드.
     * @param password 사용자 비밀번호
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * 사용자 이름 설정 메서드.
     * @param name 사용자 이름
     */
    public void setName(String name) {
        this.name = name;
    }
}
