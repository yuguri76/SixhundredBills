package com.sparta.sixhundredbills.exception;

public class NotFoundCommentException extends RuntimeException {
    private final ErrorEnum errorEnum;

    public ErrorEnum getErrorEnum() {
        return errorEnum;
    }

    public NotFoundCommentException(ErrorEnum errorEnum) {
        super(errorEnum.getMessage());
        this.errorEnum = errorEnum;
    }
}