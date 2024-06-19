package com.sparta.sixhundredbills.post.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.Arrays;
import java.util.List;

@Getter
@Setter
public class PostRequestDto {

    @NotBlank(message = "내용이 비어있습니다.")
    private String content; // 게시물 내용

    private String showName; // 익명으로 표시될 이름

    @NotBlank(message = "카테고리를 입력해주세요.")
    private String category; // 게시물 카테고리

    // 유효한 카테고리 목록
    private static final List<String> VALID_CATEGORIES = Arrays.asList("일상공유", "고민상담");

    // 카테고리 유효성 검사
    public boolean isValidCategory() {
        return VALID_CATEGORIES.contains(this.category);
    }
}