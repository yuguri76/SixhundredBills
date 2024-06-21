package com.sparta.sixhundredbills.auth.entity;

import com.sparta.sixhundredbills.auth.dto.SignupRequestDto;
import com.sparta.sixhundredbills.timestamp.TimeStamp;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;


// User 엔티티는 사용자 정보를 담는 객체로, DB의 특정 테이블과 매핑됨.
// 필드들은 사용자 정보를 저장, 생성자와 메서드는 사용자 객체의 생성 & 동작을 정의.


@Entity // JPA 엔티티임을 나타내는 어노테이션
@Getter // Lombok: 모든 필드에 대한 getter 메서드 자동 생성
@Setter // Lombok: 모든 필드에 대한 setter 메서드 자동 생성
@Table(name = "sixhundredbills") // 데이터베이스 테이블 이름 지정
@NoArgsConstructor // Lombok: 매개변수 없는 기본 생성자 자동 생성
public class User extends TimeStamp {

    @Id // Primary Key 설정
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 자동 생성되는 값, MySQL의 AUTO_INCREMENT와 같음
    private Long id;

    @Column(name = "EMAIL", nullable = false, length = 35) // 데이터베이스 컬럼 설정
    private String email; // 이메일 (사용자명)

    @Column(name = "PASSWORD", nullable = false, length = 60) // 데이터베이스 컬럼 설정
    private String password; // 비밀번호

    @Column(name = "NAME", nullable = false, length = 40) // 데이터베이스 컬럼 설정
    private String name; // 이름



    @Setter // setter를 직접 설정
    @Column(nullable = false) // 데이터베이스 컬럼 설정
    @Enumerated(EnumType.STRING) // Enum 타입을 문자열로 저장
    private UserStatusEnum userStatus; // 사용자 상태

    @Column(name = "REFRESH_TOKEN", length = 255) // 데이터베이스 컬럼 설정
    private String refreshToken; // 리프레시 토큰

    @CreatedDate // 생성 일자 자동 생성
    @LastModifiedDate // 마지막 수정 일자 자동 생성
    @Temporal(TemporalType.TIMESTAMP) // 시간 타입 설정
    @Column(name = "USER_STATUS_TIME", nullable = false) // 데이터베이스 컬럼 설정
    private LocalDateTime userStatusTime; // 사용자 상태 변경 일시

    // 회원가입 요청 정보로부터 사용자 생성하는 생성자
    public User(SignupRequestDto signupRequestDto) {
        this.email = signupRequestDto.getEmail();
        this.password = signupRequestDto.getPassword();
        this.name = signupRequestDto.getName();
        this.userStatus = UserStatusEnum.USER_NORMAL; // 회원가입 시 기본으로 정상 사용자 상태 설정
    }

    // 필드를 직접 받아서 사용자 생성하는 생성자
    public User(String email, String password, String name, UserStatusEnum userStatusEnum) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.userStatus = UserStatusEnum.USER_NORMAL; // 기본으로 정상 사용자 상태 설정
    }

    // 사용자의 상태에 따라 역할을 반환하는 메서드
    public String getRole() {
        return this.userStatus == UserStatusEnum.USER_NORMAL ? Role.USER.name() : Role.ADMIN.name();
    }

    // 리프레시 토큰 설정 메서드
    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}