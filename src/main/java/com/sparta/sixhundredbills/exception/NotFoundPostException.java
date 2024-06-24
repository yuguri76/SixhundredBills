package com.sparta.sixhundredbills.exception;

public class NotFoundPostException extends RuntimeException {
    private final ErrorEnum errorEnum;

    public ErrorEnum getErrorEnum() {
        return errorEnum;
    }

    public NotFoundPostException(ErrorEnum errorEnum) {
        super(errorEnum.getMessage());
        this.errorEnum = errorEnum;
    }

}