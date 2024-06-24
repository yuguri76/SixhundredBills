package com.sparta.sixhundredbills.post_like.service;

import com.sparta.sixhundredbills.auth.entity.User;
import com.sparta.sixhundredbills.exception.CustomException;
import com.sparta.sixhundredbills.exception.ErrorEnum;
import com.sparta.sixhundredbills.post.entity.Post;
import com.sparta.sixhundredbills.post.repository.PostRepository;
import com.sparta.sixhundredbills.post_like.dto.PostLikeResponseDto;
import com.sparta.sixhundredbills.post_like.entity.PostLike;
import com.sparta.sixhundredbills.post_like.repository.PostLikeRepository;
import jakarta.transaction.Transactional;
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

    @Transactional
    public PostLikeResponseDto likePost(Long postId, User user) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorEnum.POST_NOT_FOUND));

        if (post.getUser().getId().equals(user.getId())) {
            throw new CustomException(ErrorEnum.CANNOT_LIKE_OWN_POST);
        }

        Optional<PostLike> postLike = postLikeRepository.findByUserAndPost(user, post);
        if (postLike.isPresent()) {
            throw new CustomException(ErrorEnum.POST_ALREADY_LIKED);
        }

        PostLike newLike = PostLike.builder()
                .user(user)
                .post(post)
                .build();

        postLikeRepository.save(newLike);

        return PostLikeResponseDto.builder()
                .message("성공적으로 좋아요를 등록했습니다")
                .postId(postId)
                .userId(user.getId())
                .likesCount(postLikeRepository.countByPostId(postId))
                .build();
    }

    @Transactional
    public PostLikeResponseDto unlikePost(Long postId, User user) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorEnum.POST_NOT_FOUND));

        PostLike postLike = postLikeRepository.findByUserAndPost(user, post)
                .orElseThrow(() -> new CustomException(ErrorEnum.LIKE_NOT_FOUND));

        postLikeRepository.delete(postLike);

        return PostLikeResponseDto.builder()
                .message("성공적으로 좋아요를 취소했습니다")
                .postId(postId)
                .userId(user.getId())
                .likesCount(postLikeRepository.countByPostId(postId))
                .build();
    }
}