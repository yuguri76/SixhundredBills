package com.sparta.sixhundredbills.auth.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.sixhundredbills.auth.exception.ErrorEnum;
import com.sparta.sixhundredbills.auth.exception.FilterExceptionResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;
import java.util.Enumeration;

/**
 * JWT 인증 진입점 클래스.
 * 인증되지 않은 사용자 요청에 대해 예외를 처리.
 * ErrorEnum을 한 번만 체크하고, 각 예외를 처리하는 로직을 반복적으로 작성하지 않도록 최적화.

 */
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper(); // JSON 직렬화를 위한 ObjectMapper 객체 => 예외 메시지를 JSON 형식으로 반환하는 데 사용.

    /**
     * 인증 예외 처리 메서드.
     * @param request HTTP 요청 객체 => 예외 정보 가져옴
     * @param response HTTP 응답 객체 => 예외 메시지를 클라이언트에게 반환
     * @param authException 인증 예외 객체
     * @throws IOException IO 예외 발생 시
     * @throws ServletException 서블릿 예외 발생 시
     */

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        // 요청의 속성들(예외 정보)을 열거형으로 가져옴
        Enumeration<String> exceptionNames = request.getAttributeNames();

        // 모든 속성들을 반복 처리
        while (exceptionNames.hasMoreElements()) {
            String exception = exceptionNames.nextElement(); // 다음 속성 이름
            ErrorEnum errorEnum = (ErrorEnum) request.getAttribute(exception); // 속성에서 ErrorEnum 객체를 가져옴

            // ErrorEnum 객체가 있는 경우
            if (errorEnum != null) {
                // 에러 응답 객체 생성
                FilterExceptionResponse filterExceptionResponse = new FilterExceptionResponse(errorEnum.getStatusCode(), errorEnum.getMessage());

                // 응답 설정
                response.setStatus(errorEnum.getStatusCode()); // 상태 코드 설정
                response.setCharacterEncoding("UTF-8"); // 응답의 문자 인코딩 설정
                response.getWriter().write(objectMapper.writeValueAsString(filterExceptionResponse)); // 응답에 에러 메시지 작성
            }
        }
    }
}
