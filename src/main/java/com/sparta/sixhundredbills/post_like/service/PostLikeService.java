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
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * PostLikeService는 게시물 좋아요 기능을 제공하는 서비스 클래스입니다.
 */
@Service
@RequiredArgsConstructor
public class PostLikeService {

    private final PostLikeRepository postLikeRepository;
    private final PostRepository postRepository;
    private final JPAQueryFactory queryFactory;

    /**
     * 게시물에 좋아요를 등록합니다.
     * @param postId 좋아요를 할 게시물의 ID
     * @param user 좋아요를 등록하는 사용자 정보
     * @return 좋아요 등록 결과를 담은 PostLikeResponseDto 객체
     */
    @Transactional
    public PostLikeResponseDto likePost(Long postId, User user) {
        // 게시물 조회, 게시물이 존재하지 않으면 예외 발생
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorEnum.POST_NOT_FOUND));

        // 사용자가 자신의 게시물을 좋아요 할 수 없도록 예외 처리
        if (post.getUser().getId().equals(user.getId())) {
            throw new CustomException(ErrorEnum.CANNOT_LIKE_OWN_POST);
        }

        // 이미 좋아요를 눌렀는지 확인, 이미 눌렀으면 예외 발생
        Optional<PostLike> postLike = postLikeRepository.findByUserAndPost(user, post);
        if (postLike.isPresent()) {
            throw new CustomException(ErrorEnum.POST_ALREADY_LIKED);
        }

        // 새로운 좋아요 객체 생성 및 저장
        PostLike newLike = PostLike.builder()
                .user(user)
                .post(post)
                .build();

        postLikeRepository.save(newLike);

        // 결과 반환
        return PostLikeResponseDto.builder()
                .content("성공적으로 좋아요를 등록했습니다")
                .postId(postId)
                .userId(user.getId())
                .likesCount(postLikeRepository.countByPostId(postId))
                .build();
    }

    /**
     * 게시물에 대한 좋아요를 취소합니다.
     * @param postId 좋아요를 취소할 게시물의 ID
     * @param user 좋아요를 취소하는 사용자 정보
     * @return 좋아요 취소 결과를 담은 PostLikeResponseDto 객체
     */
    @Transactional
    public PostLikeResponseDto unlikePost(Long postId, User user) {
        // 게시물 조회, 게시물이 존재하지 않으면 예외 발생
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorEnum.POST_NOT_FOUND));

        // 좋아요 정보 조회, 존재하지 않으면 예외 발생
        PostLike postLike = postLikeRepository.findByUserAndPost(user, post)
                .orElseThrow(() -> new CustomException(ErrorEnum.LIKE_NOT_FOUND));

        // 좋아요 삭제
        postLikeRepository.delete(postLike);

        // 결과 반환
        return PostLikeResponseDto.builder()
                .content("성공적으로 좋아요를 취소했습니다")
                .postId(postId)
                .userId(user.getId())
                .likesCount(postLikeRepository.countByPostId(postId))
                .build();
    }

    /**
     * 사용자가 좋아요한 게시물 목록을 페이징 처리하여 조회합니다.
     * @param user 사용자 정보
     * @param pageable 페이지 정보
     * @return 좋아요한 게시물 목록을 담은 Page 객체
     */
    @Transactional(readOnly = true)
    public Page<PostLikeResponseDto> getLikedPosts(User user, Pageable pageable) {
        // 사용자가 좋아요한 게시물 조회
        Page<PostLike> likedPosts = postLikeRepository.findAllByUser(user, pageable);

        // 결과 반환
        return likedPosts.map(postLike -> PostLikeResponseDto.builder()
                .content(postLike.getPost().getContent())  // 게시물의 내용을 content에 설정
                .postId(postLike.getPost().getId())
                .userId(user.getId())
                .likesCount(postLikeRepository.countByPostId(postLike.getPost().getId()))
                .build());
    }
}
