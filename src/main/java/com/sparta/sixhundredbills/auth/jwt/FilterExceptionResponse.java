package com.sparta.sixhundredbills.auth.jwt;

/**
 * 필터 예외 응답 클래스.
 * 필터에서 발생한 예외에 대한 상태 코드와 메시지를 포함하는 응답 객체를 나타냄.
 */
public class FilterExceptionResponse {

    // HTTP 상태 코드
    private final int statusCode;

    // 예외 메시지
    private final String message;

    /**
     * FilterExceptionResponse 생성자.
     * 상태 코드와 메시지를 초기화.
     * @param statusCode HTTP 상태 코드
     * @param message 예외 메시지
     */
    public FilterExceptionResponse(int statusCode, String message) {
        this.statusCode = statusCode; // 상태 코드 초기화
        this.message = message; // 예외 메시지 초기화
    }

    /**
     * HTTP 상태 코드를 반환.
     * @return HTTP 상태 코드
     */
    public int getStatusCode() {
        return statusCode;
    }

    /**
     * 예외 메시지를 반환.
     * @return 예외 메시지
     */
    public String getMessage() {
        return message;
    }
}
