package com.sparta.sixhundredbills.post.dto;

import com.sparta.sixhundredbills.post.entity.Post;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 게시물 응답 DTO
 */
@Getter
public class PostResponseDto {
    private Long id;
    private Long userId;
    private String showName;
    private String content;
    private String category;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private int likeCount;  // 좋아요 개수 추가

    @Builder
    public PostResponseDto(Long id, Long userId, String showName, String content, String category, LocalDateTime createdAt, LocalDateTime updatedAt, int likeCount) {
        this.id = id;
        this.userId = userId;
        this.showName = showName;
        this.content = content;
        this.category = category;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.likeCount = likeCount;
    }

    /**
     * Post 엔티티를 이용해 PostResponseDto 객체를 생성하는 생성자
     * @param post Post 엔티티
     */
    public PostResponseDto(Post post, int likeCount) {
        this.id = post.getId();
        this.userId = post.getUser().getId();
        this.showName = post.getShowName();
        this.content = post.getContent();
        this.category = post.getCategory();
        this.createdAt = post.getCreatedAt();
        this.updatedAt = post.getModifiedAt();
        this.likeCount = likeCount;
    }

    // 기본 생성자
    public PostResponseDto(Post post) {
        this(post, 0);  // 기본 좋아요 개수를 0으로 설정
    }
}
