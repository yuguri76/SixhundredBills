package com.sparta.sixhundredbills.post.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.List;

@Getter
@Setter
@Builder
public class PostRequestDto {

    @NotBlank(message = "내용이 비어있습니다.")
    private String content;

    @NotBlank(message = "카테고리를 입력해주세요.")
    private String category;

    private static final List<String> VALID_CATEGORIES = Arrays.asList("일상공유", "고민상담", "익명토론");

    /**
     * 카테고리가 유효한지 확인하는 메서드
     * @return 유효한 카테고리인지 여부
     */
    public boolean isValidCategory() {
        return VALID_CATEGORIES.contains(this.category);
    }
}