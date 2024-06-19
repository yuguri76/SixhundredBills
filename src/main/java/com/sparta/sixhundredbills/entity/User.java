package com.sparta.sixhundredbills.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

// DB 테이블에 매핑되는 Entity
// => 사용자의 정보를 저장하는 역할.

@Entity
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
}