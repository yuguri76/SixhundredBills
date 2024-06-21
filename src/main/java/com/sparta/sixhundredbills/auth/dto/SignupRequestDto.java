package com.sparta.sixhundredbills.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/*
 * 회원가입 요청을 위한 데이터 전송 객체 (DTO).
 * 각 필드는 유효성 검사를 포함하며, Lombok을 사용하여 getter와 setter 메소드를 자동 생성.
 */

@Getter
@Setter
public class SignupRequestDto {

    /**
     * 사용자 ID
     * - 공백 불가
     * - 길이: 최소 4자, 최대 10자
     * - 소문자 영문자와 숫자만 허용
     */
    @NotBlank(message = "ID는 공백일 수 없습니다.")
    @Size(min = 4, max = 10, message = "아이디는 최소 4자 이상, 10자 이하로 작성해주세요.")
    @Pattern(regexp = "^[a-z0-9]+$", message = "아이디는 소문자(a~z) 영문 + 숫자(0~9)만을 허용합니다.")
    private String email;

    /**
     * 사용자 비밀번호
     * - 공백 불가
     * - 길이: 최소 8자, 최대 15자
     * - 대소문자 영문자, 숫자, 특수문자 각 1자 이상 포함해야 함
     */
    @NotBlank(message = "비밀번호는 공백일 수 없습니다.")
    @Size(min = 8, max = 15, message = "비밀번호는 최소 8자 이상, 15자 이하로 작성해주세요.")
    @Pattern(regexp = "^(?=.*?[A-Za-z])(?=.*?[0-9])(?=.*?[#?!@$ %^&*-]).{10,}$",
            message = "대소문자 포함 영문 + 숫자 + 특수문자를 최소 1글자씩 포함해야합니다. ")
    private String password;

    /**
     * 사용자 이름
     * - 공백 불가
     */
    @NotBlank(message = "이름은 공백일 수 없습니다.")
    private String name;

    public String getEmail() {
        return "";
    }
}


