package com.sparta.sixhundredbills.comment.controller;

import com.sparta.sixhundredbills.comment.dto.CommentRequestDto;
import com.sparta.sixhundredbills.comment.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/post")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    // 댓글 작성
    @PostMapping("/{postId}/comment")
    public ResponseEntity<String> createComment(@PathVariable Long postId, @RequestBody CommentRequestDto requestDto) {
        return ResponseEntity.ok(commentService.getCommentsByPostId(postid));
    }
}
