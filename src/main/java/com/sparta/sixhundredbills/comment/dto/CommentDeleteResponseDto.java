package com.sparta.sixhundredbills.comment.dto;

import lombok.Getter;

@Getter
public class CommentDeleteResponseDto {
    private String message; // 삭제 응답 메시지
    /**
     * CommentDeleteResponseDto 생성자.
     * @param message 삭제 응답 메시지
     */
    public CommentDeleteResponseDto(String message) {
        this.message = message;
    }
}