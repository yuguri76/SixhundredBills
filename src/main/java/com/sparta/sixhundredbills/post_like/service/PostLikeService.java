package com.sparta.sixhundredbills.post_like.service;

import com.sparta.sixhundredbills.auth.entity.User;
import com.sparta.sixhundredbills.exception.CustomException;
import com.sparta.sixhundredbills.exception.ErrorEnum;
import com.sparta.sixhundredbills.post.entity.Post;
import com.sparta.sixhundredbills.post.repository.PostRepository;
import com.sparta.sixhundredbills.post_like.dto.PostLikeResponseDto;
import com.sparta.sixhundredbills.post_like.entity.PostLike;
import com.sparta.sixhundredbills.post_like.repository.PostLikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * 게시물 좋아요 서비스 클래스
 */
@Service
@RequiredArgsConstructor
public class PostLikeService {

    private final PostLikeRepository postLikeRepository;
    private final PostRepository postRepository;

    /**
     * 게시물에 좋아요를 추가하는 메서드.
     *
     * @param postId 게시물 ID
     * @param user 사용자 정보
     * @return 좋아요 추가 결과를 포함한 PostLikeResponseDto
     */
    public PostLikeResponseDto likePost(Long postId, User user) {

        // 게시물 존재 여부 확인
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorEnum.POST_NOT_FOUND));

        // 자신이 작성한 게시물에 좋아요를 누를 수 없도록 예외 처리
        if (post.getUser().getId().equals(user.getId())) {
            throw new CustomException(ErrorEnum.CANNOT_LIKE_OWN_POST);
        }

        // 이미 좋아요를 눌렀는지 확인
        Optional<PostLike> postLike = postLikeRepository.findByUserAndPost(user, post);
        if (postLike.isPresent()) {
            throw new CustomException(ErrorEnum.POST_ALREADY_LIKED);
        }

        // 새로운 좋아요 엔티티 생성 및 저장
        PostLike newLike = PostLike.builder()
                .user(user)
                .post(post)
                .build();

        postLikeRepository.save(newLike);

        // 좋아요 추가 결과 반환
        return PostLikeResponseDto.builder()
                .message("성공적으로 좋아요를 등록했습니다")
                .postId(postId)
                .userId(user.getId())
                .likesCount(postLikeRepository.countByPostId(postId))
                .build();
    }

    /**
     * 게시물의 좋아요를 취소하는 메서드.
     *
     * @param postId 게시물 ID
     * @param user 사용자 정보
     * @return 좋아요 취소 결과를 포함한 PostLikeResponseDto
     */
    public PostLikeResponseDto unlikePost(Long postId, User user) {
        // 게시물 존재 여부 확인
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorEnum.POST_NOT_FOUND));

        // 좋아요가 눌린 적이 있는지 확인
        PostLike postLike = postLikeRepository.findByUserAndPost(user, post)
                .orElseThrow(() -> new CustomException(ErrorEnum.LIKE_NOT_FOUND));

        // 좋아요 엔티티 삭제
        postLikeRepository.delete(postLike);

        // 좋아요 취소 결과 반환
        return PostLikeResponseDto.builder()
                .message("성공적으로 좋아요를 취소했습니다")
                .postId(postId)
                .userId(user.getId())
                .likesCount(postLikeRepository.countByPostId(postId))
                .build();
    }
}