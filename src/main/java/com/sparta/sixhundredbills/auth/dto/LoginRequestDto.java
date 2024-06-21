package com.sparta.sixhundredbills.auth.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// 로그인 요청 정보를 전달하는 DTO 클래스
// 클라이언트에서 서버로 로그인 관련 정보를 전송할 때 사용.

@Getter
@NoArgsConstructor
@Setter
public class LoginRequestDto {
    private String username; // 사용자 이름을 저장하는 필드
    private String password; // 사용자 비밀번호를 저장하는 필드
}
