package com.sparta.sixhundredbills.profile.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProfileRequestDto {  // 요청
    private String name;
    private String password;
    private String newPassword;
}
