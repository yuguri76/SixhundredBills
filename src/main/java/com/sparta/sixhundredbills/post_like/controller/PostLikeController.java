package com.sparta.sixhundredbills.post_like.controller;

import com.sparta.sixhundredbills.auth.security.UserDetailsImpl;
import com.sparta.sixhundredbills.exception.CustomException;
import com.sparta.sixhundredbills.post_like.dto.PostLikeRequestDto;
import com.sparta.sixhundredbills.post_like.dto.PostLikeResponseDto;
import com.sparta.sixhundredbills.post_like.service.PostLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostLikeController {

    private final PostLikeService postLikeService;

    /**
     * 게시물에 좋아요를 추가하는 메서드.
     *
     * @param postId 게시물 ID
     * @param postLikeRequestDto 좋아요 요청 DTO
     * @param userDetails 사용자 정보
     * @return 좋아요 추가 결과를 포함한 ResponseEntity
     */

    @PostMapping("/{postId}/likes")
    public ResponseEntity<Object> likePost(@PathVariable Long postId, @RequestBody PostLikeRequestDto postLikeRequestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        // 사용자 인증 여부 확인
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "로그인 해주세요."));
        }

        // 요청된 사용자 ID와 인증된 사용자 ID가 일치하는지 확인
        if (!userDetails.getUser().getId().equals(postLikeRequestDto.getUserId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "토큰의 사용자 정보와 요청된 사용자 정보가 일치하지 않습니다."));
        }

        try {
            // 서비스 호출하여 게시물에 좋아요 추가, 성공 시 응답 반환
            PostLikeResponseDto responseDto = postLikeService.likePost(postId, userDetails.getUser());
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
        } catch (CustomException e) {
            // 예외 발생 시 에러 응답 반환
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * 게시물의 좋아요를 취소하는 메서드.
     *
     * @param postId 게시물 ID
     * @param postLikeRequestDto 좋아요 요청 DTO
     * @param userDetails 사용자 정보
     * @return 좋아요 취소 결과를 포함한 ResponseEntity
     */

    @DeleteMapping("/{postId}/likes")
    public ResponseEntity<Object> unlikePost(@PathVariable Long postId, @RequestBody PostLikeRequestDto postLikeRequestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        // 사용자 인증 여부 확인
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "로그인 해주세요."));
        }

        // 요청된 사용자 ID와 인증된 사용자 ID가 일치하는지 확인
        if (!userDetails.getUser().getId().equals(postLikeRequestDto.getUserId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "토큰의 사용자 정보와 요청된 사용자 정보가 일치하지 않습니다."));
        }

        try {
            // 서비스 호출하여 게시물의 좋아요 취소, 성공 시 응답 반환
            PostLikeResponseDto responseDto = postLikeService.unlikePost(postId, userDetails.getUser());
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
        } catch (CustomException e) {
            // 예외 발생 시 에러 응답 반환
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    //좋아요 갯수 확인
    @GetMapping("/likes")
    public ResponseEntity<Page<PostLikeResponseDto>> getLikedPosts(
            @AuthenticationPrincipal UserDetailsImpl userDetails, Pageable pageable) {
        Page<PostLikeResponseDto> likedPosts = postLikeService.getLikedPosts(userDetails.getUser(), pageable);
        return ResponseEntity.ok(likedPosts);
    }
}