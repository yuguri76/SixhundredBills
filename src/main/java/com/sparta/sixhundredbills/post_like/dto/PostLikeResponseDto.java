package com.sparta.sixhundredbills.post_like.dto;

import com.sparta.sixhundredbills.post_like.entity.PostLike;
import lombok.Builder;
import lombok.Getter;

@Getter
public class PostLikeResponseDto {
    private String content;
    private Long postId;
    private Long userId;
    private int likesCount;

    @Builder
    public PostLikeResponseDto(String content, Long postId, Long userId, int likesCount) {
        this.content = content;
        this.postId = postId;
        this.userId = userId;
        this.likesCount = likesCount;
    }

    public static PostLikeResponseDto fromEntity(PostLike postLike) {
        return PostLikeResponseDto.builder()
                .content(postLike.getPost().getContent())
                .postId(postLike.getPost().getId())
                .userId(postLike.getUser().getId())
                .likesCount(postLike.getPost().getLikeCount())
                .build();
    }
}
