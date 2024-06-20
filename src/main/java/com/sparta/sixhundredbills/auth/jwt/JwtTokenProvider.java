//package com.sparta.sixhundredbills.auth.jwt;
//
//
//import io.jsonwebtoken.Claims;
//import io.jsonwebtoken.Jws;
//import io.jsonwebtoken.JwtException;
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.SignatureAlgorithm;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.stereotype.Component;
//
//import jakarta.annotation.PostConstruct;
//
//import java.util.Base64;
//import java.util.Collection;
//import java.util.Date;
//import java.util.List;
//import java.util.stream.Collectors;
//
//// JWT 토큰 제공자
//// JWT 토큰 생성, 검증 & 토큰에서 사용자 이름과 역할을 추출하는 기능을 제공하는 클래스.
//// 인증 및 권한 부여 과정에서 중요 역할
//
//
//
//
//
//
//@Component
//public class JwtTokenProvider {
//    private static String secretKey = "secret"; // JWT 토큰을 서명하고 검증하는 데 사용되는 비밀 키 (초기값은 "secret", Base64로 인코딩)
//    private static final long validityInMilliseconds = 3600000; // 토큰의 유효 시간 (1시간)을 밀리초 단위로 설정
//
//    // secretKey를 Base64로 인코딩하여 초기화하는 메서드
//    @PostConstruct
//    protected void init() {
//        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
//    }
//
//    // JWT 토큰을 생성하는 메서드
//
//    // 사용자 이름과 역할을 기반으로 JWT 토큰 생성
//    public static String createToken(String username, List<String> roles) {
//        Claims claims = Jwts.claims().setSubject(username); // 클레임 생성 & 사용자 이름을 subject로 설정
//        claims.put("roles", roles); // 사용자 역할을 클레임에 추가
//
//        Date now = new Date(); // 현재 시간을 나타내는 Date 객체를 생성.
//        Date validity = new Date(now.getTime() + validityInMilliseconds); // 현재 시간에 유효 시간 더해 만료 시간 설정
//
//        // JWT 토큰 생성 및 서명
//        return Jwts.builder()
//                .setClaims(claims) // 클레임 설정
//                .setIssuedAt(now) // 토큰 발행 시간 설정
//                .setExpiration(validity) // 토큰 만료 시간 설정
//                .signWith(SignatureAlgorithm.HS256, secretKey) // 서명 알고리즘과 비밀 키 설정
//                .compact(); // 토큰을 문자열로 반환
//    }
//
//    // JWT 토큰의 유효성을 검증하는 메서드
//    public boolean validateToken(String token) {
//        try {
//            // 토큰을 파싱하여 클레임을 얻음
//            Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
//            // 토큰의 만료 시간이 현재 시간보다 이후인지 확인
//            return !claims.getBody().getExpiration().before(new Date());
//        } catch (JwtException | IllegalArgumentException e) {
//            // 토큰이 만료되었거나 유효하지 않은 경우 예외 발생
//            throw new RuntimeException("만료 또는 유효하지 않은 토큰입니다.");
//        }
//    }
//
//    // 토큰에서 사용자 이름을 추출하는 메서드
//    public String getUsername(String token) {
//        // 토큰을 파싱하여 subject (사용자 이름)를 반환
//        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
//    }
//
//    // 토큰에서 사용자 역할을 추출하여 GrantedAuthority 컬렉션으로 반환하는 메서드
//    public Collection<? extends GrantedAuthority> getRoles(String token) {
//        // 토큰을 파싱하여 클레임을 얻음
//        Claims claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
//        // 클레임에서 roles 리스트를 얻음
//        List<String> roles = claims.get("roles", List.class);
//        // roles 리스트를 SimpleGrantedAuthority 리스트로 변환하여 반환
//        return roles.stream()
//                .map(role -> new SimpleGrantedAuthority(role))
//                .collect(Collectors.toList());
//    }
//}
