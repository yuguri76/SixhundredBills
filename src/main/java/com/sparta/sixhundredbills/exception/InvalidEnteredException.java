package com.sparta.sixhundredbills.exception;

public class InvalidEnteredException extends RuntimeException {
    private final ErrorEnum errorEnum;

    public InvalidEnteredException(ErrorEnum errorEnum) {
        super(errorEnum.getMessage());
        this.errorEnum = errorEnum;
    }

    public ErrorEnum getErrorEnum() {
        return errorEnum;
    }
}
