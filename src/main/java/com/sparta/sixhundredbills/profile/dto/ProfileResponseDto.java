package com.sparta.sixhundredbills.profile.dto;

import com.sparta.sixhundredbills.auth.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ProfileResponseDto {
    private String email;
    private String name;
    private long likedPostsCount; // 좋아요한 게시물 개수
    private long likedCommentsCount; // 좋아요한 댓글 개수

    public static ProfileResponseDto fromUser(User user, long likedPostsCount, long likedCommentsCount) {
        return ProfileResponseDto.builder()
                .email(user.getEmail())
                .name(user.getName())
                .likedPostsCount(likedPostsCount)
                .likedCommentsCount(likedCommentsCount)
                .build();
    }
}