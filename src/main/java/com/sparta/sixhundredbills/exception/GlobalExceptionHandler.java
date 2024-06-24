package com.sparta.sixhundredbills.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CommonResponse<Void>> handleException(Exception e) {
        e.printStackTrace(); // 콘솔에 예외 스택 트레이스 출력
        CommonResponse<Void> response = CommonResponse.<Void>builder()
                .msg("Internal Server Error: " + e.getMessage())
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .build();
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * 유효하지 않은 토큰
     * @param e
     * @return : 401 에러와 오류 메시지 반환
     */
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<CommonResponse<Void>> handleInvalidTokenException(UnauthorizedException e) {
        CommonResponse<Void> response = CommonResponse.<Void>builder()
                .msg(e.getMessage())
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .build();
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    /**
     * InfoNotCorrectedException: 유저정보가 맞지 않을때
     * @param e
     * @return
     */
    @ExceptionHandler(InfoNotCorrectedException.class)
    public ResponseEntity<CommonResponse<Void>> handleInfoNotCorrectedException(InfoNotCorrectedException e) {
        CommonResponse<Void> response = CommonResponse.<Void>builder()
                .msg(e.getMessage())
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .build();
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    /**
     * 해당 게시물이 DB에 존재하지 않을 때
     *
     * @return : 클라이언트로 에러 코드와 메시지 반환
     */
    @ExceptionHandler(NotFoundPostException.class)
    public ResponseEntity<CommonResponse<Void>> handleNotFoundPostException(NotFoundPostException e) {
        CommonResponse<Void> response = CommonResponse.<Void>builder()
                .msg("해당 게시물은 존재하지 않습니다.")
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .build();
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * 해당 댓글이 DB에 존재하지 않을 때
     *
     * @return : 클라이언트로 에러 코드와 메시지 반환
     */
    @ExceptionHandler(NotFoundCommentException.class)
    public ResponseEntity<CommonResponse<Void>> handleNotFoundCommentException(NotFoundCommentException e) {
        CommonResponse<Void> response = CommonResponse.<Void>builder()
                .msg(e.getMessage())
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .build();
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * InvalidEnteredException : 잘못된 입력값이 들어왔을 때
     * @param e : InvalidEnteredException 예외 발생 메시지
     * @return : 400 에러와 오류 메시지 반환
     */
    @ExceptionHandler(InvalidEnteredException.class)
    public ResponseEntity<CommonResponse<Void>> handleInvalidEnteredException(InvalidEnteredException e) {
        CommonResponse<Void> response = CommonResponse.<Void>builder()
                .msg(e.getMessage())
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .build();
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * ResponseStatusException 예외 처리
     * @param e (HttpStatusCode status, String reason)
     * @return ResponseStatusException 이 호출될 때 사용된 메시지와 상태코드를 반환
     */
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<CommonResponse<Void>> handleResponseStatusException(ResponseStatusException e) {
        CommonResponse<Void> response = CommonResponse.<Void>builder()
                .msg(e.getReason())
                .statusCode(e.getStatusCode().value())
                .build();
        return new ResponseEntity<>(response, e.getStatusCode());
    }
}