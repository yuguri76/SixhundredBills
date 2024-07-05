package com.sparta.sixhundredbills.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentResponseDto {
    private String showName; // 익명으로 표시될 이름
    private String comment; // 댓글 내용
    private int likesCount; // 댓글에 대한 좋아요 수
}