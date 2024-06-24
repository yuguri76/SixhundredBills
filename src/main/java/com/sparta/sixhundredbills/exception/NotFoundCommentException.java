package com.sparta.sixhundredbills.exception;

public class NotFoundCommentException extends RuntimeException {
    public NotFoundCommentException() {
        super("해당 댓글은 존재하지 않습니다.");
    }

    public NotFoundCommentException(String message) {
        super(message);
    }
}