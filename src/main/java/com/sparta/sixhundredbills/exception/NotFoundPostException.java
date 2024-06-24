package com.sparta.sixhundredbills.exception;

public class NotFoundPostException extends RuntimeException {
    public NotFoundPostException() {
        super("해당 게시물은 존재하지 않습니다.");
    }

    public NotFoundPostException(String message) {
        super(message);
    }
}