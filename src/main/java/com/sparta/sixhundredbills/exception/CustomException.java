package com.sparta.sixhundredbills.exception;


// CustomException이라는 사용자 정의 예외를 정의하고,
// 이 예외가 발생할 때 ErrorEnum에서 정의된 메시지를 예외 메시지로 사용하도록 구현

import lombok.Getter;

// CustomException 클래스 선언
@Getter
public class CustomException extends RuntimeException {
    private final ErrorEnum errorEnum;

    public CustomException(ErrorEnum errorEnum) {
        super(errorEnum.getMessage());
        this.errorEnum = errorEnum;
    }

    public int getStatusCode() {
        return this.errorEnum.getStatusCode();
    }

    public String getErrorMessage() {
        return this.errorEnum.getMessage();
    }
}