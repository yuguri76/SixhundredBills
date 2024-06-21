package com.sparta.sixhundredbills.profile.dto;

import com.sparta.sixhundredbills.auth.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ProfileResponseDto {  // 응답
    private String username;
    private String name;

    public ProfileResponseDto(User user) {
        this.username = user.getUsername();
    }
}
