package com.sparta.sixhundredbills.post.controller;


import com.sparta.sixhundredbills.auth.entity.User;
import com.sparta.sixhundredbills.post.dto.PostRequestDto;
import com.sparta.sixhundredbills.post.dto.PostResponseDto;
import com.sparta.sixhundredbills.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/post")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    /**
     * 게시물을 생성하는 메서드
     * @param postRequestDto 게시물 생성 요청 DTO
     * @param user 인증된 사용자 정보
     * @return 생성된 게시물 정보
     */
    @PostMapping
    public ResponseEntity<PostResponseDto> createPost(@RequestBody PostRequestDto postRequestDto, @AuthenticationPrincipal User user) {
        PostResponseDto responseDto = postService.createPost(postRequestDto, user);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    /**
     * 모든 게시물을 페이지네이션하여 조회하는 메서드
     * @param page 페이지 번호
     * @param size 페이지 크기
     * @return 페이지네이션된 게시물 목록
     */
    @GetMapping
    public ResponseEntity<Page<PostResponseDto>> getPosts(@RequestParam int page, @RequestParam int size) {
        Page<PostResponseDto> posts = postService.getPosts(page, size);
        return new ResponseEntity<>(posts, HttpStatus.OK);
    }

    /**
     * 게시물을 수정하는 메서드
     * @param postId 수정할 게시물 ID
     * @param postRequestDto 게시물 수정 요청 DTO
     * @param user 인증된 사용자 정보
     * @return 수정된 게시물 정보
     */
    @PutMapping("/{postId}")
    public ResponseEntity<PostResponseDto> updatePost(@PathVariable Long postId, @RequestBody PostRequestDto postRequestDto, @AuthenticationPrincipal User user) {
        PostResponseDto responseDto = postService.updatePost(postId, postRequestDto, user);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    /**
     * 게시물을 삭제하는 메서드
     * @param postId 삭제할 게시물 ID
     * @param user 인증된 사용자 정보
     * @return 삭제 결과
     */
    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable Long postId, @AuthenticationPrincipal User user) {
        postService.deletePost(postId, user);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}