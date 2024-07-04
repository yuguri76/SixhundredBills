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
    private long likedPostsCount;
    private long likedCommentsCount;

    public static ProfileResponseDto fromUser(User user, long likedPostsCount, long likedCommentsCount) {
        return ProfileResponseDto.builder()
                .email(user.getEmail())
                .name(user.getName())
                .likedPostsCount(likedPostsCount)
                .likedCommentsCount(likedCommentsCount)
                .build();
    }
}