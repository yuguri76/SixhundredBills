package com.sparta.sixhundredbills.auth.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.sixhundredbills.auth.security.UserDetailsImpl;
import com.sparta.sixhundredbills.auth.security.UserDetailsServiceImpl;
import com.sparta.sixhundredbills.auth.service.AuthService;
import com.sparta.sixhundredbills.exception.CommonResponse;
import com.sparta.sixhundredbills.exception.ErrorEnum;
import com.sparta.sixhundredbills.exception.UnauthorizedException;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/*
 * JWT 인증 필터.
 * Spring Security의 OncePerRequestFilter를 확장하여 JWT 기반 인증을 처리.

 * JWT 토큰을 검증 & 유효한 토큰이 있을 시 사용자 인증 => 보안 컨텍스트에 설정.
 */
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil; // JWT 유틸리티 클래스
    private final UserDetailsServiceImpl userDetailsService; // 사용자 세부 정보 서비스
    private final AuthService authService; // 인증 서비스

    /**
     * JwtAuthorizationFilter 생성자.
     * @param jwtUtil JWT 유틸리티 클래스
     * @param userDetailsService 사용자 세부 정보 서비스
     * @param authService 인증 서비스
     */
    public JwtAuthorizationFilter(JwtUtil jwtUtil, UserDetailsServiceImpl userDetailsService, AuthService authService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.authService = authService;
    }

    /**
     * 내부 필터 로직을 구현한 메서드.
     * @param req HTTP 요청 객체
     * @param res HTTP 응답 객체
     * @param filterChain 필터 체인 객체
     * @throws ServletException 서블릿 예외 발생 시
     * @throws IOException IO 예외 발생 시
     */

    // 필터 내부의 로직 구현
    // HTTP 요청에서 엑스스 토큰 & 리프레시 토큰을 추출, 토큰의 유효성을 검증한 후 인증을 설정.
    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain filterChain) throws ServletException, IOException {
        // 요청에서 액세스 토큰과 리프레시 토큰을 가져옴
        String tokenValue = jwtUtil.getAccessTokenFromRequest(req);
        String refreshTokenValue = jwtUtil.getRefreshTokenFromRequest(req);

        // 로그인 또는 회원가입 요청이 아닌 경우
        if (!(req.getRequestURI().equals("/users/login") || req.getRequestURI().equals("/users/signup"))) {
            try {
                // 토큰이 존재하지 않는 경우
                if (!(StringUtils.hasText(tokenValue) && StringUtils.hasText(refreshTokenValue))) {
                    throw new UnauthorizedException(ErrorEnum.NOT_LOGIN);
                }

                // 토큰의 "Bearer " 접두사 제거
                tokenValue = jwtUtil.substringToken(tokenValue);
                refreshTokenValue = jwtUtil.substringToken(refreshTokenValue);

                // 토큰 재발급 요청인 경우
                if (req.getRequestURI().equals("/users/reissue")) {
                    // 리프레시 토큰 유효성 검사
                    jwtUtil.validToken(refreshTokenValue, JwtTokenType.REFRESH_TOKEN, req);
                    // 새로운 액세스 토큰 발급
                    tokenValue = authService.tokenReissuance(refreshTokenValue, res);
                    tokenValue = jwtUtil.substringToken(tokenValue);
                } else {
                    // 리프레시 토큰 및 액세스 토큰 유효성 검사
                    jwtUtil.validToken(refreshTokenValue, JwtTokenType.REFRESH_TOKEN, req);
                    jwtUtil.validToken(tokenValue, JwtTokenType.ACCESS_TOKEN, req);
                }

                // 토큰에서 사용자 정보 추출
                Claims info = jwtUtil.getUserInfoFromToken(tokenValue);
                // 인증 설정
                setAuthentication(info.getSubject());
            } catch (UnauthorizedException e) {
                // 예외가 발생하면 응답에 상태 코드와 메시지 설정
                ErrorEnum errorEnum = e.getErrorEnum();
                res.setStatus(errorEnum.getStatusCode());  // 상태코드
                res.setContentType(MediaType.APPLICATION_JSON_VALUE);  // 데이터 타입
                res.setCharacterEncoding("UTF-8");  // 인코딩 설정

                // CommonResponse 객체 생성
                CommonResponse response = new CommonResponse<>(errorEnum.getMessage(), errorEnum.getStatusCode());

                // JSON 형식으로 응답 메시지 작성
                ObjectMapper mapper = new ObjectMapper();
                String jsonResponse = mapper.writeValueAsString(response);

                res.getWriter().write(jsonResponse);
                return; // 필터 체인을 중지합니다.
            }
        }
        // 다음 필터 호출
        filterChain.doFilter(req, res);
    }

    /**
     * 인증 설정 메서드.
     * @param email 사용자 이름
     */
    public void setAuthentication(String email) {
        // 빈 보안 컨텍스트 생성
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        // 인증 객체 생성
        Authentication authentication = createAuthentication(email);
        // 보안 컨텍스트에 인증 객체 설정
        context.setAuthentication(authentication);

        // 보안 컨텍스트 홀더에 설정된 보안 컨텍스트 저장
        SecurityContextHolder.setContext(context);

        // 사용자 역할 로깅
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String role = userDetails.getUser().getRole();
        System.out.println("현재 사용자의 역할: " + role);
    }

    /**
     * 인증 객체 생성 메서드.
     * @param email 사용자 이름
     * @return 인증 객체
     */
    private Authentication createAuthentication(String email) {
        // 사용자 이름을 기반으로 UserDetails 객체 로드
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        // UserDetails 객체를 기반으로 UsernamePasswordAuthenticationToken 객체 생성
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }
}