package com.sparta.sixhundredbills.post.controller;


import com.sparta.sixhundredbills.auth.entity.User;
import com.sparta.sixhundredbills.auth.security.UserDetailsImpl;
import com.sparta.sixhundredbills.post.dto.PostRequestDto;
import com.sparta.sixhundredbills.post.dto.PostResponseDto;
import com.sparta.sixhundredbills.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    /**
     * 게시물을 생성하는 엔드포인트
     * @param postRequestDto 게시물 요청 데이터
     * @param userDetails 인증된 사용자 정보
     * @return 생성된 게시물의 응답 데이터
     */

    @PostMapping
    public ResponseEntity<PostResponseDto> createPost(@RequestBody PostRequestDto postRequestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        PostResponseDto responseDto = postService.createPost(postRequestDto, userDetails.getUser());
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    /**
     * 게시물을 조회하는 엔드포인트
     * @param page 조회할 페이지 번호
     * @return 페이징된 게시물 응답 데이터
     */
    @GetMapping
    public ResponseEntity<Page<PostResponseDto>> getPosts(@RequestParam int page) {
        int size = 5;
        Page<PostResponseDto> posts = postService.getPosts(page, size);
        return new ResponseEntity<>(posts, HttpStatus.OK);
    }

    /**
     * 게시물을 수정하는 엔드포인트
     * @param postId 수정할 게시물 ID
     * @param postRequestDto 게시물 요청 데이터
     * @param userDetails 인증된 사용자 정보
     * @return 수정된 게시물의 응답 데이터
     */
    @PutMapping("/{postId}")
    public ResponseEntity<PostResponseDto> updatePost(@PathVariable Long postId, @RequestBody PostRequestDto postRequestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        PostResponseDto responseDto = postService.updatePost(postId, postRequestDto, userDetails.getUser());
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    /**
     * 게시물을 삭제하는 엔드포인트
     * @param postId 삭제할 게시물 ID
     * @param userDetails 인증된 사용자 정보
     * @return 응답 상태 코드
     */
    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable Long postId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        postService.deletePost(postId, userDetails.getUser());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}