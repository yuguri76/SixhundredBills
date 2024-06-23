package com.sparta.sixhundredbills.post_like.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * 좋아요 요청을 위한 DTO 클래스
 */
@Getter
@Setter
public class PostLikeRequestDto {
    private Long userId; // 요청하는 사용자의 ID
}