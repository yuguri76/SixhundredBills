package com.sparta.sixhundredbills.comment.controller;

import com.sparta.sixhundredbills.auth.entity.User;
import com.sparta.sixhundredbills.auth.security.UserDetailsImpl;
import com.sparta.sixhundredbills.comment.dto.CommentRequestDto;
import com.sparta.sixhundredbills.comment.dto.CommentResponseDto;
import com.sparta.sixhundredbills.comment.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/post")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    /**
     * 게시물을 생성하는 메서드
     * @param requestDto 댓글 생성 요청 DTO
     * @param postId 인증된 사용자 정보
     * @return 생성된 댓글 정보
     */
    @PostMapping("/{postId}/comment")
    public ResponseEntity<CommentResponseDto> createComment(@PathVariable Long postId, @RequestBody CommentRequestDto requestDto,
                                                @AuthenticationPrincipal UserDetailsImpl userDetails) {
        CommentResponseDto responseDto = commentService.createComment(postId, requestDto, userDetails);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }
}
