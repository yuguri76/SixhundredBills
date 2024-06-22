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
     * @param postId 로 post 조회
     * @param requestDto 로 필요한 정보 입수
     * @param userDetails 에서 user 정보 입수
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
//        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    /**
     * 댓글 조회
     * @param postId 로 post 조회
     * @param page size sortBy 로 페이지네이션
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
     * @param postId 로 post 조회
     * @param commentId 로 comment 조회
     * @param requestDto 로 필요한 정보 입수
     * @param userDetails 에서 user 정보 입수
     * @return 수정된 댓글의 응답 데이터
     */
    @PutMapping("/{postId}/comments/{commentId}")
    public ResponseEntity<CommentResponseDto> updateComment(@PathVariable Long postId,
                                                            @PathVariable Long commentId,
                                                            @RequestBody CommentRequestDto requestDto,
                                                            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        CommentResponseDto responseDto = commentService.updateComment(postId, commentId, requestDto, userDetails);
        //        return new ResponseEntity<>(responseDto, HttpStatus.OK);
        return ResponseEntity.ok(responseDto);
//        return ResponseEntity.badRequest().body(responseDto);
//        return ResponseEntity.status(HttpStatus.MULTI_STATUS).body(responseDto);
    }

    /**
     * 댓글 삭제
     * @param postId 로 post 조회
     * @param commentId 로 comment 조회
     * @param userDetails 에서 user 정보 입수
     * @return 삭제된 댓글의 응답 데이터
     */
    @DeleteMapping("/{postId}/comments/{commentId}")
    public ResponseEntity<String> deleteComemnt(@PathVariable Long postId,
                                                @PathVariable Long commentId,
                                                @AuthenticationPrincipal UserDetailsImpl userDetails) {
        String responseDto = commentService.deleteComment(postId, commentId, userDetails);
        return ResponseEntity.ok(responseDto);
    }
}
