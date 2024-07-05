package com.sparta.sixhundredbills.post_like.repository;

import com.sparta.sixhundredbills.auth.entity.User;
import com.sparta.sixhundredbills.post_like.entity.PostLike;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * 커스텀 PostLikeRepository 인터페이스
 * 사용자 정의 메서드를 정의합니다.
 */
public interface PostLikeRepositoryCustom {

    /**
     * 사용자에 의해 좋아요 된 게시물 수를 반환
     * @param user 사용자 엔티티
     * @return 좋아요 된 게시물 수
     */

    long countByUser(User user);

    /**
     * 특정 게시물의 좋아요를 모두 삭제
     * @param postId 게시물 ID
     */
    void deleteByPostId(Long postId);

    /**
     * 사용자가 좋아요 한 게시물 페이지네이션 조회
     * @param user 사용자 엔티티
     * @param pageable 페이지 정보
     * @return 좋아요 한 게시물 페이지
     */
    Page<PostLike> findLikedPostsByUser(User user, Pageable pageable);
}