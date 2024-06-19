package com.sparta.sixhundredbills.config;

import com.sparta.sixhundredbills.jwt.JwtAuthenticationEntryPoint;
import com.sparta.sixhundredbills.jwt.JwtAuthenticationFilter;
import com.sparta.sixhundredbills.jwt.JwtTokenFilter;
import com.sparta.sixhundredbills.jwt.JwtTokenProvider;
import com.sparta.sixhundredbills.repository.UserRepository;
import com.sparta.sixhundredbills.security.UserDetailsServiceImpl;
import com.sparta.sixhundredbills.service.AuthService;
import jakarta.servlet.Filter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


// Spring Security를 사용 => 인증 및 인가 매커니즘을 설정
// JWT 기반 보안 필터를 추가하여 HTTP 요청을 보호.


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final AuthService authService; // 인증 서비스
    private final UserDetailsServiceImpl userDetailsService; // 사용자 세부 정보 서비스
    private final JwtTokenProvider jwtTokenProvider; // JWT 토큰 제공자
    private final AuthenticationConfiguration authenticationConfiguration; // Spring Security의 인증 구성을 제공

    // 생성자를 통한 의존성 주입
    public SecurityConfig(AuthService authService, UserDetailsServiceImpl userDetailsService, JwtTokenProvider jwtTokenProvider, AuthenticationConfiguration authenticationConfiguration) {
        this.authService = authService;
        this.userDetailsService = userDetailsService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.authenticationConfiguration = authenticationConfiguration;
    }

    // 보안 필터 체인 설정
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // CSRF 보호를 비활성화합니다.
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 세션 관리를 STATELESS로 설정합니다.
                .authorizeRequests(authorizeRequests -> // 요청에 대한 인가 규칙을 설정합니다.
                        authorizeRequests
                                .requestMatchers("/auth/signup", "/auth/login").permitAll() // "/auth/signup" 및 "/auth/login" 경로는 인증 없이 접근 가능합니다.
                                .requestMatchers(HttpMethod.GET, "/public/**").permitAll() // HTTP GET 요청으로 "/public/**" 경로는 인증 없이 접근 가능합니다.
                                .anyRequest().authenticated() // 그 외 모든 요청은 인증이 필요합니다.
                )
                .exceptionHandling(exceptionHandling -> // 예외 처리 설정
                        exceptionHandling
                                .authenticationEntryPoint(jwtAuthenticationEntryPoint()) // 인증 실패 시 처리할 EntryPoint를 설정합니다.
                                .accessDeniedPage("/") // 권한이 없는 경우 처리할 페이지를 설정합니다.
                )
                .addFilterBefore(jwtTokenFilter(), UsernamePasswordAuthenticationFilter.class) // JwtTokenFilter를 UsernamePasswordAuthenticationFilter 앞에 추가합니다.
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class); // JwtAuthenticationFilter를 UsernamePasswordAuthenticationFilter 앞에 추가합니다.

        return http.build();
    }

    // 비밀번호 암호화에 사용할 PasswordEncoder 빈 설정
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 인증 실패 시 처리할 EntryPoint 설정
    @Bean
    public JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint() {
        return new JwtAuthenticationEntryPoint();
    }

    // JWT 토큰 제공자 빈 설정
    @Bean
    public JwtTokenProvider jwtTokenProvider() {
        return new JwtTokenProvider();
    }

    // JWT 토큰 필터 빈 설정
    @Bean
    public JwtTokenFilter jwtTokenFilter() {
        return new JwtTokenFilter(jwtTokenProvider());
    }

    // JWT 인증 필터 빈 설정
    @Bean
    public Filter jwtAuthenticationFilter() throws Exception {
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(authService);
        filter.setAuthenticationManager(authenticationManager(authenticationConfiguration));
        return (Filter) filter;
    }

    // AuthenticationManager 빈 설정
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    // UserDetailsServiceImpl 빈 설정
    @Bean
    public UserDetailsServiceImpl userDetailsServiceImpl(UserRepository userRepository) {
        return new UserDetailsServiceImpl(userRepository);
    }
}
