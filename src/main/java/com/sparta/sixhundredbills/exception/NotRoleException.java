package com.sparta.sixhundredbills.exception;

public class NotRoleException extends RuntimeException {
    private final ErrorEnum errorEnum;

    public ErrorEnum getErrorEnum() {
        return errorEnum;
    }

    public NotRoleException(ErrorEnum errorEnum) {
        super(errorEnum.getMessage());
        this.errorEnum = errorEnum;
    }
}
