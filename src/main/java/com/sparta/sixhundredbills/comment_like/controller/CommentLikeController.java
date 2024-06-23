package com.sparta.sixhundredbills.comment_like.controller;

import com.sparta.sixhundredbills.auth.security.UserDetailsImpl;
import com.sparta.sixhundredbills.comment_like.dto.CommentLikeRequestDto;
import com.sparta.sixhundredbills.comment_like.dto.CommentLikeResponseDto;
import com.sparta.sixhundredbills.comment_like.service.CommentLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class CommentLikeController {

    private final CommentLikeService commentLikeService;

    /**
     * 댓글에 좋아요를 추가하는 메서드.
     *
     * @param postId 게시물 ID
     * @param commentId 댓글 ID
     * @param commentLikeRequestDto 좋아요 요청 DTO
     * @param userDetails 사용자 정보
     * @return 좋아요 추가 결과를 포함한 ResponseEntity
     */

    @PostMapping("/{postId}/comments/{commentId}/likes")
    public ResponseEntity<Object> likeComment(@PathVariable Long postId, @PathVariable Long commentId,
                                              @RequestBody CommentLikeRequestDto commentLikeRequestDto,
                                              @AuthenticationPrincipal UserDetailsImpl userDetails) {
        // 사용자 인증 여부 확인
        if (userDetails == null) {
            // 인증되지 않은 경우 401 Unauthorized 응답
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "로그인 해주세요."));
        }

        // 요청된 사용자 ID와 인증된 사용자 ID가 일치하는지 확인
        if (!userDetails.getUser().getId().equals(commentLikeRequestDto.getUserId())) {
            // 사용자 정보가 일치하지 않는 경우 403 Forbidden 응답
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "토큰의 사용자 정보와 요청된 사용자 정보가 일치하지 않습니다."));
        }

        try {
            // 서비스 호출하여 댓글에 좋아요 추가, 성공 시 응답 반환
            CommentLikeResponseDto responseDto = commentLikeService.likeComment(postId, commentId, userDetails.getUser());
            // 성공적으로 처리된 경우 201 Created 응답
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
        } catch (Exception e) {
            // 예외 발생 시 에러 응답 반환
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * 댓글의 좋아요를 취소하는 메서드.
     *
     * @param postId 게시물 ID
     * @param commentId 댓글 ID
     * @param commentLikeRequestDto 좋아요 요청 DTO
     * @param userDetails 사용자 정보
     * @return 좋아요 취소 결과를 포함한 ResponseEntity
     */
    @DeleteMapping("/{postId}/comments/{commentId}/likes")
    public ResponseEntity<Object> unlikeComment(@PathVariable Long postId, @PathVariable Long commentId,
                                                @RequestBody CommentLikeRequestDto commentLikeRequestDto,
                                                @AuthenticationPrincipal UserDetailsImpl userDetails) {
        // 사용자 인증 여부 확인
        if (userDetails == null) {
            // 인증되지 않은 경우 401 Unauthorized 응답
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "로그인 해주세요."));
        }

        // 요청된 사용자 ID와 인증된 사용자 ID가 일치하는지 확인
        if (!userDetails.getUser().getId().equals(commentLikeRequestDto.getUserId())) {
            // 사용자 정보가 일치하지 않는 경우 403 Forbidden 응답
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "토큰의 사용자 정보와 요청된 사용자 정보가 일치하지 않습니다."));
        }

        try {
            // 서비스 호출하여 댓글의 좋아요 취소, 성공 시 응답 반환
            CommentLikeResponseDto responseDto = commentLikeService.unlikeComment(postId, commentId, userDetails.getUser());
            // 성공적으로 처리된 경우 201 Created 응답
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
        } catch (Exception e) {
            // 예외 발생 시 에러 응답 반환
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }
}