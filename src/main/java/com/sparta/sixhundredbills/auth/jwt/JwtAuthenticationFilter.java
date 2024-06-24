package com.sparta.sixhundredbills.auth.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.sixhundredbills.auth.dto.LoginRequestDto;
import com.sparta.sixhundredbills.auth.service.AuthService;
import com.sparta.sixhundredbills.exception.CommonResponse;
import com.sparta.sixhundredbills.exception.ErrorEnum;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

/*
 * 사용자 로그인 인증을 처리하는 필터.
 * Spring Security의 UsernamePasswordAuthenticationFilter를 확장하여 JWT 기반 인증을 처리.

 * 사용자 로그인 요청이 처리 & 인증이 성공시 JWT 토큰이 생성 => 클라이언트에게 전달.
 * 실패 시 해당하는 적절한 오류 MSG & HTTP 상태 코드 반환.
 */

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthService authService; // 인증 서비스

    private LoginRequestDto loginRequestDto; // 로그인 요청 DTO 객체

    /**
     * JwtAuthenticationFilter 생성자.
     * @param authService 인증 서비스
     */
    public JwtAuthenticationFilter(AuthService authService) {
        this.authService = authService;
        setFilterProcessesUrl("/users/login"); // 로그인 요청 URL 설정
    }

    /**
     * 사용자 인증 시도 메서드 재정의.
     * @param request HTTP 요청 객체
     * @param response HTTP 응답 객체
     * @return 인증 객체
     * @throws AuthenticationException 인증 예외 발생 시
     */

    // 사용자의 로그인 요청을 처리 및 LoginRequestDto 를 생성하여 인증 매니저에게 전달
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            // HTTP 요청의 InputStream에서 LoginRequestDto 객체로 변환
            loginRequestDto = new ObjectMapper().readValue(request.getInputStream(), LoginRequestDto.class);

            // Spring Security의 UsernamePasswordAuthenticationToken을 사용하여 인증 매니저에게 인증을 요청
            return getAuthenticationManager().authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequestDto.getEmail(),
                            loginRequestDto.getPassword(),
                            null
                    )
            );
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * 인증 성공 시 호출되는 메서드 재정의.
     * @param request HTTP 요청 객체
     * @param response HTTP 응답 객체
     * @param chain FilterChain 객체
     * @param authResult 인증 결과 객체
     * @throws IOException IO 예외 발생 시
     * @throws ServletException 서블릿 예외 발생 시
     */

    // 인증이 성공한 경우 호출 & AuthService를 사용하여 로그인 처리
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        // AuthService를 사용하여 로그인 처리
        authService.login(loginRequestDto, response, request);
        // 로그인 성공 시 메시지 출력
        successLogin(response);
    }

    /**
     * 인증 실패 시 호출되는 메서드 재정의.
     * @param request HTTP 요청 객체
     * @param response HTTP 응답 객체
     * @param failed 인증 실패 예외 객체
     * @throws IOException IO 예외 발생 시
     * @throws ServletException 서블릿 예외 발생 시
     */

    // 인증이 실패한 경우 호출
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException {
        // 에러 응답을 위한 기본 설정
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        // ErrorEnum 주입
        ErrorEnum errorEnum;

        // 인증 실패 예외에 따라 처리
        if (failed instanceof InternalAuthenticationServiceException) {
            errorEnum = ErrorEnum.USER_NOT_FOUND;
        } else if (failed instanceof BadCredentialsException) {
            errorEnum = ErrorEnum.BAD_LOGIN_VALUE;
        } else {
            errorEnum = ErrorEnum.NOT_VALID_TOKEN; // 기본 예외 처리
        }

        // HTTP 응답 상태 설정
        response.setStatus(errorEnum.getStatusCode());

        // CommonResponse 객체 생성
        CommonResponse errorResponse = new CommonResponse(errorEnum.getMessage(), errorEnum.getStatusCode());

        // JSON 형식으로 응답 메시지 작성
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonResponse = objectMapper.writeValueAsString(errorResponse);

        // 응답에 JSON 메시지 작성
        response.getWriter().write(jsonResponse);
    }

    /**
     * 로그인 성공 시 출력할 메시지를 설정하는 메서드.
     * @param res HTTP 응답 객체
     */
    private void successLogin(HttpServletResponse res) {
        try {
            // 로그인 성공 메시지를 담은 CommonResponse 객체 생성
            CommonResponse<Void> response = CommonResponse.<Void>builder()
                    .msg("로그인 성공")
                    .statusCode(200)
                    .build();

            // 응답 설정: UTF-8 인코딩, JSON 콘텐츠 타입
            res.setCharacterEncoding("UTF-8");
            res.setContentType("application/json");

            // 응답에 메시지를 작성
            res.getWriter().write(new ObjectMapper().writeValueAsString(response));
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }
}
