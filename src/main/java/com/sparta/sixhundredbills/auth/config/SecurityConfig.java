package com.sparta.sixhundredbills.auth.config;

import com.sparta.sixhundredbills.auth.jwt.*;
import com.sparta.sixhundredbills.auth.security.UserDetailsServiceImpl;
import com.sparta.sixhundredbills.auth.service.AuthService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security 설정 클래스.
 * JWT 기반 인증 및 인가를 설정하고 Spring Security 필터 체인을 구성합.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtUtil jwtUtil;
    private final AuthService authService;
    private final UserDetailsServiceImpl userDetailsService;
    private final AuthenticationConfiguration authenticationConfiguration;

    /**
     * 생성자를 통해 필요한 의존성을 주입 받음.
     *
     * @param authService                 인증 서비스
     * @param userDetailsService          사용자 세부 정보 서비스
     * @param jwtUtil                     JWT 유틸리티 클래스
     * @param authenticationConfiguration 인증 구성
     */
    public SecurityConfig(AuthService authService, UserDetailsServiceImpl userDetailsService, JwtUtil jwtUtil, AuthenticationConfiguration authenticationConfiguration) {
        this.authService = authService;
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
        this.authenticationConfiguration = authenticationConfiguration;
    }


    /**
     * 보안 필터 체인을 설정합니다.
     *
     * @param http HttpSecurity 객체
     * @return 설정된 SecurityFilterChain 객체
     * @throws Exception 설정 중 발생할 수 있는 예외
     */
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception { // 보안 필터 체인을 설정.
        http
                .csrf(csrf -> csrf.disable()) // CSRF 보호를 비활성화.
                .sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 세션을 사용하지 않도록 설정합니다.
                .authorizeRequests(authorizeRequests -> // 요청에 대한 인가 규칙을 설정.
                        authorizeRequests
                                .requestMatchers("/users/signup").permitAll() // 회원가입 경로는 인증 없이 접근 가능.
                                .requestMatchers("/users/login").permitAll() // 로그인 경로는 인증 없이 접근 가능.
                                .anyRequest().authenticated() // 그 외 모든 요청은 인증이 필요.
                );

        http.exceptionHandling(exception -> // 예외 처리 설정
                exception
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint()) // 인증 실패 시 처리할 EntryPoint를 설정.
                        .accessDeniedPage("/") // 권한이 없는 경우 처리할 페이지를 설정.
        );

        // JWT 인증 필터를 추가합니다.
        http.addFilterBefore(jwtAuthorizationFilter(), JwtAuthenticationFilter.class); // JWT 인증 필터를 UsernamePasswordAuthenticationFilter 앞에 추가.
        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class); // JWT 인증 필터를 UsernamePasswordAuthenticationFilter 앞에 추가.

        return http.build();
    }

    /**
     * 비밀번호 암호화에 사용할 PasswordEncoder 빈을 설정.
     *
     * @return BCryptPasswordEncoder 객체
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 인증 실패 시 처리할 EntryPoint를 설정.
     *
     * @return JwtAuthenticationEntryPoint 객체
     */
    @Bean
    public JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint() {
        return new JwtAuthenticationEntryPoint();
    }

    /**
     * JWT 인증 필터를 설정.
     *
     * @return JwtAuthenticationFilter 객체
     * @throws Exception 설정 중 발생할 수 있는 예외
     */
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() throws Exception {
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(authService);
        filter.setAuthenticationManager(authenticationManager(authenticationConfiguration));
        return filter;
    }

    /**
     * AuthenticationManager 빈을 설정.
     *
     * @param authenticationConfiguration 인증 구성 객체
     * @return AuthenticationManager 객체
     * @throws Exception 설정 중 발생할 수 있는 예외
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    /**
     * JWT 인가 필터를 설정.
     *
     * @return JwtAuthorizationFilter 객체
     */
    @Bean
    public JwtAuthorizationFilter jwtAuthorizationFilter() {
        return new JwtAuthorizationFilter(jwtUtil, userDetailsService, authService);
    }
}

//    // JWT 토큰 제공자 빈 설정, JWT 토큰 관련 기능을 제공
//    @Bean
//    public JwtTokenProvider jwtTokenProvider() {
//        return new JwtTokenProvider();
//    }
//
//    // JWT 토큰 필터 빈 설정, JWT 토큰의 유효성 검사 및 사용자 인증을 수행
//    @Bean
//    public JwtTokenFilter jwtTokenFilter() {
//        return new JwtTokenFilter(jwtTokenProvider, userDetailsService);
//    }


//    // UserDetailsServiceImpl 빈 설정, 사용자 세부 정보 서비를 설정.
//    // UserRepository 를 주입받아 생성.
//    @Bean
//    public UserDetailsServiceImpl userDetailsServiceImpl(UserRepository userRepository) {
//        return new UserDetailsServiceImpl(userRepository);
//    }

