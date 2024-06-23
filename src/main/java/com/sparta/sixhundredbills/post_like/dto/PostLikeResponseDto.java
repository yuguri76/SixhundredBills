package com.sparta.sixhundredbills.post_like.dto;

import lombok.Builder;
import lombok.Getter;

/**
 * 좋아요 응답을 위한 DTO 클래스
 */
@Getter
@Builder
public class PostLikeResponseDto {
    private String message; // 응답 메시지
    private Long postId; // 게시물 ID
    private Long userId; // 사용자 ID
    private int likesCount; // 좋아요 수
}