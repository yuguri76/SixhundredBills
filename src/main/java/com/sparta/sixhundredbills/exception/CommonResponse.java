package com.sparta.sixhundredbills.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder // 빌더 패턴을 사용하여 객체를 생성하기 위한 Lombok 애노테이션

// REST API에서 클라이언트로 응답을 전송할 때 사용.



public class CommonResponse<T> {
    private String msg; // 응답 메시지를 저장하는 문자열 필드
    private int statusCode; // HTTP 상태 코드를 저장하는 정수 필드

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data; // 응답 데이터를 저장하는 제네릭 타입의 필드


    public CommonResponse(String msg, int statusCode) {
        this.msg = msg;
        this.statusCode = statusCode;
        this.data = null; // 기본값으로 null 설정
    }

    public CommonResponse(String msg, int statusCode, T data) {
        this.msg = msg;
        this.statusCode = statusCode;
        this.data = data;
    }
}