package com.sparta.sixhundredbills.comment_like.dto;

import com.sparta.sixhundredbills.comment_like.entity.CommentLike;
import lombok.Builder;
import lombok.Getter;

/**
 * 좋아요 응답을 위한 DTO 클래스
 */
@Getter
@Builder
public class CommentLikeResponseDto {
    private String content; // 응답 메시지 또는 댓글 내용
    private Long commentId; // 댓글 ID
    private Long userId; // 사용자 ID
    private int likesCount; // 좋아요 수

    @Builder
    public CommentLikeResponseDto(String content, Long commentId, Long userId, int likesCount) {
        this.content = content;
        this.commentId = commentId;
        this.userId = userId;
        this.likesCount = likesCount;
    }

    public static CommentLikeResponseDto fromEntity(CommentLike commentLike) {
        return CommentLikeResponseDto.builder()
                .content(commentLike.getComment().getComment())
                .commentId(commentLike.getComment().getId())
                .userId(commentLike.getUser().getId())
                .likesCount(commentLike.getComment().getLikesCount())
                .build();
    }
}
