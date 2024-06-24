package com.sparta.sixhundredbills.exception;

public class UnauthorizedException extends RuntimeException {
    private final ErrorEnum errorEnum;

    public UnauthorizedException(ErrorEnum errorEnum) {
        super(errorEnum.getMessage());
        this.errorEnum = errorEnum;
    }

    public UnauthorizedException(ErrorEnum errorEnum, Throwable cause) {
        super(errorEnum.getMessage(), cause);
        this.errorEnum = errorEnum;
    }

    public ErrorEnum getErrorEnum() {
        return errorEnum;
    }
}