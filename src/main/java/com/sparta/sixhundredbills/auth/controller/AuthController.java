package com.sparta.sixhundredbills.auth.controller;

import com.sparta.sixhundredbills.auth.dto.SignupRequestDto;
import com.sparta.sixhundredbills.auth.dto.SignupResponseDto;
import com.sparta.sixhundredbills.exception.CommonResponse;
import com.sparta.sixhundredbills.auth.service.AuthService;
import com.sparta.sixhundredbills.auth.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j // SLF4J를 사용한 로깅을 위한 애노테이션
@RestController // REST API 컨트롤러임을 나타내는 애노테이션
@RequiredArgsConstructor // 필수 생성자를 자동으로 생성해주는 Lombok 애노테이션
@RequestMapping("/users") // 이 컨트롤러의 기본 URL 경로 설정
public class AuthController {

    private final UserService userService; // 사용자 관련 비즈니스 로직을 처리하는 서비스 클래스
    private final AuthService authService; // 인증 관련 비즈니스 로직을 처리하는 서비스 클래스

    // 회원 가입 요청을 처리하는 엔드포인트
    @PostMapping("/signup")
    public ResponseEntity<CommonResponse<SignupResponseDto>> signup(@Valid @RequestBody SignupRequestDto requestDto) {
        // 요청 데이터가 유효한지 검사하고, UserService를 통해 회원 가입을 처리하고 응답 DTO를 생성합니다.
        SignupResponseDto responseDto = userService.signup(requestDto);

        // 처리 결과를 CommonResponse에 담아서 클라이언트에게 응답합니다.
        return ResponseEntity.ok().body(CommonResponse.<SignupResponseDto>builder()
                .msg("회원가입 성공")
                .statusCode(200)
                .data(responseDto)
                .build());
    }

    // 토큰 재발급 요청을 처리하는 엔드포인트
    @GetMapping("/reissue")
    public ResponseEntity<CommonResponse<Void>> tokenReissuance() {
        // AuthService를 통해 토큰 재발급 처리를 수행하고 성공 메시지를 응답.
        return ResponseEntity.ok().body(CommonResponse.<Void>builder()
                .msg("토큰 재발급 성공")
                .statusCode(200)
                .build());
    }

    // 로그아웃 요청을 처리하는 엔드포인트
    @GetMapping("/logout")
    public ResponseEntity<CommonResponse<Void>> logout(HttpServletResponse response, HttpServletRequest request) {
        // AuthService를 통해 로그아웃 처리를 수행하고 성공 메시지를 응답.
        authService.invalidateTokens(response, request);

        return ResponseEntity.ok().body(CommonResponse.<Void>builder()
                .msg("로그아웃 성공")
                .statusCode(200)
                .build());
    }
}

// 회원탈퇴는 필수 구현 조건에 없기에 임시 주석처리.
//    @PutMapping("/resign")
//    public ResponseEntity <CommonResponse<ResignDto>> resign(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody ResignDto resignDto){
//
//        userService.resign(userDetails.getUser(), resignDto);
//        return ResponseEntity.ok().body(CommonResponse.<ResignDto>builder()
//                .msg("회원탈퇴 성공")
//                .statusCode(200)
//                .build());
//    }




