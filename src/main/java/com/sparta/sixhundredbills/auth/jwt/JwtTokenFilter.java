package com.sparta.sixhundredbills.auth.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// JWT 필터
// JWT를 사용하여 인증된 사용자를 필터링하고 Spring Security에서 인증을 처리하는 필터
// OneePerRequestFilter를 상속받아 한 번의 요청에 대해 한번만 실행되도록 함.
// 유효한 토큰이 있을 경우 사용자 인증을 수행




public class JwtTokenFilter extends OncePerRequestFilter {


    private final JwtTokenProvider jwtTokenProvider; // jwtTokenProvider는 jwt 토큰을 생성 및 유효성을 검사하는 도구
    private final UserDetailsService userDetailsService;


    // JwtTokenProvider를 주입받아 필터를 초기화하는 생성자
    // JwtTokenProvider 는 JWT 토큰의 생성 및 검증을 담당.
    // UserDetailsServic 는 Spring Security에서 사용자의 세부 정보를 가져오는 역할
    public JwtTokenFilter(JwtTokenProvider jwtTokenProvider, UserDetailsService userDetailsService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userDetailsService = userDetailsService;
    }

    // 실제 필터링 로직이 구현된 메서드
    @Override

    // 필터의 핵심 로직이 구현된 메서드
    // HTTP 요청을 필터링하고 JWT 토큰의 유효성을 검사
    // 인증된 사용자 정보를 SecurityContextHolder 에 설정.
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        // 요청에서 JWT 토큰을 추출
        String token = resolveToken(request);
        try {
            // 토큰이 유효한지 확인
            if (token != null && jwtTokenProvider.validateToken(token)) {
                // 토큰에서 사용자 이름을 추출
                String username = jwtTokenProvider.getUsername(token);
                // 사용자 세부 정보를 로드
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                // 사용자 정보를 기반으로 UsernamePasswordAuthenticationToken 생성
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                        username, null, jwtTokenProvider.getRoles(token));
                // SecurityContextHolder에 인증 정보 설정
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        } catch (Exception e) {
            // 인증 과정에서 예외 발생 시 로그 기록 및 인증 실패 처리
            logger.error("인증 오류", e);
            SecurityContextHolder.clearContext();
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "인증 실패");
            return;
        }
        // 다음 필터 체인 실행
        filterChain.doFilter(request, response);
    }


    // HTTP 요청에서 Authorization 헤더를 통해 JWT 토큰을 추출하는 메서드
    // "Bearer" 로 시작하는 Authorization 헤더에서 실제 토큰 부분을 추출하여 반환.
    private String resolveToken(HttpServletRequest request) {
        // Authorization 헤더에서 Bearer 토큰을 추출
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            // "Bearer " 이후의 실제 토큰 부분을 반환
            return bearerToken.substring(7);
        }
        return null;
    }
}