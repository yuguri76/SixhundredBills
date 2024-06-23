package com.sparta.sixhundredbills.post.dto;

import com.sparta.sixhundredbills.post.entity.Post;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PostResponseDto {
    private Long id;
    private Long userId;
    private String showName;
    private String content;
    private String category;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Builder
    public PostResponseDto(Long id, Long userId, String showName, String content, String category, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.showName = showName;
        this.content = content;
        this.category = category;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * Post 엔티티를 이용해 PostResponseDto 객체를 생성하는 생성자
     * @param post Post 엔티티
     */
    public PostResponseDto(Post post) {
        this.id = post.getId();
        this.userId = post.getUser().getId();
        this.showName = post.getShowName();
        this.content = post.getContent();
        this.category = post.getCategory();
        this.createdAt = post.getCreatedAt();
        this.updatedAt = post.getModifiedAt();
    }
}