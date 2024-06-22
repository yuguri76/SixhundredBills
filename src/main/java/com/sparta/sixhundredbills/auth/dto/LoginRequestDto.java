package com.sparta.sixhundredbills.auth.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 로그인 요청 정보를 전달하는 DTO 클래스
// 클라이언트에서 서버로 로그인 관련 정보를 전송할 때 사용.

@Getter
@NoArgsConstructor
public class LoginRequestDto {
    private String email; // 사용자 이메일을 저장하는 필드(사용자 이름)
    private String password; // 사용자 비밀번호를 저장하는 필드


    // 빌더 에노테이션을 사용하여 빌터 패턴 적용.
    // 객체 생성 시 빌더를 통해 필요한 필드를 설정.
    @Builder

    // 아래 생성자는 빌더 패턴을 통해 객체를 생성할 때 사용.
    public LoginRequestDto(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
