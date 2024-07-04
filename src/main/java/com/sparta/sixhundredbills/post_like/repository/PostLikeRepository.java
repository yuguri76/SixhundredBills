package com.sparta.sixhundredbills.post_like.repository;

import com.sparta.sixhundredbills.auth.entity.User;
import com.sparta.sixhundredbills.post.entity.Post;
import com.sparta.sixhundredbills.post_like.entity.PostLike;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * 게시물 좋아요 레포지토리 인터페이스
 */
public interface PostLikeRepository extends JpaRepository<PostLike, Long>, PostLikeRepositoryCustom {
    /**
     * 사용자와 게시물로 좋아요 찾기
     * @param user 사용자 정보
     * @param post 게시물 정보
     * @return 좋아요 정보
     */
    Optional<PostLike> findByUserAndPost(User user, Post post);

    /**
     * 특정 게시물의 좋아요 수 조회
     * @param postId 게시물 ID
     * @return 좋아요 수
     */
    @Query("SELECT COUNT(pl) FROM PostLike pl WHERE pl.post.id = :postId")
    int countByPostId(Long postId);

    /**
     * 사용자별 좋아요한 게시물 조회 (페이징 처리)
     * @param user 사용자 정보
     * @param pageable 페이지 정보
     * @return 좋아요한 게시물 목록 (페이징 처리)
     */
    Page<PostLike> findAllByUser(User user, Pageable pageable);
}