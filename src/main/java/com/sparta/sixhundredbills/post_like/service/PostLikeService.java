package com.sparta.sixhundredbills.post_like.service;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.sixhundredbills.auth.entity.User;
import com.sparta.sixhundredbills.exception.CustomException;
import com.sparta.sixhundredbills.exception.ErrorEnum;
import com.sparta.sixhundredbills.post.entity.Post;
import com.sparta.sixhundredbills.post.repository.PostRepository;
import com.sparta.sixhundredbills.post_like.dto.PostLikeResponseDto;
import com.sparta.sixhundredbills.post_like.entity.PostLike;
import com.sparta.sixhundredbills.post_like.repository.PostLikeRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostLikeService {

    private final PostLikeRepository postLikeRepository;
    private final PostRepository postRepository;
    private final JPAQueryFactory queryFactory;

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
                .content("성공적으로 좋아요를 등록했습니다")
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
                .content("성공적으로 좋아요를 취소했습니다")
                .postId(postId)
                .userId(user.getId())
                .likesCount(postLikeRepository.countByPostId(postId))
                .build();
    }

    @Transactional(readOnly = true)
    public Page<PostLikeResponseDto> getLikedPosts(User user, Pageable pageable) {
        Page<PostLike> likedPosts = postLikeRepository.findAllByUser(user, pageable);
        return likedPosts.map(postLike -> PostLikeResponseDto.builder()
                .content(postLike.getPost().getContent())  // 게시물의 내용을 content에 설정
                .postId(postLike.getPost().getId())
                .userId(user.getId())
                .likesCount(postLikeRepository.countByPostId(postLike.getPost().getId()))
                .build());
    }
}
