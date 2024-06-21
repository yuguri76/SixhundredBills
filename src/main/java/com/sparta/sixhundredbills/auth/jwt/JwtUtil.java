package com.sparta.sixhundredbills.auth.jwt;

import com.sparta.sixhundredbills.exception.ErrorEnum;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.Key;
import java.util.Base64;
import java.util.Date;

/*
 * JWT 관리 유틸리티 클래스

 * createToken, createRefreshToken, createAccessToken 각각 일반 토큰, 리프레시 토큰, 엑세스 토큰을 생성
 * validToken 메서드는 주어진 토큰의 유효성을 검사 및 만료 여부 확인

 * addJwtToCookie, initJwtCookie, addRefreshJwtToCookie, addAccessJwtToCookie 각각 JWT 토큰을 Cookie에 추가하거나 초기화하는 역할

 * substringToken 메서드는 JWT 토큰에서 Bearer 접두사를 제거한 토큰 값을 반환

 * getUserInfoFromToken 메서드는 JWT 토큰에서 사용자 정보(Claims)를 추출

 * getTokenFromRequest, getAccessTokenFromRequest, getRefreshTokenFromRequest 메서드는 HttpServletRequest에서 특정 헤더의 JWT 토큰을 추출

 */

@Component
public class JwtUtil {

    // Header KEY 값
    public static final String ACCESS_TOKEN_HEADER = "AccessToken";
    public static final String REFRESH_TOKEN_HEADER = "RefreshToken";
    public static final String BEARER_PREFIX = "Bearer ";

    // 토큰 만료시간
    private final long TOKEN_TIME = 60 * 30 * 1000L; // 30분
    private final long REFRESH_TOKEN_TIME = 60 * 60 * 24 * 14 * 1000L; // 14일

    // Base64 Encode 한 SecretKey
    @Value("${jwt.secret.key}")
    private String secretKey;
    private Key key;
    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

    // 로그 설정
    public static final Logger logger = LoggerFactory.getLogger("JWT 관련 로그");

    // Bean 초기화 후 실행되는 메서드
    @PostConstruct
    public void init() {
        byte[] bytes = Base64.getDecoder().decode(secretKey);
        key = Keys.hmacShaKeyFor(bytes);
    }

    /**
     * 토큰 생성
     * @param username 사용자 이름
     * @param setExpirationTime 토큰 만료 시간(ms)
     * @return 생성된 JWT 토큰
     */
    public String createToken(String username, long setExpirationTime) {
        Date date = new Date();
        return BEARER_PREFIX +
                Jwts.builder()
                        .setSubject(username) // 사용자 식별자값(ID)
                        .setExpiration(new Date(date.getTime() + setExpirationTime)) // 만료 시간 설정
                        .setIssuedAt(date) // 발급일 설정
                        .signWith(key, signatureAlgorithm) // 서명 설정
                        .compact();
    }

    /**
     * Refresh Token 생성
     * @param username 사용자 이름
     * @return 생성된 Refresh Token
     */
    public String createRefreshToken(String username) {
        return createToken(username, REFRESH_TOKEN_TIME);
    }

    /**
     * Access Token 생성
     * @param username 사용자 이름
     * @return 생성된 Access Token
     */
    public String createAccessToken(String username) {
        return createToken(username, TOKEN_TIME);
    }

    /**
     * JWT 토큰을 Cookie에 추가
     * @param token 추가할 JWT 토큰
     * @param res HttpServletResponse 객체
     * @param headerName Cookie의 헤더 이름
     */
    public void addJwtToCookie(String token, HttpServletResponse res, String headerName) {
        try {
            token = URLEncoder.encode(token, "utf-8").replaceAll("\\+", "%20"); // Cookie Value에는 공백이 불가능해서 encoding 진행

            Cookie cookie = new Cookie(headerName, token); // Name-Value
            cookie.setPath("/");

            // Response 객체에 Cookie 추가
            res.addCookie(cookie);
        } catch (UnsupportedEncodingException e) {
            logger.error(e.getMessage());
        }
    }

    /**
     * 초기화된 JWT Cookie 설정
     * @param res HttpServletResponse 객체
     */
    public void initJwtCookie(HttpServletResponse res) {
        Cookie cookie = new Cookie(REFRESH_TOKEN_HEADER, ""); // Name-Value
        Cookie cookie2 = new Cookie(ACCESS_TOKEN_HEADER, "");
        cookie.setPath("/");
        cookie2.setPath("/");

        // Response 객체에 Cookie 추가
        res.addCookie(cookie);
        res.addCookie(cookie2);
    }

    /**
     * Refresh Token을 Cookie에 추가
     * @param token 추가할 Refresh Token
     * @param res HttpServletResponse 객체
     */
    public void addRefreshJwtToCookie(String token, HttpServletResponse res) {
        addJwtToCookie(token, res, REFRESH_TOKEN_HEADER);
    }

    /**
     * Access Token을 Cookie에 추가
     * @param token 추가할 Access Token
     * @param res HttpServletResponse 객체
     */
    public void addAccessJwtToCookie(String token, HttpServletResponse res) {
        addJwtToCookie(token, res, ACCESS_TOKEN_HEADER);
    }

    /**
     * JWT 토큰의 Bearer 접두사 제거 후 반환
     * @param tokenValue Bearer 접두사가 포함된 JWT 토큰
     * @return Bearer 접두사가 제거된 JWT 토큰
     */
    public String substringToken(String tokenValue) {
        if (StringUtils.hasText(tokenValue) && tokenValue.startsWith(BEARER_PREFIX)) {
            return tokenValue.substring(7); // "Bearer " 접두사 제거
        }
        logger.error("Not Found Token");
        throw new NullPointerException("Not Found Token");
    }

    /**
     * 토큰 유효성 검증
     * @param token 검증할 JWT 토큰
     * @param jwtTokenType JWT 토큰의 종류 (Access 또는 Refresh)
     * @param request HttpServletRequest 객체
     */
    public void validToken(String token, JwtTokenType jwtTokenType, HttpServletRequest request) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
        } catch (SecurityException | MalformedJwtException | SignatureException e) {
            request.setAttribute("NOT_VALID_TOKEN", ErrorEnum.NOT_VALID_TOKEN);
            throw new RuntimeException("유효하지 않는 토큰 오류");
        } catch (ExpiredJwtException e) {
            if (jwtTokenType.equals(JwtTokenType.ACCESS_TOKEN)) {
                request.setAttribute("EXPIRED_TOKEN", ErrorEnum.EXPIRED_TOKEN_VALUE);
                throw new RuntimeException("만료된 토큰 오류");
            } else {
                request.setAttribute("EXPIRED_TOKEN", ErrorEnum.EXPIRED_REFRESH_TOKEN_VALUE);
                throw new RuntimeException("만료된 리프레시 토큰 오류");
            }
        } catch (UnsupportedJwtException | IllegalArgumentException e) {
            throw new RuntimeException("기타");
        }
    }

    /**
     * JWT 토큰에서 사용자 정보(Claims)를 가져옴
     * @param token JWT 토큰
     * @return JWT 토큰에서 추출한 사용자 정보(Claims)
     */
    public Claims getUserInfoFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }

    /**
     * HttpServletRequest에서 Cookie Value로부터 JWT 토큰을 가져옴
     * @param req HttpServletRequest 객체
     * @param headerName Cookie의 헤더 이름
     * @return 추출한 JWT 토큰
     */
    public String getTokenFromRequest(HttpServletRequest req, String headerName) {
        Cookie[] cookies = req.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(headerName)) {
                    try {
                        return URLDecoder.decode(cookie.getValue(), "UTF-8"); // Encode 되어 넘어간 Value 다시 Decode
                    } catch (UnsupportedEncodingException e) {
                        return null;
                    }
                }
            }
        }
        return null;
    }

    /**
     * HttpServletRequest에서 Access Token을 가져옴
     * @param req HttpServletRequest 객체
     * @return 추출한 Access Token
     */
    public String getAccessTokenFromRequest(HttpServletRequest req) {
        return getTokenFromRequest(req, ACCESS_TOKEN_HEADER);
    }

    /**
     * HttpServletRequest에서 Refresh Token을 가져옴
     * @param req HttpServletRequest 객체
     * @return 추출한 Refresh Token
     */
    public String getRefreshTokenFromRequest(HttpServletRequest req) {
        return getTokenFromRequest
                (req, REFRESH_TOKEN_HEADER);
    }
}
