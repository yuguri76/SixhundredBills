package com.sparta.sixhundredbills.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 다양한 예외 상황을 정의하고 각 상황에 맞는 상태 코드 & 메시지 제공.


@NoArgsConstructor
@AllArgsConstructor
@Getter
public enum ErrorEnum {

    // user 관련 예외 상황 정의
    BAD_RESIGN(400, "이미 탈퇴한 회원입니다."),
    BAD_DUPLICATE(400, "중복되거나 탈퇴한 사용자가 존재합니다."),

    // authorization 관련 예외 상황 정의
    NOT_VALID_TOKEN(400, "유효하지 않은 토큰입니다"),
    EXPIRED_REFRESH_TOKEN_VALUE(403, "리프레시 토큰이 만료되었습니다, 재로그인이 필요합니다"),
    EXPIRED_TOKEN_VALUE(403, "토큰이 만료되었습니다, 재발급이 필요합니다"),
    USER_NOT_FOUND(400, "등록되지 않은 사용자입니다."),
    BAD_PASSWORD(400, "비밀번호를 확인해주세요");

    int statusCode; // 예외 발생 시 반환할 HTTP 상태 코드
    String message; // 예외 메시지

}