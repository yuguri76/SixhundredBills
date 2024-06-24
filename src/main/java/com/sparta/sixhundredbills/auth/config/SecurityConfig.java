package com.sparta.sixhundredbills.auth.config;

import com.sparta.sixhundredbills.auth.jwt.JwtAuthenticationEntryPoint;
import com.sparta.sixhundredbills.auth.jwt.JwtAuthenticationFilter;
import com.sparta.sixhundredbills.auth.jwt.JwtAuthorizationFilter;
import com.sparta.sixhundredbills.auth.jwt.JwtUtil;
import com.sparta.sixhundredbills.auth.security.UserDetailsServiceImpl;
import com.sparta.sixhundredbills.auth.service.AuthService;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
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
    private final UserDetailsServiceImpl userDetailsService;
    private final AuthenticationConfiguration authenticationConfiguration;
    private final ObjectProvider<AuthService> authServiceProvider;

    /**
     * 생성자를 통해 필요한 의존성을 주입 받음.
     *
     * @param userDetailsService          사용자 세부 정보 서비스
     * @param jwtUtil                     JWT 유틸리티 클래스
     * @param authenticationConfiguration 인증 구성
     * @param authServiceProvider         인증 서비스 제공자
     */
    public SecurityConfig(UserDetailsServiceImpl userDetailsService, JwtUtil jwtUtil, AuthenticationConfiguration authenticationConfiguration, ObjectProvider<AuthService> authServiceProvider) {
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
        this.authenticationConfiguration = authenticationConfiguration;
        this.authServiceProvider = authServiceProvider;
    }


    /**
     * 보안 필터 체인을 설정합니다.
     *
     * @param http HttpSecurity 객체
     * @return 설정된 SecurityFilterChain 객체
     * @throws Exception 설정 중 발생할 수 있는 예외
     * (필터중 빠진 것들이 있어서 일괄 수정했습니다 role도 추가하셔야 할 것 같아서 해당부분 주석처리하고 넣어놓았으니 나중에 활용하시면 될것같습니다) - 유규리
     */
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception { // 보안 필터 체인을 설정.
        // CSRF 설정
        http.csrf((csrf) -> csrf.disable());

        // 기본 설정인 Session 방식은 사용하지 않고 JWT 방식을 사용하기 위한 설정
        http.sessionManagement((sessionManagement) ->
                sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );

        http.authorizeHttpRequests((authorizeHttpRequests) ->
                authorizeHttpRequests
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll() // resources 접근 허용 설정
                        .requestMatchers("/").permitAll() // 메인 페이지 요청 허가
                        .requestMatchers("/users/signup").permitAll() // 회원가입 요청 허가
                        .requestMatchers("/users/login").permitAll() // 로그인 요청 허가
                        .requestMatchers("/users/**").authenticated() // '/user/'로 시작하는 요청 인증 필요
                       // .requestMatchers("/admin/**").hasAuthority(UserRoleEnum.ADMIN.getAuthority()) //권한이 Admin 인 유저만 접근가능
                        .anyRequest().authenticated() // 그 외 모든 요청 인증처리
        );

        // 필터 관리
        http.addFilterBefore(jwtAuthorizationFilter(), JwtAuthenticationFilter.class);
        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

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
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(authServiceProvider.getObject());
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
        return new JwtAuthorizationFilter(jwtUtil, userDetailsService, authServiceProvider.getObject());
    }
}


