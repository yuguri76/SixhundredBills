package com.sparta.sixhundredbills.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception e) {
        e.printStackTrace(); // 콘솔에 예외 스택 트레이스 출력
        return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

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

    /**
     * InvalidEnteredException : 잘못된 입력값이 들어왔을 때
     * @param e : InvalidEnteredException 예외 발생 메시지
     * @return : 400 에러와 오류 메시지 반환
     * */
    @ExceptionHandler(InvalidEnteredException.class)
    public ResponseEntity<CommonResponse<Void>> invalidEnteredException(InvalidEnteredException e) {
        CommonResponse<Void> response = CommonResponse.<Void>builder()
                .msg(e.getMessage())
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * ResponseStatusException 예외 처리
     * @param  e (HttpStatusCode status, String reason)
     * @return ResponseStatusException 이 호출될 때 사용된 메시지와 상태코드를 반환
     * */
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<String> handleResponseStatusException(ResponseStatusException e) {
        return new ResponseEntity<>(e.getReason(), e.getStatusCode());
    }

}