package com.sparta.sixhundredbills.auth.jwt;


// JWT 토큰의 종류를 나타내는 열거형

public enum JwtTokenType {
    ACCESS_TOKEN,    // 엑세스 토큰 : 인증된 사용자가 보호된 리소스에 접근하는데 사용.
    REFRESH_TOKEN    // 리프레시 토큰 : 엑세스 토콘의 만료 후 새로운 엑세스 토큰을 발급받기 위해 사용.
}
