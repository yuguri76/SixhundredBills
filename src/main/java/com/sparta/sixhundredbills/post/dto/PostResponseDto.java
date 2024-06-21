package com.sparta.sixhundredbills.post.dto;

import com.sparta.sixhundredbills.post.entity.Post;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PostResponseDto {
    private Long id; // 게시물 ID
    private Long userId; // 작성자 ID
    private String showName; // 익명으로 표시될 이름
    private String content; // 게시물 내용
    private String category; // 게시물 카테고리
    private LocalDateTime createdAt; // 생성일자
    private LocalDateTime updatedAt; // 수정일자

    // 모든 필드를 포함하는 생성자
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

    // Post 엔티티를 기반으로 PostResponseDto를 생성하는 생성자
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