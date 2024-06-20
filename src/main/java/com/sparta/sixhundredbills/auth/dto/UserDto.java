package com.sparta.sixhundredbills.auth.dto;


import lombok.Data;

import javax.management.relation.Role;

// 클라이언트에서 전송되는 사용자 정보를 담는 DTO 클래스
@Data
public class UserDto {

    private String username; // 사용자의 이름을 저장하는 필드

    private String password; // 사용자의 패스워드를 저장하는 필드

    private Role role; // 사용자의 역할을 저장하는 필드

}