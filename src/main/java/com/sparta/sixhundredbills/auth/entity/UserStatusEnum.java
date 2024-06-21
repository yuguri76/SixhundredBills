package com.sparta.sixhundredbills.auth.entity;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

// 사용자의 상태를 나타내기 위한 열거형 클래스.
// => 사용자의 상태를 표현

@NoArgsConstructor  // 파라미터 없는 기본 생성자 자동 생성
@AllArgsConstructor  // 모든 필드를 포함하는 생성자 자동 생성
public enum UserStatusEnum {
    USER_NORMAL("정상"),  // 사용자 상태가 '정상'인 경우를 나타내는 enum 상수
    USER_RESIGN("탈퇴");  // 사용자 상태가 '탈퇴'인 경우를 나타내는 enum 상수

    String status;  // 사용자 상태를 나타내는 문자열 필드

    public String getStatus(){  // 사용자 상태 문자열을 반환하는 메서드
        return this.status;
    } // 각 열거 상수의 상태 문자열을 외부에서 가져 올 수 있음.
}