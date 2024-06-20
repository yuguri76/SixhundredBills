package com.sparta.sixhundredbills.auth.entity;


// 사용자 역할을 정의하는 열거형(enum)
//  ㄴ Role 열거형을 사용하여 사용자의 역할을 지정할 수 있음.


public enum Role {
    USER, // 일반 사용자 역할을 나타내는 상수
    ADMIN; // 관리자 역할을 나타내는 상수


    // Spring Security에서 권한을 부여할 때
    // 규약에 맞게 'ROLE_USER', 'ROLE_ADMIN'과 같은 형태로 역할을 반환.
    public String getRoleName() {
        return "ROLE_" + this.name();
    }
}
