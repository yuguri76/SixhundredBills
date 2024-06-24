package com.sparta.sixhundredbills.comment.dto;

import lombok.Getter;

@Getter
public class CommentDeleteResponseDto {
    private String message;
    public CommentDeleteResponseDto(String message) {
        this.message = message;
    }
}
