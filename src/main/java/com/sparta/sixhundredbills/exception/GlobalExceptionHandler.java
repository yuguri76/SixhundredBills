package com.sparta.sixhundredbills.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.io.IOException;

@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 유효하지 않은 토큰
     * @param message
     * @return : 401 에러와 오류 메시지 반환
     */
    // 유효하지 않은 토큰
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<String> handleInvalidTokenException(String message) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(message);

    }

    /**
     * InfoNotCorrectedException: 유저정보가 맞지 않을때
     * @param message
     * @return
     */
    @ExceptionHandler(InfoNotCorrectedException.class)
    public ResponseEntity<String> InfoNotCorrectedException(String message) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).contentType(MediaType.APPLICATION_JSON).body(message);
    }

    /**
     * 해당 게시물이 DB에 존재하지 않을 때
     *
     * @return : 클라이언트로 에러 코드와 메시지 반환
     */
    @ExceptionHandler(NotFoundPostException.class)
    public ResponseEntity<String> notFoundPostHandler() {
        return ResponseEntity.status(400).body("해당 게시물은 존재하지 않습니다.");
    }

}
