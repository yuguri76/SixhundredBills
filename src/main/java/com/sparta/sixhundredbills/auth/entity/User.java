package com.sparta.sixhundredbills.auth.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

// DB 테이블에 매핑되는 Entity
// => 사용자의 정보를 저장하는 역할.


@Entity // JPA가 관리하는 엔티티 클래스임을 명시
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // DB에 자동 생성되는 값임을 표시.
    private Long id; // 사용자의 고유 식별자 (Primary Key)

    @Column(unique = true, nullable = false)
    private String username; // 사용자의 로그인 이름 (고유하게 설정, 비어 있으면 안 됨)

    @Column(nullable = false)
    private String password; // 사용자의 암호 (비어 있으면 안 됨)

    @Enumerated(EnumType.STRING)
    private Role role; // 사용자의 역할 (Enum 타입으로 저장)


    // => 추가된 필드 및 메서드
    // userStatus 필드와 관련된 getUserStatus() 메서드 추가 => 사용자 상태 반환
    // refreshToken 관련 필드 & getter/setter 메서드 추가 => Refresh Token을 관리


    @Setter
    private UserStatusEnum userStatus; // 사용자의 상태를 나타내는 Enum 타입 필드 , EX) 활성 상태, 비활성 상태 등 저장가능
    private String refreshToken; // JWT의 Refresh Token을 저장하는 필드  => 사용자가 로그인하고 나서 Refresh Token이 발급될 경우 이 필드에 저장!


    // 사용자의 상태를 반환하는 메서드 => 필드 값 반환.
    public UserStatusEnum getUserStatus() {
        return userStatus;
    }

    // 사용자의 역할을 반환하는 메서드 추가
    public String getRole() {
        return role.name(); // role의 이름을 문자열로 반환
    }


    // Refresh Token 설정 메서드 => 주어진 문자열을 refreshToken 필드에 저장!
    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    // Refresh Token 반환 메서드 => 현재 객체의 refreshToken 필드 값을 반환
    public String getRefreshToken() {
        return this.refreshToken;
    }
}