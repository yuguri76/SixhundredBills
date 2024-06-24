package com.sparta.sixhundredbills.comment.controller;

import com.sparta.sixhundredbills.auth.entity.User;
import com.sparta.sixhundredbills.auth.security.UserDetailsImpl;
import com.sparta.sixhundredbills.comment.dto.CommentDeleteResponseDto;
import com.sparta.sixhundredbills.comment.dto.CommentRequestDto;
import com.sparta.sixhundredbills.comment.dto.CommentResponseDto;
import com.sparta.sixhundredbills.comment.entity.Comment;
import com.sparta.sixhundredbills.comment.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    /**
     * 댓글 생성
     * @param postId 게시물 ID
     * @param requestDto 생성할 댓글 정보
     * @param userDetails 인증된 사용자 정보
     * @return 생성된 댓글의 응답 데이터
     */
    @PostMapping("/{postId}/comments")
    public ResponseEntity<CommentResponseDto> createComment(@PathVariable Long postId,
                                                            @RequestParam(required = false) Long parentCommentId,
                                                            @RequestBody CommentRequestDto requestDto,
                                                            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        CommentResponseDto responseDto;
        if (parentCommentId == null) {
            responseDto = commentService.createComment(postId, null, requestDto, userDetails);
        } else {
            responseDto = commentService.createComment(postId, parentCommentId, requestDto, userDetails);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    /**
     * 댓글 조회
     * @param postId 게시물 ID
     * @param page 페이지 번호
     * @param size 페이지 크기
     * @param sortBy 정렬 기준
     * @return 조회된 댓글의 응답 데이터
     */
    @GetMapping("/{postId}/comments")
    public ResponseEntity<List<CommentResponseDto>> getComments(@PathVariable Long postId,
                                                                @RequestParam(value = "page", defaultValue = "1") int page,
                                                                @RequestParam(value = "size", defaultValue = "5") int size,
                                                                @RequestParam(value = "sortBy", defaultValue = "createdAt") String sortBy) {
        List<CommentResponseDto> response = commentService.getComments(postId, page - 1, size, sortBy);
        return ResponseEntity.ok(response);
    }

    /**
     * 댓글 수정
     * @param postId 게시물 ID
     * @param commentId 댓글 ID
     * @param requestDto 수정할 댓글 정보
     * @param userDetails 인증된 사용자 정보
     * @return 수정된 댓글의 응답 데이터
     */
    @PutMapping("/{postId}/comments/{commentId}")
    public ResponseEntity<CommentResponseDto> updateComment(@PathVariable Long postId,
                                                            @PathVariable Long commentId,
                                                            @RequestBody CommentRequestDto requestDto,
                                                            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        CommentResponseDto responseDto = commentService.updateComment(postId, commentId, requestDto, userDetails);
        return ResponseEntity.ok(responseDto);
    }

    /**
     * 댓글 삭제
     * @param postId 게시물 ID
     * @param commentId 댓글 ID
     * @param userDetails 인증된 사용자 정보
     * @return 삭제된 댓글의 응답 데이터
     */
    @DeleteMapping("/{postId}/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long postId,
                                                @PathVariable Long commentId,
                                                @AuthenticationPrincipal UserDetailsImpl userDetails) {
        commentService.deleteComment(postId, commentId, userDetails);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}