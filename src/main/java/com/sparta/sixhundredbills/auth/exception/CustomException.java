package com.sparta.sixhundredbills.auth.exception;


// CustomException이라는 사용자 정의 예외를 정의하고,
// 이 예외가 발생할 때 ErrorEnum에서 정의된 메시지를 예외 메시지로 사용하도록 구현

// CustomException 클래스 선언
public class CustomException extends RuntimeException {

    // CustomException 생성자 선언, ErrorEnum 타입의 message 매개변수를 받음
    public CustomException(ErrorEnum message) {
        // 상위 클래스(RuntimeException)의 생성자 호출, message를 문자열로 변환하여 전달
        super(String.valueOf(message));
    }
}