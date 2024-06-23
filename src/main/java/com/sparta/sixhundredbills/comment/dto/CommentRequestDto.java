package com.sparta.sixhundredbills.comment.dto;

import lombok.Getter;

@Getter
public class CommentRequestDto {
    private String comment;
    private Long parentCommentId;
}
